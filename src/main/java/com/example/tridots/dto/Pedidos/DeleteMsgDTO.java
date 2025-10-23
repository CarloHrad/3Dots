package com.example.tridots.dto.Pedidos;

import com.example.tridots.model.Usuario;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

public record DeleteMsgDTO(
        @PathVariable
        @NotBlank
        @Valid
        String pedidoId,

        @NotBlank(message = "O ID da mensagem é obrigatório")
        @Valid
        String mensagemId
) {}
