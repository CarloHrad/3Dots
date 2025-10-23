package com.example.tridots.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MensagemRequestDTO(
        @NotBlank(message = "O conteúdo do comentário é obrigatório")
        @Size(max = 500, message = "O comentário deve ter no máximo 500 caracteres")
        @Valid
        String conteudo
) {}
