package com.arkho.flotas.mapper;

import org.springframework.stereotype.Component;

import com.arkho.flotas.domain.Solicitud;
import com.arkho.flotas.dto.SolicitudResponseDto;

@Component
public class SolicitudMapper {

    public SolicitudResponseDto toDto(Solicitud solicitud) {

        return SolicitudResponseDto.builder()
                .id(solicitud.getId())
                .patente(solicitud.getPatente())
                .nombrePropietario(solicitud.getNombrePropietario())
                .emailPropietario(solicitud.getEmailPropietario())
                .marca(solicitud.getMarca())
                .modelo(solicitud.getModelo())
                .anualidad(solicitud.getAnualidad())
                .estado(solicitud.getEstado().name())
                .fechaCreacion(solicitud.getFechaCreacion())
                .build();
    }
}