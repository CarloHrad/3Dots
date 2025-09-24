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
    private String idPedido;
    @ManyToOne
    @JoinColumn(name = "id_aluno", nullable = false)
    private Aluno aluno;
    @OneToOne
    @JoinColumn(name = "id_arquivo", nullable = false)
    private Arquivo arquivo;
    private String descricao;
    private String medidas;
    private String observacao;
    private Integer diasEstimados;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusPedido status;
    private LocalDateTime data;
}
