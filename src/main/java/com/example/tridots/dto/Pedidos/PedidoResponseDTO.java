package com.example.tridots.dto.Pedidos;

import com.example.tridots.dto.ArquivoDTO;

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
