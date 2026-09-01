package com.logimarui.occurrence.api.v2.controller;

import com.logimarui.occurrence.api.common.PagedResponse;
import com.logimarui.occurrence.api.v2.dto.CreateReturnOccurrenceRequest;
import com.logimarui.occurrence.api.v2.dto.OccurrenceResponse;
import com.logimarui.occurrence.api.v2.mapper.OccurrenceApiMapper;
import com.logimarui.occurrence.core.domain.enums.OccurrenceStatus;
import com.logimarui.occurrence.core.domain.enums.OccurrenceType;
import com.logimarui.occurrence.core.domain.model.OccurrenceSearchCriteria;
import com.logimarui.occurrence.core.usecase.ConfirmReturnUseCase;
import com.logimarui.occurrence.core.usecase.CreateReturnOccurrenceUseCase;
import com.logimarui.occurrence.core.usecase.RevertOccurrenceUseCase;
import com.logimarui.occurrence.core.usecase.SearchOccurrencesUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/occurrences")
@RequiredArgsConstructor
@Validated
@Tag(name = "Occurrences", description = "Abertura e acompanhamento de ocorrências internas.")
public class OccurrenceController {
    private final CreateReturnOccurrenceUseCase createReturnOccurrenceUseCase;
    private final ConfirmReturnUseCase confirmReturnUseCase;
    private final RevertOccurrenceUseCase revertOccurrenceUseCase;
    private final SearchOccurrencesUseCase searchOccurrencesUseCase;
    private final OccurrenceApiMapper mapper;

    @PostMapping("/returns")
    @Operation(summary = "Criar ocorrência de devolução")
    public ResponseEntity<OccurrenceResponse> createReturn(
            @RequestBody @Valid CreateReturnOccurrenceRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(
                createReturnOccurrenceUseCase.execute(
                        request.customerId(), request.invoiceNumber(), request.reason(),
                        request.observation(), request.transferPossible()
                )
        ));
    }

    @PostMapping("/{occurrenceId}/confirm-return")
    @Operation(summary = "Confirmar devolução")
    public ResponseEntity<OccurrenceResponse> confirmReturn(
            @PathVariable @Positive Long occurrenceId
    ) {
        return ResponseEntity.ok(mapper.toResponse(confirmReturnUseCase.execute(occurrenceId)));
    }

    @PostMapping("/{occurrenceId}/revert")
    @Operation(summary = "Reverter ocorrência")
    public ResponseEntity<OccurrenceResponse> revert(
            @PathVariable @Positive Long occurrenceId
    ) {
        return ResponseEntity.ok(mapper.toResponse(revertOccurrenceUseCase.execute(occurrenceId)));
    }

    @GetMapping
    @Operation(summary = "Listar ocorrências")
    public ResponseEntity<PagedResponse<OccurrenceResponse>> search(
            @RequestParam(required = false) @Positive Long customerId,
            @RequestParam(required = false) @Positive Long invoiceNumber,
            @RequestParam(required = false) OccurrenceType type,
            @RequestParam(required = false) OccurrenceStatus status,
            @RequestParam(required = false) Boolean problemResolved,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        var criteria = new OccurrenceSearchCriteria(
                customerId, invoiceNumber, type, status, problemResolved
        );
        var result = searchOccurrencesUseCase.execute(criteria, pageable);
        return ResponseEntity.ok(PagedResponse.from(result, mapper::toResponse));
    }
}
