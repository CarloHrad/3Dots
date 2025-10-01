package com.example.tridots.model;

import com.example.tridots.enums.StatusMatricula;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DiscriminatorValue("ALUNO")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Aluno extends Usuario {
    @Column(unique = true, nullable = false)
    private String raAluno;
    private String curso;
    private int semestre;
    @Enumerated(EnumType.STRING)
    private StatusMatricula statusMatricula;
}
