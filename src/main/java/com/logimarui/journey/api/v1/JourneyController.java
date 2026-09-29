package com.logimarui.journey.api.v1;

import com.logimarui.journey.api.v1.dto.JlItem;
import com.logimarui.journey.api.v1.dto.JourneyPeriodResponse;
import com.logimarui.journey.api.v1.dto.TiItem;
import com.logimarui.journey.api.v1.dto.TmlItem;
import com.logimarui.journey.api.v1.dto.TrItem;
import com.logimarui.journey.core.JourneyQuery;
import com.logimarui.journey.core.JourneyService;
import com.logimarui.platform.web.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/journey")
@Tag(name = "Journey", description = "Projeções da Jornada sobre procedures operacionais V2")
@ApiResponse(responseCode = "400", description = "Período ou filtro inválido",
        content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
@ApiResponse(responseCode = "401", description = "Autenticação necessária", content = @Content)
@ApiResponse(responseCode = "500", description = "Falha na leitura operacional",
        content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
public class JourneyController {
    private final JourneyService service;

    public JourneyController(JourneyService service) {
        this.service = service;
    }

    @GetMapping("/tml")
    @Operation(summary = "Consultar TML por mapa e colaborador")
    public JourneyPeriodResponse<TmlItem> tml(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @Parameter(description = "ponto ou mpd", required = true) @RequestParam String mode,
            @RequestParam(required = false) Long map,
            @RequestParam(required = false) Long employeeCode,
            @Parameter(description = "motorista ou ajudante") @RequestParam(required = false) String role,
            @Parameter(description = "all, expurged ou not_expurged") @RequestParam(required = false) String expurge
    ) {
        return service.tml(query(from, to, mode, map, employeeCode, role, expurge));
    }

    @GetMapping("/tr")
    @Operation(summary = "Consultar TR por mapa e colaborador")
    public JourneyPeriodResponse<TrItem> tr(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @Parameter(description = "TR não aceita mode") @RequestParam(required = false) String mode,
            @RequestParam(required = false) Long map,
            @RequestParam(required = false) Long employeeCode,
            @Parameter(description = "motorista ou ajudante") @RequestParam(required = false) String role,
            @Parameter(description = "all, expurged ou not_expurged") @RequestParam(required = false) String expurge
    ) {
        if (mode != null) throw new IllegalArgumentException("TR does not support mode");
        return service.tr(query(from, to, null, map, employeeCode, role, expurge));
    }

    @GetMapping("/ti")
    @Operation(summary = "Consultar TI e etapas físicas/financeiras por mapa e colaborador")
    public JourneyPeriodResponse<TiItem> ti(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @Parameter(description = "ponto ou mpd", required = true) @RequestParam String mode,
            @RequestParam(required = false) Long map,
            @RequestParam(required = false) Long employeeCode,
            @Parameter(description = "motorista ou ajudante") @RequestParam(required = false) String role,
            @Parameter(description = "all, expurged ou not_expurged") @RequestParam(required = false) String expurge
    ) {
        return service.ti(query(from, to, mode, map, employeeCode, role, expurge));
    }

    @GetMapping("/jl")
    @Operation(summary = "Consultar Jornada operacional ou laboral por mapa e colaborador")
    public JourneyPeriodResponse<JlItem> jl(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @Parameter(description = "ponto ou mpd", required = true) @RequestParam String mode,
            @RequestParam(required = false) Long map,
            @RequestParam(required = false) Long employeeCode,
            @Parameter(description = "motorista ou ajudante") @RequestParam(required = false) String role,
            @Parameter(description = "all, expurged ou not_expurged") @RequestParam(required = false) String expurge
    ) {
        return service.jl(query(from, to, mode, map, employeeCode, role, expurge));
    }

    private JourneyQuery query(LocalDate from, LocalDate to, String mode, Long map,
                               Long employeeCode, String role, String expurge) {
        return new JourneyQuery(from, to, mode, map, employeeCode, role, expurge);
    }
}
