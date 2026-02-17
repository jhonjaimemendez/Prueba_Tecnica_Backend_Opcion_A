package com.arkho.flotas.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.arkho.flotas.dto.CreateSolicitudResponseDto;
import com.arkho.flotas.dto.PageResponseDto;
import com.arkho.flotas.dto.SolicitudRequestDto;
import com.arkho.flotas.dto.SolicitudResponseDto;
import com.arkho.flotas.dto.UploadUrlResponseDto;
import com.arkho.flotas.service.SolicitudService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/solicitudes")
@RequiredArgsConstructor
@Tag(name = "Solicitudes", description = "API de gestión de solicitudes")
public class SolicitudController {

	private final SolicitudService solicitudService;

	@Operation(
		    summary = "Crear una nueva solicitud",
		    description = "Recibe los datos del propietario y del vehículo, valida las reglas de negocio ")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CreateSolicitudResponseDto createSolicitud(@Valid @RequestBody SolicitudRequestDto request) {

		return solicitudService.createSolicitud(request);
	}

	@Operation(
		    summary = "Listar solicitudes",
		    description = "Retorna el listado paginado de solicitudes registradas en el sistema"
	)
	@GetMapping
	public PageResponseDto<SolicitudResponseDto> listar(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		return solicitudService.listSolicitudes(page, size);
	}
	
	@Operation(
		    summary = "Obtener solicitud por ID",
		    description = "Retorna el detalle completo de una solicitud específica"
	)
	@GetMapping("/{id}")
    public SolicitudResponseDto getSolictudById(@PathVariable UUID id) {

        return solicitudService.getSolicitudById(id);
    }
	
	@Operation(
		    summary = "Generar URL prefirmada para carga de documento",
		    description = "Genera una URL prefirmada de S3 que permite al frontend cargar un documento directamente al bucket sin pasar por el backend"
	)
	@PostMapping("/{id}/documentos/upload-url")
	public UploadUrlResponseDto generarUploadUrl(@PathVariable UUID id) {
	    return solicitudService.generatePresignedUrl(id);
	}

}
