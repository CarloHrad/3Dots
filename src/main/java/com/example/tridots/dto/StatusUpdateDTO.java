package com.example.tridots.dto;

import com.example.tridots.enums.StatusPedido;

public record StatusUpdateDTO(
        StatusPedido status,
        Integer diasEstimados
) {}
