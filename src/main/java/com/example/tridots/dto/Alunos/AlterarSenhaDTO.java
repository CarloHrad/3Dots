package com.example.tridots.dto.Alunos;

import jakarta.validation.constraints.NotBlank;

public record AlterarSenhaDTO(
        @NotBlank
        String senhaAtual,
        @NotBlank
        String novaSenha
) {}
