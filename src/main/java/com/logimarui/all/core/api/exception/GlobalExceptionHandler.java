package com.logimarui.all.core.api.exception;

import com.logimarui.all.core.api.exception.cliente.ClienteNaoEncontradoException;
import com.logimarui.all.core.api.exception.motorista.MotoristaNaoEncontradoException;
import com.logimarui.all.core.api.exception.produto.ProdutoNaoEncontradoException;
import com.logimarui.all.core.api.exception.supervisor.SupervisorNaoEncontradoException;
import com.logimarui.all.core.api.exception.vendedor.VendedorNaoEncontradoException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Object> resposta(String msg, int status) {
        Map<String, Object> erro = new HashMap<>();
        erro.put("mensagem", msg);
        erro.put("status", status);
        erro.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(ClienteNaoEncontradoException.class)
    public ResponseEntity<Object> handlerCliente(ClienteNaoEncontradoException ex){
        return resposta(ex.getMessage(), 404);
    }
    @ExceptionHandler(ProdutoNaoEncontradoException.class)
    public ResponseEntity<Object> handlerProduto(ProdutoNaoEncontradoException ex){
        return resposta(ex.getMessage(), 404);
    }
    @ExceptionHandler(MotoristaNaoEncontradoException.class)
    public ResponseEntity<Object> handlerMotorista(MotoristaNaoEncontradoException ex){
        return resposta(ex.getMessage(), 404);
    }
    @ExceptionHandler(VendedorNaoEncontradoException.class)
    public ResponseEntity<Object> handlerVendedor(VendedorNaoEncontradoException ex){
        return resposta(ex.getMessage(), 404);
    }
    @ExceptionHandler(SupervisorNaoEncontradoException.class)
    public ResponseEntity<Object> handlerSupervisor(SupervisorNaoEncontradoException ex){
        return resposta(ex.getMessage(), 404);
    }


}
