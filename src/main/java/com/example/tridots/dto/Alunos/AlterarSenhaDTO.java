package com.example.tridots.dto.Alunos;

import jakarta.validation.constraints.NotBlank;

public record AlterarSenhaDTO(
        @NotBlank(message = "Senha atual é obrigatória")
        String senhaAtual,
        @NotBlank(message = "Nova senha é obrigatória")
        String novaSenha
) {}
