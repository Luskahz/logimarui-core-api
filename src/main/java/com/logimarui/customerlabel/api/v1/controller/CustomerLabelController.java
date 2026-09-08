package com.logimarui.customerlabel.api.v1.controller;

import com.logimarui.customerlabel.api.v1.dto.CustomerLabelResponse;
import com.logimarui.customerlabel.core.service.CustomerLabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Validated
@Tag(name = "Customer label", description = "Consulta o rótulo calculado do cliente.")
public class CustomerLabelController {
    private final CustomerLabelService customerLabelService;

    @GetMapping("/{customerId}/label")
    @Operation(summary = "Consultar rótulo do cliente")
    public ResponseEntity<CustomerLabelResponse> getLabel(
            @PathVariable @Positive Long customerId
    ) {
        var label = customerLabelService.findByCustomerId(customerId);
        return ResponseEntity.ok(new CustomerLabelResponse(
                label.customerId(),
                label.label(),
                label.generatedAt()
        ));
    }
}
