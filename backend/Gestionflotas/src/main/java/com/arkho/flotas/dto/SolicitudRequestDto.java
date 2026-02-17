package com.arkho.flotas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SolicitudRequestDto {

    @NotBlank
    @Size(max = 12)
    @Pattern(regexp = "^[A-Z]{3}[0-9]{4}$")
    private String patente;

    @NotBlank
    @Size(max = 100)
    private String nombrePropietario;

    @NotBlank
    @Email
    @Size(max = 150)
    private String emailPropietario;

    @NotBlank
    @Size(max = 50)
    private String marca;

    @NotBlank
    @Size(max = 50)
    private String modelo;

    @NotNull
    @Min(1900)
    private Integer anualidad;
}
