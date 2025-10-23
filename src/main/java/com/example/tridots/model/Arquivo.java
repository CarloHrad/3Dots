package com.example.tridots.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "arquivo")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Arquivo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "idArquivo")
    private String idArquivo;

    @Column(name = "nomeArquivo")
    private String nomeArquivo;

    @Column(name = "tipoArquivo")
    private String tipoArquivo;

    @Column(name = "tamanho")
    private Long tamanho;

    @Column(name = "caminho")
    private String caminho;
}
