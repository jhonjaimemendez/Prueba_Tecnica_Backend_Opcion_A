package com.arkho.flotas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.arkho.flotas.domain.Solicitud;
import com.arkho.flotas.dto.PageResponseDto;
import com.arkho.flotas.dto.SolicitudRequestDto;
import com.arkho.flotas.dto.SolicitudResponseDto;
import com.arkho.flotas.event.SolicitudEventPublisher;
import com.arkho.flotas.exception.InvalidSolicitudException;
import com.arkho.flotas.mapper.SolicitudMapper;
import com.arkho.flotas.repository.SolicitudRepository;
import com.arkho.flotas.service.SolicitudService;




@ExtendWith(MockitoExtension.class)
class GestionflotasApplicationTests {

	@Mock
	private SolicitudRepository solicitudRepository;

	@Mock
	private SolicitudEventPublisher solicitudEventPublisher;

	@InjectMocks
	private SolicitudService solicitudService;
	
	@Mock
	private SolicitudMapper solicitudMapper;

	private SolicitudRequestDto solicitudValidateRequestDto;
	
	@BeforeEach
	void setUp() {
		
		 solicitudValidateRequestDto = new SolicitudRequestDto();
		 solicitudValidateRequestDto.setPatente("ABC123");
		 solicitudValidateRequestDto.setNombrePropietario("Jhon Mendez");
		 solicitudValidateRequestDto.setEmailPropietario("jhonmendez@gmail.com");
		 solicitudValidateRequestDto.setMarca("Chevrolet");
		 solicitudValidateRequestDto.setModelo("Spark");
		 solicitudValidateRequestDto.setAnualidad(Year.now().getValue());
	 }

	
	@Test
    void createSolicitudHappyPath() {

        when(solicitudRepository.existsByPatente(anyString())).thenReturn(false);

        Solicitud mockSavedSolicitudBD = Solicitud.builder()
                .id(UUID.randomUUID())
                .patente("ABC123")
                .anualidad(solicitudValidateRequestDto.getAnualidad())
                .build();

        when(solicitudRepository.save(any(Solicitud.class)))
                .thenReturn(mockSavedSolicitudBD);

        var response = solicitudService.createSolicitud(solicitudValidateRequestDto);

        assertNotNull(response);
        
        verify(solicitudEventPublisher, times(1))
                .publicSolicitudCreate(any(UUID.class), anyString());
    }
	
	 @Test
	 void returnSolicitudWhenExist() {

		    UUID id = UUID.randomUUID();

		    Solicitud solicitud = Solicitud.builder()
		            .id(id)
		            .patente("ABC123")
		            .build();

		    SolicitudResponseDto dto = SolicitudResponseDto.builder()
		            .id(id)
		            .patente("ABC123")
		            .build();

		    when(solicitudRepository.findById(id))
		            .thenReturn(Optional.of(solicitud));

		    when(solicitudMapper.toDto(solicitud))
		            .thenReturn(dto);

		    var result = solicitudService.getSolicitudById(id);

		    assertEquals(id, result.getId());
	    }

	 
	 
	    @Test
	    void throwExceptionWhenSolicitudNotFound() {

	        UUID id = UUID.randomUUID();

	        when(solicitudRepository.findById(id))
	                .thenReturn(Optional.empty());

	        assertThrows(InvalidSolicitudException.class, () ->
	                solicitudService.getSolicitudById(id)
	        );
	    }

	 
	    @Test
	    void returnPaginatedSolicitudesWhenPageIsRequested() {

	        Page<Solicitud> page = new PageImpl<>(List.of(
	                Solicitud.builder().patente("ABC123").build()
	        ));

	        when(solicitudRepository.findAll(any(Pageable.class)))
	                .thenReturn(page);

	        PageResponseDto<SolicitudResponseDto> result =
	                solicitudService.listSolicitudes(0, 10);

	        
	        assertEquals(1, result.getContent().size());
	        assertEquals(0, result.getPage());
	        assertEquals(1, result.getTotalElements());
	    }

}
