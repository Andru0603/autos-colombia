package com.example.Parqueadero.repository;

import com.example.Parqueadero.model.entity.Registro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RegistroRepository extends JpaRepository<Registro, Integer> {

    // Registro activo de una placa (fecha_salida NULL = sigue dentro)
    Optional<Registro> findByVehiculo_PlacaAndFechaSalidaIsNull(String placa);

    // HU04: vehículos dentro, con búsqueda opcional por placa
    List<Registro> findByFechaSalidaIsNullAndVehiculo_PlacaContainingIgnoreCase(String placa);

    // HU05: historial filtrado por placa y rango de fechas
    @Query("SELECT r FROM Registro r " +
            "WHERE r.vehiculo.placa LIKE :placa " +
            "AND r.fechaEntrada BETWEEN :desde AND :hasta " +
            "ORDER BY r.fechaEntrada DESC")
    List<Registro> buscarHistorial(@Param("placa") String placa,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta);
}