package com.arkho.flotas.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudResponseDto {

	private UUID id;
	
	private String patente;

	private String nombrePropietario;

	private String emailPropietario;

	private String marca;

	private String modelo;

	private Integer anualidad;

	private String estado;

	private Instant fechaCreacion;

}
