package com.example.tridots.model;

import com.example.tridots.enums.StatusPedido;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "idPedido")
    private String idPedido;

    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = false)
    private Aluno aluno;

    @OneToOne
    @JoinColumn(name = "idArquivo", nullable = false)
    private Arquivo arquivo;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    @Embedded
    private Medidas3D medidas;

    @Column(name = "observacao")
    private String observacao;

    @Column(name = "estimativaDias", nullable = false)
    private Integer diasEstimados;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusPedido status;
    @Column(name = "data", nullable = false)
    private LocalDateTime data;

}
