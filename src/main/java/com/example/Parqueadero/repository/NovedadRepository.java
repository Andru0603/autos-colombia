package com.example.Parqueadero.repository;

import com.example.Parqueadero.model.entity.Novedad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NovedadRepository extends JpaRepository<Novedad, Integer> {
}