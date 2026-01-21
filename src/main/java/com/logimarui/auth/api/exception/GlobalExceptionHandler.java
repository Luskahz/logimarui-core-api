package com.logimarui.auth.api.exception;


import com.logimarui.auth.core.domain.exception.UserNotFoundException;
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

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> userHandler(UserNotFoundException ex){
        return resposta(ex.getMessage(), 404);
    }
}
