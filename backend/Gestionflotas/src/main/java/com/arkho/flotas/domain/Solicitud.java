package com.arkho.flotas.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa una solicitud de inscripción de vehículo.
 */

@Entity
@Table( name = "solicitudes", uniqueConstraints = @UniqueConstraint(columnNames = "patente"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Solicitud {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, length = 12)
	private String patente;

	@Column(nullable = false, length = 100)
	private String nombrePropietario;

	@Column(nullable = false, length = 150)
	private String emailPropietario;

	@Column(nullable = false, length = 50)
	private String marca;

	@Column(nullable = false, length = 50)
	private String modelo;

	@Column(nullable = false)
	private Integer anualidad;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EstadoSolicitud estado;

	/** ------------------------------------------------------------------------------- 
	 * Estas fechas, por constumbre en los procesos de auditoria que he trabajado permiten saber cuando se creo y 
	 * cuando se modifico para trazabilidad
	 **/
	@Column(nullable = false, updatable = false)
	private Instant fechaCreacion;
	
	@Column(nullable = false)
	private Instant fechaActualizacion;
	
	/** ------------------------------------------------------------------------------- */

	
	/** Con esto voy a evitar que dos procesos modifiquen el mismo registro al tiempo **/
	@Version
	private Long version;
	
	/**
	 * Se adicionan los tiempos antes de guardar la solicitud
	 */
	@PrePersist
	public void prePersist() {
	    Instant now = Instant.now();
	    this.fechaCreacion = now;
	    this.fechaActualizacion = now;
	    this.estado = EstadoSolicitud.RECIBIDA;
	}

	/**
	 * Cuando actualiza modifica la fecha
	 */
	@PreUpdate
	public void preUpdate() {
	    this.fechaActualizacion = Instant.now();
	}

}
