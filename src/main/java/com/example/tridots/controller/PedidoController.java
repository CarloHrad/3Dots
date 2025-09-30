package com.example.tridots.controller;

import com.example.tridots.OperationCode.OperationCode;
import com.example.tridots.dto.Administrador.StatusUpdateDTO;
import com.example.tridots.dto.Pedidos.PedidoAlunoDTO;
import com.example.tridots.dto.Pedidos.PedidoRequestDTO;
import com.example.tridots.dto.Pedidos.PedidoResponseDTO;
import com.example.tridots.model.Usuario;
import com.example.tridots.service.BaseResponse;
import com.example.tridots.service.PedidoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/pedido")
public class PedidoController {
    @Autowired
    PedidoService pedidoService;

    private static final Logger log = LoggerFactory.getLogger(PedidoController.class);

    @PostMapping(value = "/criar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse> criar(
            @AuthenticationPrincipal Usuario alunoLogin,
            @RequestPart("file") @Valid MultipartFile file,
            @RequestPart("pedido") @Valid PedidoRequestDTO pedidoRequestDTO) throws IOException {

        try {
            String idAluno = alunoLogin.getIdUsuario();
            log.warn("Criação de Pedido requisitado.");
            BaseResponse response = pedidoService.criarPedido(idAluno, file, pedidoRequestDTO);

            return ResponseEntity.status(response.getHttpStatus()).body(response);

        } catch (ResponseStatusException responseStatusException) {
            BaseResponse erro = new BaseResponse(
                    OperationCode.INTERNAL_ServerError.getCode(),
                    OperationCode.INTERNAL_ServerError.getDescription() + ": Erro ao processar Criação de Pedido",
                    null,
                    OperationCode.INTERNAL_ServerError.getHttpStatus()
            );
            return ResponseEntity.status(responseStatusException.getStatusCode()).body(erro);

        } catch (Exception exception) {
            log.error("Erro inesperado ao inserir pedido: {}", exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse(
                    OperationCode.ARGUMENT_Null.getCode(),
                    OperationCode.ARGUMENT_Null.getDescription(),
                    null,
                    OperationCode.ARGUMENT_Null.getHttpStatus()
            ));
        }

    }

    @PostMapping("/{idPedido}/cancelar")
    public ResponseEntity<BaseResponse> cancelarPedido(@AuthenticationPrincipal Usuario alunoLogin, @PathVariable("idPedido") String idPedido) throws AccessDeniedException {
        try {
            pedidoService.cancelarPedido(alunoLogin.getIdUsuario(), idPedido);
            log.warn("Cencelamento do pedido {} requisitado", idPedido);
            return ResponseEntity.noContent().build();

        } catch (AccessDeniedException ex) {
            log.error("Acesso negado ao cancelar pedido {}", idPedido, ex);
            return ResponseEntity.status(OperationCode.ACCESS_Denid.getHttpStatus())
                    .body(new BaseResponse(
                            OperationCode.ACCESS_Denid.getCode(),
                            OperationCode.ACCESS_Denid.getDescription(),
                            null,
                            OperationCode.ACCESS_Denid.getHttpStatus()
                    ));

        } catch (Exception exception) {
            log.error("Erro inesperado ao cancelar material: {}", exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse(
                    OperationCode.ARGUMENT_Null.getCode(),
                    OperationCode.ARGUMENT_Null.getDescription(),
                    null,
                    OperationCode.ARGUMENT_Null.getHttpStatus()
            ));

        }
    }

    @GetMapping("/meus-pedidos")
    public ResponseEntity<?> listarMeusPedidos(@AuthenticationPrincipal Usuario usuario) {
        try {

            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado");
            }

            String idAluno = usuario.getIdUsuario();
            log.warn("Listando histórico de pedidos de usuário");

            List<PedidoResponseDTO> pedidos = pedidoService.listarMeusPedidos(idAluno);

            if (pedidos.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(pedidos);

        } catch (Exception exception) {
            log.error("Erro inesperado ao listar pedidos", exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno no servidor");

        }
    }



}
