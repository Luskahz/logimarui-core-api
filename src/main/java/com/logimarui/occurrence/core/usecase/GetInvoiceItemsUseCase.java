package com.logimarui.occurrence.core.usecase;

import com.logimarui.occurrence.core.domain.model.InvoiceItem;
import com.logimarui.occurrence.core.port.repository.OrderReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetInvoiceItemsUseCase {
    private final OrderReadRepository orderReadRepository;

    @Transactional(transactionManager = "readTransactionManager", readOnly = true)
    public Page<InvoiceItem> execute(Long invoiceNumber, Pageable pageable) {
        if (pageable.getPageSize() > 100) {
            throw new IllegalArgumentException("Page size must not exceed 100");
        }
        return orderReadRepository.findInvoiceItems(invoiceNumber, pageable);
    }
}
