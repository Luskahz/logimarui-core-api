package com.logimarui.occurrence.api.v1.mapper;

import com.logimarui.occurrence.api.v1.dto.InvoiceItemResponse;
import com.logimarui.occurrence.api.v1.dto.OrderSummaryResponse;
import com.logimarui.occurrence.api.v1.dto.ReturnAlertContextResponse;
import com.logimarui.occurrence.core.domain.model.InvoiceItem;
import com.logimarui.occurrence.core.domain.model.OrderSummary;
import com.logimarui.occurrence.core.domain.model.ReturnAlertContext;
import org.springframework.stereotype.Component;

@Component
public class OrderReadApiMapper {
    public OrderSummaryResponse toResponse(OrderSummary order) {
        return new OrderSummaryResponse(
                order.orderNumber(), order.invoiceNumber(), order.deliveryDate(),
                order.invoiceIssueDate(), order.orderValue(), order.totalHectoliters()
        );
    }

    public InvoiceItemResponse toResponse(InvoiceItem item) {
        return new InvoiceItemResponse(
                item.productCode(), item.productName(), item.quantity()
        );
    }

    public ReturnAlertContextResponse toResponse(ReturnAlertContext context) {
        return new ReturnAlertContextResponse(
                context.customerId(), context.invoiceNumber(),
                context.orderValue(), context.totalHectoliters()
        );
    }
}
