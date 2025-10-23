package com.example.tridots.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comentario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Mensagem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "idComentario")
    private String idMensagem;

    @ManyToOne
    @JoinColumn(name = "idPedido", nullable = false)
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "idAutor", nullable = false)
    private Usuario autor;

    @Column(name = "mensagem", nullable = false)
    private String conteudo;

    @Column(name = "dataComentario", nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();
}
