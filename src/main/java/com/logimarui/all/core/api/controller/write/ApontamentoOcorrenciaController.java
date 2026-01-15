package com.logimarui.all.core.api.controller.write;

import com.logimarui.all.core.api.dto.write.ocorrencia.MontarOcorrenciaResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ocorrencia/")
@AllArgsConstructor
public class ApontamentoOcorrenciaController {


    @GetMapping("/{codigo}")
    public MontarOcorrenciaResponseDTO montarPorCliente(
            @PathVariable Long codigo,
            Authentication authentication
    ){
        String usuario = authentication.getName();
        return
    }
}
