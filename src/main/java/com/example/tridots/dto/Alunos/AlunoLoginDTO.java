package com.example.tridots.dto.Alunos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AlunoLoginDTO(
        @NotBlank(message = "O email é obrigatório")
        @Size(max = 50, message = "Nome deve ter no máximo 50 caracteres")
        @Email(message = "Email inválido")
        @Column(name="emailInstitucional", unique = true)
        String emailInstitucional,

        @Column(nullable = false)
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password
) {}
