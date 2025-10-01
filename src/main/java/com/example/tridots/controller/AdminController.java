package com.example.tridots.controller;

import com.example.tridots.OperationCode.OperationCode;
import com.example.tridots.dto.Administrador.StatusUpdateDTO;
import com.example.tridots.dto.Alunos.AlunoResponseDTO;
import com.example.tridots.dto.Pedidos.PedidoAlunoDTO;
import com.example.tridots.dto.Pedidos.PedidoResponseDTO;
import com.example.tridots.model.Administrador;
import com.example.tridots.model.Aluno;
import com.example.tridots.service.AlunoService;
import com.example.tridots.service.BaseResponse;
import com.example.tridots.service.PedidoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    AlunoService alunoService;
    @Autowired
    PedidoService pedidoService;

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    /*Endpoint para administrador atualizar os próprios dados*/

    @GetMapping("/listar-pedidos")
    public ResponseEntity<BaseResponse> listarTodos() {
        log.info("Listando histórico de todos os pedidos");
        try {
            List<PedidoAlunoDTO> pedidos = pedidoService.listarTodos();

            BaseResponse response = new BaseResponse(
                    OperationCode.SUCCESSFUL_Operation.getCode(),
                    OperationCode.SUCCESSFUL_Operation.getDescription(),
                    pedidos,
                    OperationCode.SUCCESSFUL_Operation.getHttpStatus()
            );
            return ResponseEntity.status(response.getHttpStatus()).body(response);

        } catch (Exception ex) {
            log.error("Erro inesperado ao listar pedidos", ex);
            BaseResponse errorResponse = new BaseResponse(
                    OperationCode.INTERNAL_ServerError.getCode(),
                    OperationCode.INTERNAL_ServerError.getDescription(),
                    null,
                    OperationCode.INTERNAL_ServerError.getHttpStatus()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PatchMapping("/{idPedido}/status")
    public ResponseEntity<BaseResponse> updateStatus(@PathVariable("idPedido") String idPedido, @RequestBody @Valid StatusUpdateDTO statusUpdateDTO) {
        log.info("Requisição para atualizar status de pedido");

        try {
            BaseResponse response = pedidoService.atualizarStatus(idPedido, statusUpdateDTO);
            return ResponseEntity.status(response.getHttpStatus()).body(response);

        } catch (AccessDeniedException ex) {
            log.error("Acesso negado ao atualizar status do pedido {}", idPedido, ex);
            BaseResponse errorResponse = new BaseResponse(
                    OperationCode.ACCESS_Denid.getCode(),
                    OperationCode.ACCESS_Denid.getDescription(),
                    null,
                    OperationCode.ACCESS_Denid.getHttpStatus()
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);

        } catch (IllegalArgumentException ex) {
            log.error("Parâmetros inválidos ao atualizar status do pedido {}", idPedido, ex);
            BaseResponse errorResponse = new BaseResponse(
                    OperationCode.INVALID_RequestValue.getCode(),
                    OperationCode.INVALID_RequestValue.getDescription(),
                    null,
                    OperationCode.INVALID_RequestValue.getHttpStatus()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch (Exception ex) {
            log.error("Erro inesperado ao atualizar status do pedido {}", idPedido, ex);
            BaseResponse errorResponse = new BaseResponse(
                    OperationCode.INTERNAL_ServerError.getCode(),
                    OperationCode.INTERNAL_ServerError.getDescription(),
                    null,
                    OperationCode.INTERNAL_ServerError.getHttpStatus()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/get-alunos")
    public ResponseEntity<List<AlunoResponseDTO>> getAlunos() {
        log.info("Lista de contas de alunos requisitada");
        try {
            List<AlunoResponseDTO> lista = alunoService.getAlunos();

            if (lista.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(lista);

        } catch (Exception ex) {
            log.error("Erro inesperado ao listar alunos", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
