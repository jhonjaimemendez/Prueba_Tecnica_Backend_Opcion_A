package com.arkho.flotas.event;


import java.util.UUID;

public record SolicitudCreadaEvent(
        UUID solicitudId,
        String patente
) {}