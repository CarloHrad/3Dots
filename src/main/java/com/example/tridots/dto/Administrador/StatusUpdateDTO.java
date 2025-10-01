package com.example.tridots.dto.Administrador;

import com.example.tridots.enums.StatusPedido;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateDTO(
        @NotNull(message = "O status do pedido é obrigatório")
        StatusPedido status,

        @NotNull(message = "Informe os dias estimados para a conclusão do projeto")
        @Min(value = 1, message = "O semestre deve ser no mínimo 1")
        @Column(name="diasEstimados")
        Integer diasEstimados
) {}
