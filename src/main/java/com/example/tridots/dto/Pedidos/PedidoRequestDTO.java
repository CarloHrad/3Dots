package com.example.tridots.dto.Pedidos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PedidoRequestDTO(
        @NotBlank(message = "O conteúdo do comentário é obrigatório")
        @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
        String descricao,

        @NotBlank(message = "O conteúdo do comentário é obrigatório")
        @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
        String medidas,

        @Size(max = 300, message = "A descrição deve ter no máximo 300 caracteres")
        String observacao
) {}
