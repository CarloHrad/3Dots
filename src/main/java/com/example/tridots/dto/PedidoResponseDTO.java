package com.example.tridots.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PedidoResponseDTO(
        String idPedido,
        ArquivoDTO arquivo,
        String descricao,
        String medidas,
        String observacao,
        Integer diasEstimados,
        LocalDateTime data,
        String status
) {}
