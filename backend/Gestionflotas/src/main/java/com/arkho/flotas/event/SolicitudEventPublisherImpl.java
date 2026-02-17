package com.arkho.flotas.event;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class SolicitudEventPublisherImpl implements SolicitudEventPublisher {

    private final SqsClient sqsClient;

    @Value("${aws.sqs.queue-url}")
    private String queueUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void publicSolicitudCreate(UUID solicitudId, String patente) {

        try {

            SolicitudCreadaEvent event =
                    new SolicitudCreadaEvent(solicitudId, patente);

            String messageBody = objectMapper.writeValueAsString(event);

            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(messageBody)
                    .build();

            sqsClient.sendMessage(request);

            log.info("Evento publicado SQS correctamente - ID: {}", solicitudId);

        } catch (Exception e) {
        	
            log.error("Error publicando evento SQS", e);
            throw new RuntimeException("Error publicando evento en SQS", e);
        }
    }

}