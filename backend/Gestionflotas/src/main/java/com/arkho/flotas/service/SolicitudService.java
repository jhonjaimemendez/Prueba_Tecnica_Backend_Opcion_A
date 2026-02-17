package com.arkho.flotas.service;

import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.arkho.flotas.domain.Solicitud;
import com.arkho.flotas.dto.CreateSolicitudResponseDto;
import com.arkho.flotas.dto.PageResponseDto;
import com.arkho.flotas.dto.SolicitudRequestDto;
import com.arkho.flotas.dto.SolicitudResponseDto;
import com.arkho.flotas.dto.UploadUrlResponseDto;
import com.arkho.flotas.event.SolicitudEventPublisher;
import com.arkho.flotas.exception.DuplicateSolicitudException;
import com.arkho.flotas.exception.InvalidSolicitudException;
import com.arkho.flotas.mapper.SolicitudMapper;
import com.arkho.flotas.repository.SolicitudRepository;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class SolicitudService {

	private final SolicitudRepository solicitudRepository;
	private final SolicitudEventPublisher solicitudEventPublisher;
	private final SolicitudMapper solicitudMapper;
	private final S3Presigner s3Presigner;

	@Value("${aws.s3.bucket}")
	private String bucketName;

	public CreateSolicitudResponseDto createSolicitud(SolicitudRequestDto request) {

		if (existPatente(request.getPatente().trim()))

			throw new DuplicateSolicitudException("Patente se encuentea registrada");

		else if (!isValideYear(request.getAnualidad())) {

			throw new InvalidSolicitudException("Año del vehículo no puede ser mayor a la actual");

		} else {

			try {

				Solicitud solicitud = Solicitud.builder().patente(request.getPatente().trim())
						.nombrePropietario(request.getNombrePropietario().trim())
						.emailPropietario(request.getEmailPropietario().trim()).marca(request.getMarca().trim())
						.modelo(request.getModelo().trim()).anualidad(request.getAnualidad()).build();

				Solicitud saved = solicitudRepository.save(solicitud);

				solicitudEventPublisher.publicSolicitudCreate(saved.getId(), saved.getPatente());

				return new CreateSolicitudResponseDto(saved.getId());

			} catch (DataIntegrityViolationException ex) {

				throw new DuplicateSolicitudException("Patente se encuentea registrada");

			}

		}

	}

	public PageResponseDto<SolicitudResponseDto> listSolicitudes(int page, int size) {

		Page<Solicitud> solicitudes = solicitudRepository.findAll(PageRequest.of(page, size));

		Page<SolicitudResponseDto> dtoPage = solicitudes.map(solicitudMapper::toDto);

		return new PageResponseDto<>(dtoPage.getContent(), dtoPage.getNumber(), dtoPage.getSize(),
				dtoPage.getTotalElements(), dtoPage.getTotalPages());
	}

	public SolicitudResponseDto getSolicitudById(UUID id) {

		Solicitud solicitud = solicitudRepository.findById(id)
				.orElseThrow(() -> new InvalidSolicitudException("Solicitud no encontrada"));

		return solicitudMapper.toDto(solicitud);

	}

	public UploadUrlResponseDto generatePresignedUrl(UUID id) {

		solicitudRepository.findById(id).orElseThrow(() -> new InvalidSolicitudException("Solicitud no encontrada"));

		String key = "solicitudes/" + id + "/documento.pdf";

		PutObjectRequest objectRequest = PutObjectRequest
				.builder()
				.bucket(bucketName)
				.key(key)
				.contentType("application/pdf").build();

		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
				.signatureDuration(Duration.ofMinutes(10)).putObjectRequest(objectRequest).build();

		PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

		return new UploadUrlResponseDto(presignedRequest.url().toString());
	}

	// **************************************************************************************************//

	public boolean isValideYear(Integer yearModel) {

		int currentYear = java.time.Year.now().getValue();
		return yearModel != null && yearModel <= currentYear;

	}

	public boolean existPatente(String patente) {

		return solicitudRepository.existsByPatente(patente);
	}

	

}
