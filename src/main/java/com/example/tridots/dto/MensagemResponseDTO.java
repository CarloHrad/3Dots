package com.example.tridots.dto;

import java.time.LocalDateTime;

public record MensagemResponseDTO(
        String idMensagem,
        String idPedido,
        String autor,
        String conteudo,
        LocalDateTime dataHora
) {}
