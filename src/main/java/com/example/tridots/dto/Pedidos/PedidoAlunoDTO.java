package com.example.tridots.dto.Pedidos;

import com.example.tridots.dto.Alunos.AlunoDTO;
import com.example.tridots.dto.ArquivoDTO;

import java.time.LocalDateTime;

public record PedidoAlunoDTO(
        AlunoDTO alunoDTO,
        String idPedido,
        ArquivoDTO arquivo,
        String descricao,
        String medidas,
        String observacao,
        Integer diasEstimados,
        LocalDateTime data,
        String status
) {}
