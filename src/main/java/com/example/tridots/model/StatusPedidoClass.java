package com.example.tridots.model;

import com.example.tridots.enums.StatusPedido;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "statuspedido")
public class StatusPedidoClass {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idStatus;

    //MUDAR PRA PEDIDO PEDIDO
    @ManyToOne // ou @OneToOne, dependendo do caso
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;
    private StatusPedido statusPedido;
    private LocalDateTime dataAlteracao;
}
