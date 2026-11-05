package com.example.Parqueadero.repository;

import com.example.Parqueadero.model.entity.Celda;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CeldaRepository extends JpaRepository<Celda, Integer> {
    Optional<Celda> findFirstByTipoAndEstado(String tipo, String estado);
}