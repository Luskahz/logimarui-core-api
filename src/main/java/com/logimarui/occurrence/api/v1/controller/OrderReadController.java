package com.logimarui.occurrence.api.v1.controller;

import com.logimarui.occurrence.api.common.PagedResponse;
import com.logimarui.occurrence.api.v1.dto.InvoiceItemResponse;
import com.logimarui.occurrence.api.v1.dto.OrderSummaryResponse;
import com.logimarui.occurrence.api.v1.dto.ReturnAlertContextResponse;
import com.logimarui.occurrence.api.v1.mapper.OrderReadApiMapper;
import com.logimarui.occurrence.core.usecase.GetCustomerOrdersUseCase;
import com.logimarui.occurrence.core.usecase.GetInvoiceItemsUseCase;
import com.logimarui.occurrence.core.usecase.GetReturnAlertContextUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
@Tag(name = "Occurrence READ", description = "Consultas operacionais somente leitura para devoluções.")
public class OrderReadController {
    private final GetCustomerOrdersUseCase getCustomerOrdersUseCase;
    private final GetInvoiceItemsUseCase getInvoiceItemsUseCase;
    private final GetReturnAlertContextUseCase getReturnAlertContextUseCase;
    private final OrderReadApiMapper mapper;

    @GetMapping("/customers/{customerId}/orders")
    @Operation(summary = "Consultar pedidos do cliente")
    public ResponseEntity<PagedResponse<OrderSummaryResponse>> getCustomerOrders(
            @PathVariable @Positive Long customerId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            @PageableDefault(
                    size = 20,
                    sort = "deliveryDate",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        var result = getCustomerOrdersUseCase.execute(customerId, date, pageable);
        return ResponseEntity.ok(PagedResponse.from(result, mapper::toResponse));
    }

    @GetMapping("/invoices/{invoiceNumber}/items")
    @Operation(summary = "Consultar itens de uma nota fiscal")
    public ResponseEntity<PagedResponse<InvoiceItemResponse>> getInvoiceItems(
            @PathVariable @Positive Long invoiceNumber,
            @PageableDefault(
                    size = 20,
                    sort = "productCode",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable
    ) {
        var result = getInvoiceItemsUseCase.execute(invoiceNumber, pageable);
        return ResponseEntity.ok(PagedResponse.from(result, mapper::toResponse));
    }

    @GetMapping("/customers/{customerId}/invoices/{invoiceNumber}/return-context")
    @Operation(summary = "Montar contexto do alerta de devolução")
    public ResponseEntity<ReturnAlertContextResponse> getReturnContext(
            @PathVariable @Positive Long customerId,
            @PathVariable @Positive Long invoiceNumber
    ) {
        return ResponseEntity.ok(mapper.toResponse(
                getReturnAlertContextUseCase.execute(customerId, invoiceNumber)
        ));
    }
}
