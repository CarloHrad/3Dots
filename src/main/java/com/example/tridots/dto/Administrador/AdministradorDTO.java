package com.example.tridots.dto.Administrador;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AdministradorDTO(
        @NotBlank(message = "O email é obrigatório")
        @Size(max = 50, message = "Nome deve ter no máximo 50 caracteres")
        @Email(message = "Email inválido")
        String email,

        @NotBlank(message = "O telefone é obrigatório")
        @Pattern(regexp = "\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}", message = "Telefone deve estar no formato válido " +
                "(ex: (11) 91234-5678 ou 1123456789)")
        String telefone,

        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        @NotBlank(message = "A senha é obrigatório")
        @Size(min = 4, message = "Senha deve possuir no mínimo 4 caracteres")
        String password
) {}
