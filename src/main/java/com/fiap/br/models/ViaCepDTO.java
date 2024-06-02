package com.fiap.br.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ViaCepDTO {
    @Pattern(regexp = "\\d{5}-\\d{3}", message = "Formato de CEP inválido")
    @NotNull
    private String cep;
}
