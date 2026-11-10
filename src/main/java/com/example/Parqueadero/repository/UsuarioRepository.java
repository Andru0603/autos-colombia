package com.example.Parqueadero.repository;

import com.example.Parqueadero.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsername(String username);

    // HU07: validar que el nombre de usuario no exista
    boolean existsByUsername(String username);

    // HU08: lista filtrada por rol ("%" = todos) y texto en nombre, apellido o usuario
    @Query("SELECT u FROM Usuario u " +
            "WHERE u.rol LIKE :rol " +
            "AND (LOWER(u.nombre) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "  OR LOWER(u.apellido) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "  OR LOWER(u.username) LIKE LOWER(CONCAT('%', :q, '%'))) " +
            "ORDER BY u.nombre")
    List<Usuario> buscar(@Param("rol") String rol, @Param("q") String q);
}
