package com.example.Parqueadero.controller;

import com.example.Parqueadero.model.entity.Usuario;
import com.example.Parqueadero.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    public record LoginRequest(String username, String password) {
    }

    @PostMapping("/login")
    public Usuario login(@RequestBody LoginRequest req) {
        return usuarioService.login(req.username(), req.password());
    }
}