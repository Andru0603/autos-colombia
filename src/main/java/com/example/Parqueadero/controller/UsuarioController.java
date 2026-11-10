package com.example.Parqueadero.controller;

import com.example.Parqueadero.model.entity.Usuario;
import com.example.Parqueadero.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Gestión de usuarios (Iteración 2). Solo el administrador puede usarlo.
 * El front envía en el header X-Usuario-Id el id del usuario que inició sesión.
 */
@RestController
@RequestMapping("/usuarios")
@CrossOrigin("*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    public record UsuarioRequest(String nombre, String apellido, String username, String password, String rol) {
    }

    public record EstadoRequest(Boolean activo) {
    }

    @PostMapping
    public Usuario crear(@RequestHeader(value = "X-Usuario-Id", required = false) Integer idSolicitante,
            @RequestBody UsuarioRequest req) {
        return usuarioService.crear(idSolicitante, req.nombre(), req.apellido(),
                req.username(), req.password(), req.rol());
    }

    @GetMapping
    public List<Usuario> listar(@RequestHeader(value = "X-Usuario-Id", required = false) Integer idSolicitante,
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) String q) {
        return usuarioService.listar(idSolicitante, rol, q);
    }

    @PutMapping("/{id}")
    public Usuario editar(@RequestHeader(value = "X-Usuario-Id", required = false) Integer idSolicitante,
            @PathVariable Integer id,
            @RequestBody UsuarioRequest req) {
        // El username no se puede editar; la contraseña es opcional
        return usuarioService.editar(idSolicitante, id, req.nombre(), req.apellido(), req.rol(), req.password());
    }

    @PutMapping("/{id}/estado")
    public Usuario cambiarEstado(@RequestHeader(value = "X-Usuario-Id", required = false) Integer idSolicitante,
            @PathVariable Integer id,
            @RequestBody EstadoRequest req) {
        if (req.activo() == null) {
            throw new RuntimeException("Debe indicar el campo activo (true o false)");
        }
        return usuarioService.cambiarEstado(idSolicitante, id, req.activo());
    }
}
