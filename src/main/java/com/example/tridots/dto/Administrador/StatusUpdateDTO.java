package com.example.tridots.dto.Administrador;

import com.example.tridots.enums.StatusPedido;

public record StatusUpdateDTO(
        StatusPedido status,
        Integer diasEstimados
) {}
