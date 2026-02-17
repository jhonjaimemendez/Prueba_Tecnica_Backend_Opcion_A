package com.arkho.flotas.event;

import java.util.UUID;

public interface SolicitudEventPublisher {

    void publicSolicitudCreate(UUID solicitudId, String patente);
    
}
