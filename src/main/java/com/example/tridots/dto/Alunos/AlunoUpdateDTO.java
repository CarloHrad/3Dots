package com.example.tridots.dto.Alunos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AlunoUpdateDTO(
        @NotBlank
        String nome,
        @NotNull
        @Valid
        Integer semestre
) {}
