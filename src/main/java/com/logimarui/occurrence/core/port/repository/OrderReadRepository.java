package com.logimarui.occurrence.core.port.repository;

import com.logimarui.occurrence.core.domain.model.InvoiceItem;
import com.logimarui.occurrence.core.domain.model.OrderSummary;
import com.logimarui.occurrence.core.domain.model.ReturnAlertContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

public interface OrderReadRepository {
    Page<OrderSummary> findCustomerOrders(Long customerId, LocalDate date, Pageable pageable);
    Page<InvoiceItem> findInvoiceItems(Long invoiceNumber, Pageable pageable);
    Optional<ReturnAlertContext> findReturnContext(Long customerId, Long invoiceNumber);
}
