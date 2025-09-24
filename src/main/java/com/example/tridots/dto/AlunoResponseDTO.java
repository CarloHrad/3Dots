package com.example.tridots.dto;

import com.example.tridots.enums.Cargo;
import com.example.tridots.enums.StatusMatricula;

public record AlunoResponseDTO(
        String idUsuario,
        String nome,
        String emailInstitucional,
        String raAluno,
        String curso,
        Integer semestre,
        StatusMatricula statusMatricula,
        Cargo cargo
) {}
