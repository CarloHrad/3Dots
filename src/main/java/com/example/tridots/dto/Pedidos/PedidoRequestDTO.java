package com.example.tridots.dto.Pedidos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PedidoRequestDTO(
        @NotBlank(message = "O conteúdo do comentário é obrigatório")
        @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
        @Valid
        String descricao,

        @NotNull(message = "A altura é obrigatória")
        @Valid
        Double altura,

        @NotNull(message = "A largura é obrigatória")
        @Valid
        Double largura,

        @NotNull(message = "A profundidade é obrigatória")
        @Valid
        Double profundidade,

        @Size(max = 300, message = "A observação deve ter no máximo 300 caracteres")
        String observacao
) {}
