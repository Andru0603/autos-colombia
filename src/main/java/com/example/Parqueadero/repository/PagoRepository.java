package com.example.Parqueadero.repository;

import com.example.Parqueadero.model.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Integer> {

    // HU20 y HU21: pagos por documento del cliente ("%" = todos) y rango de fechas
    @Query("SELECT p FROM Pago p " +
            "WHERE p.cliente.documento LIKE :documento " +
            "AND p.fechaPago BETWEEN :desde AND :hasta " +
            "ORDER BY p.fechaPago DESC")
    List<Pago> buscar(@Param("documento") String documento,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta);
}
