package com.example.tridots.controller;

import com.example.tridots.dto.MensagemRequestDTO;
import com.example.tridots.dto.MensagemResponseDTO;
import com.example.tridots.dto.Pedidos.DeleteMsgDTO;
import com.example.tridots.model.Usuario;
import com.example.tridots.service.MensagemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos/{pedidoId}")
public class MensagemController {

    @Autowired
    private MensagemService mensagemService;

    @PostMapping("/publicar")
    public MensagemResponseDTO adicionarComentario(
            @PathVariable("pedidoId") String pedidoId, @AuthenticationPrincipal Usuario usuario, @RequestBody MensagemRequestDTO dto) {

        return mensagemService.enviarMsg(usuario, pedidoId, dto);
    }

    @GetMapping("/listar-por-pedido")
    public Page<MensagemResponseDTO> listarComentarios(@PathVariable("pedidoId") String pedidoId, Pageable pageable) {
        return mensagemService.listarHistoricoMensagem(pedidoId, pageable);
    }

    @DeleteMapping("/deletar")
    public ResponseEntity<Void> deletarComentario(
            @PathVariable("pedidoId") String pedidoId,
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody @Valid DeleteMsgDTO deleteDTO) {
        mensagemService.deletarComentario(pedidoId, deleteDTO.mensagemId(), usuario);
        return ResponseEntity.noContent().build();
    }



}
