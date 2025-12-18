package com.logimarui.core.api.controller.read;


import com.logimarui.core.api.dto.produto.ProdutoResponseDTO;
import com.logimarui.core.api.mapper.produto.ProdutoMapper;
import com.logimarui.core.api.service.ProdutoService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/produtos/")
@AllArgsConstructor
public class ProdutoController {
    private final ProdutoService produtoService;

    @GetMapping("/{codigo}")
    public ProdutoResponseDTO buscar(
            @PathVariable Long codigo,
            Authentication authentication
    ){
        String usuario = authentication.getName();
        return new ProdutoMapper().toResponse(produtoService.buscar(codigo));
    }
}
