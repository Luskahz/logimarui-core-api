package com.logimarui.all.core.api.controller.read;

import com.logimarui.all.core.api.dto.vendedor.VendedorResponseDTO;
import com.logimarui.all.core.api.mapper.vendedor.VendedorMapper;
import com.logimarui.all.core.api.service.VendedorService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/vendedores/")
@AllArgsConstructor
public class VendedoresController {
    private final VendedorService vendedorService;

    @GetMapping("/{codigo}")
    public VendedorResponseDTO buscar(
            @PathVariable Long codigo,
            Authentication authentication
    ){
        String usuario = authentication.getName();
        return new VendedorMapper().toResponse(vendedorService.buscar(codigo));
    }
}
