package com.example.Parqueadero.repository;

import com.example.Parqueadero.model.entity.Celda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CeldaRepository extends JpaRepository<Celda, Integer> {

    // Iteración 1 (RF3): la entrada de vehículos busca la primera celda LIBRE
    Optional<Celda> findFirstByTipoAndEstado(String tipo, String estado);

    // HU11 / HU13: el número de celda no se puede repetir
    boolean existsByNumero(String numero);

    boolean existsByNumeroAndIdCeldaNot(String numero, Integer idCelda);

    // HU12: resumen de ocupación
    long countByTipoAndEstado(String tipo, String estado);

    // HU12: filtros por tipo y estado ("%" = todos)
    @Query("SELECT c FROM Celda c " +
            "WHERE c.tipo LIKE :tipo AND c.estado LIKE :estado " +
            "ORDER BY c.numero")
    List<Celda> filtrar(@Param("tipo") String tipo, @Param("estado") String estado);
}
