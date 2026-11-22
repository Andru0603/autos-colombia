package com.example.Parqueadero.repository;

import com.example.Parqueadero.model.entity.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Integer> {

    Optional<Vehiculo> findByPlaca(String placa);

    // Iteración 3: vehículos de un cliente (para calcular el valor de la mensualidad)
    List<Vehiculo> findByCliente_IdCliente(Integer idCliente);
}
