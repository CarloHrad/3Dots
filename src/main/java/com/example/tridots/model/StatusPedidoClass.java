package com.example.tridots.model;

import com.example.tridots.enums.StatusPedido;
import jakarta.annotation.Resource;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "statuspedido")
public class StatusPedidoClass {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "idStatus")
    private String idStatus;

    @ManyToOne
    @JoinColumn(name = "idPedido", nullable = false)
    private Pedido pedido;

    @Column(name = "statusPedido", nullable = false)
    private StatusPedido statusPedido;

    @Column(name = "dataAlteracao", nullable = false)
    private LocalDateTime dataAlteracao;
}
