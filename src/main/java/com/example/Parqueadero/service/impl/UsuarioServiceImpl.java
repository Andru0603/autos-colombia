package com.example.Parqueadero.service.impl;

import com.example.Parqueadero.exception.AccesoDenegadoException;
import com.example.Parqueadero.model.entity.Usuario;
import com.example.Parqueadero.repository.UsuarioRepository;
import com.example.Parqueadero.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final Set<String> ROLES = Set.of("OPERARIO", "ADMINISTRADOR");
    private static final int PASSWORD_MINIMA = 8;

    @Autowired
    private UsuarioRepository usuarioRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // ---------- HU01: login (iteración 1, ahora valida que el usuario esté activo) ----------
    @Override
    public Usuario login(String username, String password) {
        Usuario usuario = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario o contraseña incorrectos"));

        if (!passwordEncoder.matches(password, usuario.getPasswordHash())) {
            throw new RuntimeException("Usuario o contraseña incorrectos");
        }
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new RuntimeException("Usuario inactivo. Comuníquese con el administrador");
        }
        return usuario;
    }

    // ---------- RNF8: solo un administrador activo gestiona usuarios y celdas ----------
    @Override
    public void validarAdministrador(Integer idSolicitante) {
        if (idSolicitante == null) {
            throw new AccesoDenegadoException("Debe iniciar sesión como administrador");
        }
        Usuario solicitante = usuarioRepo.findById(idSolicitante)
                .orElseThrow(() -> new AccesoDenegadoException("El usuario que hace la solicitud no existe"));

        boolean esAdminActivo = "ADMINISTRADOR".equals(solicitante.getRol())
                && Boolean.TRUE.equals(solicitante.getActivo());
        if (!esAdminActivo) {
            throw new AccesoDenegadoException("Solo un administrador activo puede realizar esta acción");
        }
    }

    // ---------- HU07: registrar usuario ----------
    @Override
    @Transactional
    public Usuario crear(Integer idSolicitante, String nombre, String apellido,
            String username, String password, String rol) {
        validarAdministrador(idSolicitante);
        exigirTexto(nombre, "El nombre", 50);
        exigirTexto(apellido, "El apellido", 50);
        exigirTexto(username, "El nombre de usuario", 30);
        validarPassword(password);
        String rolNormalizado = normalizarRol(rol);

        String user = username.trim();
        if (usuarioRepo.existsByUsername(user)) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre.trim());
        usuario.setApellido(apellido.trim());
        usuario.setUsername(user);
        usuario.setPasswordHash(passwordEncoder.encode(password)); // RNF6/RNF9: nunca texto plano
        usuario.setRol(rolNormalizado);
        usuario.setActivo(true);
        return usuarioRepo.save(usuario);
    }

    // ---------- HU08: consultar usuarios ----------
    @Override
    public List<Usuario> listar(Integer idSolicitante, String rol, String texto) {
        validarAdministrador(idSolicitante);
        String filtroRol = (rol == null || rol.isBlank()) ? "%" : rol.trim().toUpperCase();
        String filtroTexto = (texto == null) ? "" : texto.trim();
        return usuarioRepo.buscar(filtroRol, filtroTexto);
    }

    // ---------- HU09: editar usuario ----------
    @Override
    @Transactional
    public Usuario editar(Integer idSolicitante, Integer idUsuario, String nombre, String apellido,
            String rol, String nuevaPassword) {
        validarAdministrador(idSolicitante);
        Usuario usuario = buscarPorId(idUsuario);

        exigirTexto(nombre, "El nombre", 50);
        exigirTexto(apellido, "El apellido", 50);
        String nuevoRol = normalizarRol(rol);

        // Evita dejar el sistema sin administradores
        if (usuario.getIdUsuario().equals(idSolicitante) && !usuario.getRol().equals(nuevoRol)) {
            throw new RuntimeException("No puedes cambiar tu propio rol");
        }

        usuario.setNombre(nombre.trim());
        usuario.setApellido(apellido.trim());
        usuario.setRol(nuevoRol);

        // Contraseña opcional: si viene vacía se conserva la actual
        if (nuevaPassword != null && !nuevaPassword.isBlank()) {
            validarPassword(nuevaPassword);
            usuario.setPasswordHash(passwordEncoder.encode(nuevaPassword));
        }
        return usuarioRepo.save(usuario);
    }

    // ---------- HU10: activar / desactivar usuario ----------
    @Override
    @Transactional
    public Usuario cambiarEstado(Integer idSolicitante, Integer idUsuario, boolean activo) {
        validarAdministrador(idSolicitante);
        Usuario usuario = buscarPorId(idUsuario);

        if (!activo && usuario.getIdUsuario().equals(idSolicitante)) {
            throw new RuntimeException("No puedes desactivar tu propio usuario");
        }
        usuario.setActivo(activo);
        return usuarioRepo.save(usuario);
    }

    // ---------- métodos privados ----------
    private Usuario buscarPorId(Integer idUsuario) {
        return usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private void exigirTexto(String valor, String campo, int maximo) {
        if (valor == null || valor.isBlank()) {
            throw new RuntimeException(campo + " es obligatorio");
        }
        if (valor.trim().length() > maximo) {
            throw new RuntimeException(campo + " no puede superar " + maximo + " caracteres");
        }
    }

    private void validarPassword(String password) {
        if (password == null || password.length() < PASSWORD_MINIMA) {
            throw new RuntimeException("La contraseña debe tener mínimo " + PASSWORD_MINIMA + " caracteres");
        }
    }

    private String normalizarRol(String rol) {
        String r = (rol == null) ? "" : rol.trim().toUpperCase();
        if (!ROLES.contains(r)) {
            throw new RuntimeException("Rol inválido. Use OPERARIO o ADMINISTRADOR");
        }
        return r;
    }
}
