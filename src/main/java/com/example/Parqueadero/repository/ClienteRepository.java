package com.example.Parqueadero.repository;

import com.example.Parqueadero.model.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    Optional<Cliente> findByDocumento(String documento);
}
