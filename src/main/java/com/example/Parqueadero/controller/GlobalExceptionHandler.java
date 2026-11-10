package com.example.Parqueadero.controller;

import com.example.Parqueadero.exception.AccesoDenegadoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Errores de negocio y validación -> 400
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> manejar(RuntimeException ex) {
        return ResponseEntity.badRequest().body(Map.of("mensaje", String.valueOf(ex.getMessage())));
    }

    // Falta de permisos (RNF8) -> 403. Spring usa el handler más específico.
    @ExceptionHandler(AccesoDenegadoException.class)
    public ResponseEntity<Map<String, String>> accesoDenegado(AccesoDenegadoException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("mensaje", String.valueOf(ex.getMessage())));
    }
}
