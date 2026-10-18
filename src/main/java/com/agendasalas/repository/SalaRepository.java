package com.agendasalas.repository;

import com.agendasalas.entity.Sala;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SalaRepository extends JpaRepository<Sala, Long> {

    Optional<Sala> findByNumero(Integer numero);

    boolean existsByNumero(Integer numero);
}