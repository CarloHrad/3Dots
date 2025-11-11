package com.example.tridots.controller;

import com.example.tridots.OperationCode.OperationCode;
import com.example.tridots.dto.Alunos.AlunoDTO;
import com.example.tridots.dto.ArquivoDTO;
import com.example.tridots.dto.Pedidos.PedidoAlunoDTO;
import com.example.tridots.dto.Pedidos.PedidoRequestDTO;
import com.example.tridots.dto.Pedidos.PedidoResponseDTO;
import com.example.tridots.model.Pedido;
import com.example.tridots.model.Usuario;
import com.example.tridots.repository.PedidoRepository;
import com.example.tridots.service.BaseResponse;
import com.example.tridots.service.PedidoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", allowedHeaders = "*")
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
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart("pedido") @Valid PedidoRequestDTO pedidoRequestDTO) throws IOException {

            log.info("Criação de Pedido requisitado.");
            BaseResponse response = pedidoService.criarPedido(alunoLogin.getIdUsuario(), file, pedidoRequestDTO);

            return ResponseEntity.status(response.getHttpStatus()).body(response);
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

            BaseResponse response = pedidoService.cancelarPedido(alunoLogin.getIdUsuario(), idPedido);
            log.info("Cencelamento do pedido {} requisitado", idPedido);

            return ResponseEntity.status(response.getHttpStatus()).body(response);

        } catch (AccessDeniedException accessDeniedException) {
            log.error("Acesso negado ao cancelar pedido {}", idPedido, accessDeniedException);
            return ResponseEntity.status(OperationCode.ACCESS_Denid.getHttpStatus())
                    .body(new BaseResponse(
                            OperationCode.ACCESS_Denid.getCode(),
                            OperationCode.ACCESS_Denid.getDescription(),
                            null,
                            OperationCode.ACCESS_Denid.getHttpStatus()
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

            log.info("Listando histórico de pedidos de usuário");
            List<PedidoResponseDTO> pedidos = pedidoService.listarMeusPedidos(usuario.getIdUsuario());

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

    @GetMapping("/{idPedido}")
    public ResponseEntity<BaseResponse> listarPedidoPorId(@AuthenticationPrincipal Usuario usuario, @PathVariable String idPedido) {
        Pedido pedido = pedidoService.buscarPorId(idPedido);

        if (pedido == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new BaseResponse("20", OperationCode.PEDIDO_NotFound.getDescription(), null, OperationCode.PEDIDO_NotFound.getHttpStatus())
            );
        }

        AlunoDTO alunoDTO = new AlunoDTO(
                pedido.getAluno().getNome(),
                pedido.getAluno().getEmailInstitucional(),
                pedido.getAluno().getRaAluno(),
                pedido.getAluno().getCurso(),
                pedido.getAluno().getSemestre()
        );

        ArquivoDTO arquivoDTO = null;
        if (pedido.getArquivo() != null) {
            arquivoDTO = new ArquivoDTO(
                    pedido.getArquivo().getIdArquivo(),
                    pedido.getArquivo().getNomeArquivo(),
                    pedido.getArquivo().getTipoArquivo()
            );
        }

        PedidoAlunoDTO pedidoResponseDTO = new PedidoAlunoDTO(
                alunoDTO,
                pedido.getIdPedido(),
                arquivoDTO,
                pedido.getDescricao(),
                pedido.getMedidas().getAltura(),
                pedido.getMedidas().getLargura(),
                pedido.getMedidas().getProfundidade(),
                pedido.getObservacao(),
                pedido.getDiasEstimados(),
                pedido.getData(),
                pedido.getStatus().name()
        );

        return ResponseEntity.ok(
                new BaseResponse(OperationCode.SUCCESSFUL_Operation.getCode(), "Sucesso na operação", pedidoResponseDTO, OperationCode.SUCCESSFUL_Operation.getHttpStatus())
        );
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
            operationCode = OperationCode.ARGUMENT_NullOrEmpty;
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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonFormatError(HttpMessageNotReadableException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Requisição inválida — JSON mal formatado ou Campo invalidado");
        body.put("message", "Verifique se o corpo JSON está correto. Erro de leitura: " + ex.getMostSpecificCause().getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }


}
