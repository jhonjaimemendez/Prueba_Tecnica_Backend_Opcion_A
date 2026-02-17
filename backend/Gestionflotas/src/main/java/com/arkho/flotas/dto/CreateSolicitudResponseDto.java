package com.arkho.flotas.dto;

import java.util.UUID;

public class CreateSolicitudResponseDto {

    private UUID id;

    public CreateSolicitudResponseDto(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}