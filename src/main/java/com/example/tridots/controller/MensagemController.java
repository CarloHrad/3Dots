package com.example.tridots.controller;

import com.example.tridots.OperationCode.OperationCode;
import com.example.tridots.dto.MensagemRequestDTO;
import com.example.tridots.dto.MensagemResponseDTO;
import com.example.tridots.dto.Pedidos.DeleteMsgDTO;
import com.example.tridots.enums.Cargo;
import com.example.tridots.model.Mensagem;
import com.example.tridots.model.Pedido;
import com.example.tridots.model.Usuario;
import com.example.tridots.repository.MensagemRepository;
import com.example.tridots.repository.PedidoRepository;
import com.example.tridots.service.BaseResponse;
import com.example.tridots.service.MensagemService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pedidos/{pedidoId}")
public class MensagemController {

    @Autowired
    private MensagemService mensagemService;
    @Autowired
    MensagemRepository mensagemRepository;
    @Autowired
    PedidoRepository pedidoRepository;

    private static final Logger log = LoggerFactory.getLogger(MensagemController.class);

    @PostMapping("/publicar")
    public ResponseEntity<?> adicionarComentario(
            @PathVariable("pedidoId") String pedidoId, @AuthenticationPrincipal Usuario usuario, @RequestBody MensagemRequestDTO dto) {

        try {
            MensagemResponseDTO msg = mensagemService.enviarMsg(usuario, pedidoId, dto);
            return ResponseEntity.ok(msg);

        } catch (AccessDeniedException accessDeniedException) {
            log.warn("Acesso negado para comentar no pedido {}", pedidoId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new BaseResponse(
                    OperationCode.ACCESS_Denid.getCode(),
                    OperationCode.ACCESS_Denid.getDescription(),
                    null,
                    OperationCode.ACCESS_Denid.getHttpStatus()
            ));
        } catch (EntityNotFoundException entityNotFoundException) {
            log.warn("Pedido ou comentário não encontrado para {}", pedidoId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse(
                    OperationCode.PEDIDO_NotFound.getCode(),
                    OperationCode.PEDIDO_NotFound.getDescription(),
                    null,
                    OperationCode.PEDIDO_NotFound.getHttpStatus()
            ));
        } catch (Exception exception) {
            log.error("Erro inesperado ao adicionar comentário ao pedido {}: {}", pedidoId, exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse(
                    OperationCode.INTERNAL_ServerError.getCode(),
                    OperationCode.INTERNAL_ServerError.getDescription(),
                    null,
                    OperationCode.INTERNAL_ServerError.getHttpStatus()
            ));
        }
    }

    @GetMapping("/listar-por-pedido")
    public ResponseEntity<BaseResponse> listarComentarios(@PathVariable("pedidoId") String pedidoId, Pageable pageable) {
        log.info("Requisitada a listagem de comentários do pedido {}", pedidoId);

        try {
            log.warn("Listando comentários do pedido {}", pedidoId);

            Page<MensagemResponseDTO> mensagens = mensagemService.listarHistoricoMensagem(pedidoId, pageable);

            if (mensagens.isEmpty()) {
                log.warn("Nenhuma mensagem encontrada para o pedido {}.", pedidoId);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new BaseResponse(
                        OperationCode.SUCCESSFUL_Operation.getCode(),
                        OperationCode.SUCCESSFUL_Operation.getDescription(),
                        mensagens,
                        OperationCode.SUCCESSFUL_Operation.getHttpStatus()
                ));
            }

            log.info("Devolvendo comentários do pedido: {}", pedidoId);
            return ResponseEntity.ok(
                    new BaseResponse(
                            OperationCode.SUCCESSFUL_Operation.getCode(),
                            OperationCode.SUCCESSFUL_Operation.getDescription(),
                            mensagens,
                            OperationCode.SUCCESSFUL_Operation.getHttpStatus()
                    )
            );

        } catch (Exception exception) {
            log.error("Erro inesperado ao listar comentários pelo pedido: {}", exception.getMessage(), exception);
            return ResponseEntity.status(OperationCode.INTERNAL_ServerError.getHttpStatus())
                    .body(new BaseResponse(
                            OperationCode.INTERNAL_ServerError.getCode(),
                            OperationCode.INTERNAL_ServerError.getDescription(),
                            null,
                            OperationCode.INTERNAL_ServerError.getHttpStatus()
                    ));
        }
    }

    @DeleteMapping("/{mensagemId}/deletar")
    public ResponseEntity<BaseResponse> deletarComentario(
            @PathVariable("pedidoId") String pedidoId,
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable("mensagemId") String mensagemId) {
        log.info("Requisição para deletar comentário {} do pedido {}", mensagemId, pedidoId);

        try {

            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));

            Mensagem mensagem = mensagemRepository.findById(mensagemId)
                    .orElseThrow(() -> new EntityNotFoundException("Comentário não encontrado"));

            if (!mensagem.getPedido().getIdPedido().equals(pedido.getIdPedido())) {
                return buildResponse(OperationCode.COMMENT_DoesNotBelong);
            }

            if (!mensagem.getAutor().getIdUsuario().equals(usuario.getIdUsuario())
                    && usuario.getCargo() != Cargo.ADMINISTRADOR) {
                return buildResponse(OperationCode.ACCESS_Denid);
            }

            mensagemService.deletarComentario(pedidoId, mensagemId, usuario);

            return ResponseEntity.status(OperationCode.SUCCESSFUL_Operation.getHttpStatus()).body(new BaseResponse(
                    OperationCode.SUCCESSFUL_Operation.getCode(),
                    OperationCode.SUCCESSFUL_Operation.getDescription() + ": Comentário deletado com sucesso",
                    null,
                    OperationCode.SUCCESSFUL_Operation.getHttpStatus()
            ));

        } catch (AccessDeniedException accessDeniedException) {
            log.warn("Acesso negado para deletar comentário");
            return ResponseEntity.status(OperationCode.ACCESS_Denid.getHttpStatus()).body(new BaseResponse(
                    OperationCode.ACCESS_Denid.getCode(),
                    OperationCode.ACCESS_Denid.getDescription(),
                    null,
                    OperationCode.ACCESS_Denid.getHttpStatus()
            ));

        } catch (EntityNotFoundException entityNotFoundException) {
            log.warn("Erro: Entidade inexistente para exclusão de comentário");
            return ResponseEntity.status(OperationCode.ARGUMENT_NotFound.getHttpStatus()).body(new BaseResponse(
                    OperationCode.ARGUMENT_NotFound.getCode(),
                    OperationCode.ARGUMENT_NotFound.getDescription(),
                    null,
                    OperationCode.ARGUMENT_NotFound.getHttpStatus()
            ));

        } catch (IllegalArgumentException illegalArgumentException) {
            log.warn("Valores de requisição inválidos");
            return ResponseEntity.status(OperationCode.INVALID_RequestValue.getHttpStatus()).body(new BaseResponse(
                    OperationCode.INVALID_RequestValue.getCode(),
                    OperationCode.INVALID_RequestValue.getDescription(),
                    null,
                    OperationCode.INVALID_RequestValue.getHttpStatus()
            ));

        } catch (Exception exception) {
            log.warn("Erro interno no servidor");
            return ResponseEntity.status(OperationCode.INTERNAL_ServerError.getHttpStatus()).body(new BaseResponse(
                    OperationCode.INTERNAL_ServerError.getCode(),
                    OperationCode.INTERNAL_ServerError.getDescription(),
                    null,
                    OperationCode.INTERNAL_ServerError.getHttpStatus()
            ));

        }
    }
    private ResponseEntity<BaseResponse> buildResponse(OperationCode code) {
        return ResponseEntity.status(code.getHttpStatus())
                .body(new BaseResponse(code.getCode(), code.getDescription(), null, code.getHttpStatus()));
    }

    private ResponseEntity<BaseResponse> buildResponse(OperationCode code, String extraMessage) {
        return ResponseEntity.status(code.getHttpStatus())
                .body(new BaseResponse(code.getCode(), code.getDescription() + ": " + extraMessage, null, code.getHttpStatus()));
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
