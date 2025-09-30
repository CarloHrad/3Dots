package com.example.tridots.dto.Pedidos;

import com.example.tridots.model.Usuario;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

public record DeleteMsgDTO(
        @PathVariable
        String pedidoId,
        @PathVariable
        String mensagemId,
        @AuthenticationPrincipal
        Usuario usuario
) {}
