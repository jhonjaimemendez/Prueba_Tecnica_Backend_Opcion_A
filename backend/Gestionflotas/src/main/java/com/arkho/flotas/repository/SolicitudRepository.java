package com.arkho.flotas.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arkho.flotas.domain.Solicitud;

public interface SolicitudRepository extends JpaRepository<Solicitud, UUID> {

	Optional<Solicitud> findByPatente(String patente);

	boolean existsByPatente(String patente);
}
