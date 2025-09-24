package com.example.tridots.controller;

import com.example.tridots.dto.*;
import com.example.tridots.model.Aluno;
import com.example.tridots.model.Pedido;
import com.example.tridots.model.Usuario;
import com.example.tridots.service.PedidoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pedido")
public class PedidoController {
    @Autowired
    PedidoService pedidoService;

    private static final Logger log = LoggerFactory.getLogger(PedidoController.class);

    @PostMapping(value = "/criar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PedidoResponseDTO> criar(
            @AuthenticationPrincipal Usuario alunoLogin,
            @RequestPart("file") @Valid MultipartFile file,
            @RequestPart("pedido") @Valid PedidoRequestDTO pedidoRequestDTO) throws IOException {

        String idAluno = alunoLogin.getIdUsuario();
        log.warn("Criação de Pedido requisitado...");
        PedidoResponseDTO response = pedidoService.criarPedido(idAluno, file, pedidoRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{idPedido}/cancelar")
    public ResponseEntity<?> cancelarPedido(@AuthenticationPrincipal Usuario alunoLogin, @PathVariable("idPedido") String idPedido) throws AccessDeniedException {
        pedidoService.cancelarPedido(alunoLogin.getIdUsuario(), idPedido);
        log.warn("Cencelamento do pedido {} requisitado", idPedido);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/meus-pedidos")
    public List<PedidoResponseDTO> listarMeusPedidos(@AuthenticationPrincipal Usuario usuario) {
        String idAluno = usuario.getIdUsuario();
        log.warn("Listando histórico de pedidos de usuário");
        return pedidoService.listarMeusPedidos(idAluno);
    }

    @GetMapping("/listar-pedidos")
    public List<PedidoAlunoDTO> listarTodos() {
        log.warn("Listando histórico de todos os pedidos");
        return pedidoService.listarTodos();
    }

    @PatchMapping("/{idPedido}/status")
    public PedidoResponseDTO updateStatus(@PathVariable("idPedido") String idPedido, @RequestBody @Valid StatusUpdateDTO statusUpdateDTO) {
        log.warn("Requisição para atualizar status de pedido");
        return pedidoService.atualizarStatus(idPedido, statusUpdateDTO);
    }

}
