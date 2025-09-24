package com.example.tridots.enums;

import jakarta.persistence.Entity;

public enum StatusPedido {
    PENDENTE,
    ACEITO,
    RECUSADO,
    EM_PRODUCAO,
    FINALIZADO,
    CANCELADO
}
