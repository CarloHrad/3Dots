package com.example.tridots.dto;

public record AlunoDTO(
        String nome,
        String emailInstitucional,
        String raAluno,
        String curso,
        Integer semestre
) {}
