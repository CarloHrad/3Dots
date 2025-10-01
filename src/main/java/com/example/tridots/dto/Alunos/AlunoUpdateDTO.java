package com.example.tridots.dto.Alunos;

import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record AlunoUpdateDTO(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 50, message = "Nome deve ter no máximo 50 caracteres")
        @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s]+$", message = "Nome deve ter apenas letras")
        @Column(name="nome", unique = true)
        String nome,

        @Valid
        @NotNull(message = "O semestre do aluno é obrigatório")
        @Min(value = 1, message = "O semestre deve ser no mínimo 1")
        @Max(value = 10, message = "O semestre deve ser no máximo 10")
        @Column(name="semestre")
        Integer semestre
) {}
