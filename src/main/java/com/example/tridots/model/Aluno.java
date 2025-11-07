package com.example.tridots.model;

import com.example.tridots.enums.Cargo;
import com.example.tridots.enums.StatusMatricula;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DiscriminatorValue("ALUNO")
@Entity
@Table(name = "aluno")
@PrimaryKeyJoinColumn(name = "idUsuario")
@Getter
@Setter
@AllArgsConstructor
public class Aluno extends Usuario {

    @Column(name = "raAluno", unique = true, nullable = false)
    private String raAluno;

    @Column(name = "curso", nullable = false)
    private String curso;

    @Column(name = "semestre", nullable = false)
    private int semestre;

    @Enumerated(EnumType.STRING)
    @Column(name = "statusMatricula", nullable = false)
    private StatusMatricula statusMatricula;

    public Aluno() {
        this.setCargo(Cargo.ALUNO);
        this.setStatusMatricula(StatusMatricula.ATIVA);
    }
}
