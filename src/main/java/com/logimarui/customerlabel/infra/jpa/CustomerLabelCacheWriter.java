package com.logimarui.customerlabel.infra.jpa;

import com.logimarui.customerlabel.infra.jpa.entity.CustomerLabelCacheEntity;
import com.logimarui.customerlabel.infra.jpa.repository.CustomerLabelCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerLabelCacheWriter {
    private final CustomerLabelCacheRepository repository;

    @Transactional
    public void replaceSnapshot(List<CustomerLabelCacheEntity> snapshot) {
        repository.deleteAllInBatch();
        repository.saveAll(snapshot);
    }
}
