package com.example.tridots.dto.Pedidos;

import com.example.tridots.dto.Alunos.AlunoDTO;
import com.example.tridots.dto.ArquivoDTO;

import java.time.LocalDateTime;

public record PedidoAlunoDTO(
        AlunoDTO alunoDTO,
        String idPedido,
        ArquivoDTO arquivo,
        String descricao,
        Double altura,
        Double largura,
        Double profundidade,
        String observacao,
        Integer diasEstimados,
        LocalDateTime data,
        String status
) {}
