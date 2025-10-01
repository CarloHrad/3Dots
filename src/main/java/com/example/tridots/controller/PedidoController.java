package com.example.tridots.controller;

import com.example.tridots.OperationCode.OperationCode;
import com.example.tridots.dto.Administrador.StatusUpdateDTO;
import com.example.tridots.dto.Pedidos.PedidoAlunoDTO;
import com.example.tridots.dto.Pedidos.PedidoRequestDTO;
import com.example.tridots.dto.Pedidos.PedidoResponseDTO;
import com.example.tridots.model.Pedido;
import com.example.tridots.model.Usuario;
import com.example.tridots.repository.PedidoRepository;
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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pedido")
public class PedidoController {
    @Autowired
    PedidoService pedidoService;
    @Autowired
    PedidoRepository pedidoRepository;

    private static final Logger log = LoggerFactory.getLogger(PedidoController.class);

    @PostMapping(value = "/criar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse> criar(
            @AuthenticationPrincipal Usuario alunoLogin,
            @RequestPart("file") @Valid MultipartFile file,
            @RequestPart("pedido") @Valid PedidoRequestDTO pedidoRequestDTO) throws IOException {

        try {
            String idAluno = alunoLogin.getIdUsuario();
            log.info("Criação de Pedido requisitado.");
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
            if (alunoLogin == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BaseResponse(
                        OperationCode.UNAUTHORIZED.getCode(),
                        OperationCode.UNAUTHORIZED.getDescription(),
                        null,
                        OperationCode.UNAUTHORIZED.getHttpStatus()
                ));
            }

            Optional<Pedido> pedidoOpt = pedidoRepository.findById(idPedido);
            if (pedidoOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new BaseResponse(
                                OperationCode.PEDIDO_NotFound.getCode(),
                                OperationCode.PEDIDO_NotFound.getDescription(),
                                null,
                                OperationCode.PEDIDO_NotFound.getHttpStatus()
                        )
                );
            }

            pedidoService.cancelarPedido(alunoLogin.getIdUsuario(), idPedido);
            log.info("Cencelamento do pedido {} requisitado", idPedido);

            return ResponseEntity.status(OperationCode.SUCCESSFUL_Operation.getHttpStatus()).body(
                    new BaseResponse(
                            OperationCode.SUCCESSFUL_Operation.getCode(),
                            OperationCode.SUCCESSFUL_Operation.getDescription() + ": Pedido cancelado com sucesso",
                            null,
                            OperationCode.SUCCESSFUL_Operation.getHttpStatus()
                    )
            );


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
            log.error("Erro inesperado ao cancelar pedido: {}", exception.getMessage(), exception);
            return ResponseEntity.status(OperationCode.INTERNAL_ServerError.getHttpStatus())
                    .body(new BaseResponse(
                            OperationCode.INTERNAL_ServerError.getCode(),
                            OperationCode.INTERNAL_ServerError.getDescription(),
                            null,
                            OperationCode.INTERNAL_ServerError.getHttpStatus()
                    ));
        }
    }

    @GetMapping("/meus-pedidos")
    public ResponseEntity<?> listarMeusPedidos(@AuthenticationPrincipal Usuario usuario) {
        try {

            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BaseResponse(
                        OperationCode.UNAUTHORIZED.getCode(),
                        OperationCode.UNAUTHORIZED.getDescription(),
                        null,
                        OperationCode.UNAUTHORIZED.getHttpStatus()
                ));
            }

            String idAluno = usuario.getIdUsuario();
            log.info("Listando histórico de pedidos de usuário");

            List<PedidoResponseDTO> pedidos = pedidoService.listarMeusPedidos(idAluno);

            if (pedidos.isEmpty()) {
                return ResponseEntity.status(OperationCode.SUCCESSFUL_Operation.getHttpStatus()).body(new BaseResponse(
                        OperationCode.SUCCESSFUL_Operation.getCode(),
                        OperationCode.SUCCESSFUL_Operation.getDescription(),
                        pedidos,
                        OperationCode.SUCCESSFUL_Operation.getHttpStatus()
                ));
            }


            return ResponseEntity.status(OperationCode.SUCCESSFUL_Operation.getHttpStatus()).body(new BaseResponse(
                    OperationCode.SUCCESSFUL_Operation.getCode(),
                    OperationCode.SUCCESSFUL_Operation.getDescription(),
                    pedidos,
                    OperationCode.SUCCESSFUL_Operation.getHttpStatus()
            ));

        } catch (Exception exception) {
            log.error("Erro inesperado ao listar pedidos: {}", exception.getMessage(), exception);
            return ResponseEntity.status(OperationCode.INTERNAL_ServerError.getHttpStatus())
                    .body(new BaseResponse(
                            OperationCode.INTERNAL_ServerError.getCode(),
                            OperationCode.INTERNAL_ServerError.getDescription(),
                            null,
                            OperationCode.INTERNAL_ServerError.getHttpStatus()
                    ));
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        BaseResponse response = new BaseResponse(
                OperationCode.INVALID_RequestValue.getCode(),
                OperationCode.INVALID_RequestValue.getDescription() + ": " + errorMessage,
                null,
                OperationCode.INVALID_RequestValue.getHttpStatus()
        );

        log.warn("Erro de validação: {}", errorMessage);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    // Captura campos obrigatórios nulos
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse> handleIllegalArgument(IllegalArgumentException ex) {
        BaseResponse response = new BaseResponse(
                OperationCode.ARGUMENT_Null.getCode(),
                OperationCode.ARGUMENT_Null.getDescription() + ": " + ex.getMessage(),
                null,
                OperationCode.ARGUMENT_Null.getHttpStatus()
        );
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

}
