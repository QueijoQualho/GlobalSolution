package com.fiap.br.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthDTO {
    @Email(regexp = ".+[@].+[\\.].+")
    @Size(min = 2, max = 255, message = "Email inválido")
    @NotNull(message = "O email é obrigatório")
    private String email;

    @NotNull(message = "O senha é obrigatório")
    private String senha;

    @NotBlank(message = "recaptchaToken está vazio")
    private String recaptchaToken;
}