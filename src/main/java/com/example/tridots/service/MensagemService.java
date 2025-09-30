package com.example.tridots.service;

import com.example.tridots.dto.MensagemRequestDTO;
import com.example.tridots.dto.MensagemResponseDTO;
import com.example.tridots.enums.Cargo;
import com.example.tridots.model.Mensagem;
import com.example.tridots.model.Pedido;
import com.example.tridots.model.Usuario;
import com.example.tridots.repository.MensagemRepository;
import com.example.tridots.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class MensagemService {
    @Autowired
    MensagemRepository mensagemRepository;
    @Autowired
    PedidoRepository pedidoRepository;

    public MensagemResponseDTO enviarMsg(Usuario autor, String pedidoId, MensagemRequestDTO msgDTO) {

        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(() ->
                new RuntimeException("Pedido não encontrado"));

        if (!autor.getIdUsuario().equals(pedido.getAluno().getIdUsuario()) && autor.getCargo() != Cargo.ADMINISTRADOR) {
            throw new RuntimeException("Você não tem permissão para comentar neste pedido");
        }


        Mensagem msg = new Mensagem();
        msg.setAutor(autor);
        msg.setPedido(pedido);
        msg.setConteudo(msgDTO.conteudo());
        msg.setDataHora(LocalDateTime.now());
        mensagemRepository.save(msg);

        return new MensagemResponseDTO(
                msg.getIdMensagem(),
                msg.getPedido().getIdPedido(),
                msg.getAutor().getNome(),
                msg.getConteudo(),
                msg.getDataHora()
        );

    }

    //Deletar comentario
    public void deletarComentario(String pedidoId, String mensagemId, Usuario usuario) {
        Mensagem msg = mensagemRepository.findById(mensagemId).orElseThrow(() -> new RuntimeException(("Comentário inexistente")));

        if (!msg.getPedido().getIdPedido().equals(pedidoId)) {
            throw new RuntimeException(("Comentário não pertence a este pedido"));
        }

        boolean podeDeletar = usuario.getIdUsuario().equals(msg.getAutor().getIdUsuario())
                || usuario.getCargo() == Cargo.ADMINISTRADOR;

        if (!podeDeletar) {throw new RuntimeException("Usuário não autorizado a deletar este comentário");}

        mensagemRepository.deleteById(mensagemId);
        System.out.println("Comentario deletado");

    }
    //Buscar comentario de um usuario

    public Page<MensagemResponseDTO> listarHistoricoMensagem(String pedidoId, Pageable pageable) {
        return mensagemRepository.findByPedidoIdPedidoOrderByDataHora(pedidoId, pageable)
                .map(m -> new MensagemResponseDTO(
                        m.getIdMensagem(),
                        m.getPedido().getIdPedido(),
                        m.getAutor().getNome(),
                        m.getConteudo(),
                        m.getDataHora()
                ));
    }

}
