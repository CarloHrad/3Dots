package com.example.tridots.dto.Alunos;

public record AlunoRegisterDTO(
        String nome,
        String emailInstitucional,
        String password,
        String raAluno,
        String curso,
        Integer semestre
) {}
