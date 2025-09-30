package com.example.tridots.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "mensagem")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Mensagem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idMensagem;
    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario autor;
    private String conteudo;
    private LocalDateTime dataHora = LocalDateTime.now();
}
