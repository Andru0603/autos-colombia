package com.example.Parqueadero.repository;

import com.example.Parqueadero.model.entity.Tarifa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TarifaRepository extends JpaRepository<Tarifa, Integer> {
    Optional<Tarifa> findByTipo(String tipo);
}
