package com.example.tridots.model;

import com.example.tridots.enums.StatusMatricula;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "aluno")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Aluno extends Usuario {
    private String raAluno;
    private String curso;
    private int semestre;
    @Enumerated(EnumType.STRING)
    private StatusMatricula statusMatricula;
}
