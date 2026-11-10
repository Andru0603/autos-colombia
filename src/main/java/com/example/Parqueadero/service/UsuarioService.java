package com.example.Parqueadero.service;

import com.example.Parqueadero.model.entity.Usuario;

import java.util.List;

public interface UsuarioService {

    Usuario login(String username, String password); // HU01 (ahora rechaza usuarios inactivos)

    // RNF8: lanza AccesoDenegadoException si quien llama no es un administrador activo
    void validarAdministrador(Integer idSolicitante);

    Usuario crear(Integer idSolicitante, String nombre, String apellido,
            String username, String password, String rol); // HU07

    List<Usuario> listar(Integer idSolicitante, String rol, String texto); // HU08

    Usuario editar(Integer idSolicitante, Integer idUsuario, String nombre, String apellido,
            String rol, String nuevaPassword); // HU09

    Usuario cambiarEstado(Integer idSolicitante, Integer idUsuario, boolean activo); // HU10
}
