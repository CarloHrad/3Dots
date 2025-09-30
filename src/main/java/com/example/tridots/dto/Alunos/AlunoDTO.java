package com.example.tridots.dto.Alunos;

public record AlunoDTO(
        String nome,
        String emailInstitucional,
        String raAluno,
        String curso,
        Integer semestre
) {}
