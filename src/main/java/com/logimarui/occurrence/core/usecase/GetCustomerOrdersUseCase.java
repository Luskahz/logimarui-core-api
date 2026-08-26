package com.logimarui.occurrence.core.usecase;

import com.logimarui.occurrence.core.domain.model.OrderSummary;
import com.logimarui.occurrence.core.port.repository.OrderReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GetCustomerOrdersUseCase {
    private final OrderReadRepository orderReadRepository;

    @Transactional(transactionManager = "readTransactionManager", readOnly = true)
    public Page<OrderSummary> execute(Long customerId, LocalDate date, Pageable pageable) {
        validatePageSize(pageable);
        return orderReadRepository.findCustomerOrders(customerId, date, pageable);
    }

    private void validatePageSize(Pageable pageable) {
        if (pageable.getPageSize() > 100) {
            throw new IllegalArgumentException("Page size must not exceed 100");
        }
    }
}
