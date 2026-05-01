package com.logimarui.authorization.api.dto;

import com.logimarui.authorization.core.domain.enums.ModuleCode;
import com.logimarui.authorization.core.domain.enums.PermissionCode;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de uma permissão")
public record PermissionResponseDTO(

        @Schema(description = "Identificador único da permissão", example = "1")
        Long id,

        @Schema(description = "Código técnico da permissão", example = "AUTHZ_ROLE_CREATE")
        PermissionCode code,

        @Schema(description = "Descrição funcional da permissão", example = "Permite criar papéis de acesso")
        String description,

        @Schema(description = "Módulo ao qual a permissão pertence", example = "AUTHORIZATION")
        ModuleCode module
) {
}