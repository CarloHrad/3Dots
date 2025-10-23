package com.example.tridots.controller;

import com.example.tridots.OperationCode.OperationCode;
import com.example.tridots.dto.Administrador.StatusUpdateDTO;
import com.example.tridots.dto.Alunos.AlunoResponseDTO;
import com.example.tridots.dto.Pedidos.PedidoAlunoDTO;
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
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

        } catch (Exception exception) {
            log.error("Erro inesperado ao listar pedidos", exception);
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

        } catch (AccessDeniedException accessDeniedException) {
            log.error("Acesso negado ao atualizar status do pedido {}", idPedido, accessDeniedException);
            BaseResponse errorResponse = new BaseResponse(
                    OperationCode.ACCESS_Denid.getCode(),
                    OperationCode.ACCESS_Denid.getDescription(),
                    null,
                    OperationCode.ACCESS_Denid.getHttpStatus()
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);

        } catch (IllegalArgumentException illegalArgumentException) {
            log.error("Parâmetros inválidos ao atualizar status do pedido {}", idPedido, illegalArgumentException);
            BaseResponse errorResponse = new BaseResponse(
                    OperationCode.INVALID_RequestValue.getCode(),
                    OperationCode.INVALID_RequestValue.getDescription(),
                    null,
                    OperationCode.INVALID_RequestValue.getHttpStatus()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch (Exception exception) {
            log.error("Erro inesperado ao atualizar status do pedido {}", idPedido, exception);
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
    public ResponseEntity<?> getAlunos() {
        log.info("Lista de contas de alunos requisitada");
        try {
            List<AlunoResponseDTO> lista = alunoService.getAlunos();

            if (lista.isEmpty()) {
                log.warn("Nenhum aluno encontrado no sistema.");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                        new BaseResponse(
                                OperationCode.SUCCESSFUL_Operation.getCode(),
                                OperationCode.SUCCESSFUL_Operation.getDescription(),
                                lista,
                                OperationCode.SUCCESSFUL_Operation.getHttpStatus()
                        )
                );
            }

            log.info("Total de alunos encontrados: {}", lista.size());
            return ResponseEntity.ok(
                    new BaseResponse(
                            OperationCode.SUCCESSFUL_Operation.getCode(),
                            OperationCode.SUCCESSFUL_Operation.getDescription(),
                            lista,
                            OperationCode.SUCCESSFUL_Operation.getHttpStatus()
                    )
            );

        } catch (Exception exception) {
            log.error("Erro inesperado ao listar alunos", exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse(
                    OperationCode.INTERNAL_ServerError.getCode(),
                    OperationCode.INTERNAL_ServerError.getDescription(),
                    null,
                    OperationCode.INTERNAL_ServerError.getHttpStatus()
            ));
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse> handlerException(MethodArgumentNotValidException ex) {

        Object target = ex.getBindingResult().getTarget();

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        boolean size = fieldErrors.stream()
                .anyMatch(error -> "Size".equalsIgnoreCase(error.getCode()));

        boolean notblank = fieldErrors.stream()
                .anyMatch(error -> "NotBlank".equalsIgnoreCase(error.getCode())
                        || "NotNull".equalsIgnoreCase(error.getCode()));

        boolean pattern = fieldErrors.stream()
                .anyMatch(error -> "Pattern".equalsIgnoreCase(error.getCode()));

        OperationCode operationCode;
        if (notblank) {
            operationCode = OperationCode.ARGUMENT_Null;
        } else if (size) {
            operationCode = OperationCode.VARIABLE_MAX_CHARACTER;
        } else if (pattern) {
            operationCode = OperationCode.INVALID_RequestValue;
        } else {
            operationCode = OperationCode.INVALID_RequestValue;
        }

        String errorMessage = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("; " + System.lineSeparator()));

        if (errorMessage.isBlank()) {
            errorMessage = "Erro de requisição de pedido";
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse(
                        operationCode.getCode(),
                        errorMessage,
                        null,
                        operationCode.getHttpStatus()
                ));
    }
}
