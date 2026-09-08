package com.logimarui.customerlabel.core.service;

import com.logimarui.customerlabel.core.domain.model.CustomerLabel;
import com.logimarui.customerlabel.core.exception.CustomerLabelNotFoundException;
import com.logimarui.customerlabel.infra.jdbc.CustomerLabelProcedureReader;
import com.logimarui.customerlabel.infra.jpa.CustomerLabelCacheWriter;
import com.logimarui.customerlabel.infra.jpa.entity.CustomerLabelCacheEntity;
import com.logimarui.customerlabel.infra.jpa.repository.CustomerLabelCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class CustomerLabelService {
    private static final Duration CACHE_MAX_AGE = Duration.ofDays(1);
    private static final ZoneId BUSINESS_ZONE = ZoneId.of("America/Sao_Paulo");

    private final CustomerLabelCacheRepository cacheRepository;
    private final CustomerLabelProcedureReader procedureReader;
    private final CustomerLabelCacheWriter cacheWriter;
    private final ReentrantLock refreshLock = new ReentrantLock();

    public CustomerLabel findByCustomerId(Long customerId) {
        ensureFreshCache();

        CustomerLabelCacheEntity cachedLabel = cacheRepository.findById(customerId)
                .orElseThrow(() -> new CustomerLabelNotFoundException(customerId));

        return new CustomerLabel(
                cachedLabel.getCustomerId(),
                cachedLabel.getLabel(),
                cachedLabel.getGeneratedAt()
        );
    }

    private void ensureFreshCache() {
        Instant now = Instant.now();
        if (isFresh(cacheRepository.findFirstByOrderByGeneratedAtDesc().map(
                CustomerLabelCacheEntity::getGeneratedAt
        ).orElse(null), now)) {
            return;
        }

        refreshLock.lock();
        try {
            Instant latestGeneratedAt = cacheRepository.findFirstByOrderByGeneratedAtDesc()
                    .map(CustomerLabelCacheEntity::getGeneratedAt)
                    .orElse(null);
            if (isFresh(latestGeneratedAt, now)) {
                return;
            }

            Instant generatedAt = Instant.now();
            LocalDate endDate = LocalDate.now(BUSINESS_ZONE);
            List<CustomerLabelCacheEntity> snapshot = procedureReader.readSnapshot(
                    endDate.minusMonths(3),
                    endDate,
                    generatedAt
            );
            if (snapshot.isEmpty()) {
                throw new IllegalStateException(
                        "A procedure sp_cluster_clientes não retornou rótulos de clientes."
                );
            }
            cacheWriter.replaceSnapshot(snapshot);
        } finally {
            refreshLock.unlock();
        }
    }

    private boolean isFresh(Instant generatedAt, Instant now) {
        return generatedAt != null && generatedAt.plus(CACHE_MAX_AGE).isAfter(now);
    }
}
