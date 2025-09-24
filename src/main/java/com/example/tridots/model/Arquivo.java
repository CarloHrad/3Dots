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
    private String idArquivo;
    //id_arquivo
    private String nomeArquivo;
    private String tipoArquivo;
    private Long tamanho;
    private String caminho;
}
