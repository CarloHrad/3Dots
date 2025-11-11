package com.example.tridots.controller;

import com.example.tridots.OperationCode.OperationCode;
import com.example.tridots.dto.Administrador.StatusUpdateDTO;
import com.example.tridots.dto.Alunos.AlterarSenhaDTO;
import com.example.tridots.dto.Alunos.AlunoLoginDTO;
import com.example.tridots.dto.Alunos.AlunoResponseDTO;
import com.example.tridots.dto.Pedidos.PedidoAlunoDTO;
import com.example.tridots.enums.Cargo;
import com.example.tridots.model.Usuario;
import com.example.tridots.security.TokenService;
import com.example.tridots.service.AdminService;
import com.example.tridots.service.AlunoService;
import com.example.tridots.service.BaseResponse;
import com.example.tridots.service.PedidoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    AlunoService alunoService;
    @Autowired
    PedidoService pedidoService;
    @Autowired
    AdminService adminService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    TokenService tokenService;

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    /*Endpoint para administrador atualizar os próprios dados*/

    @PostMapping("/login")
    public ResponseEntity<BaseResponse> adminLogin(@RequestBody @Valid AlunoLoginDTO loginDTO) {
        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.emailInstitucional(), loginDTO.password())
            );

            Usuario usuario = (Usuario) auth.getPrincipal();

            if (usuario.getCargo() != Cargo.ADMINISTRADOR) {
                throw new BadCredentialsException("Acesso permitido apenas para administradores");
            }

            String token = tokenService.generateToken(usuario);

            BaseResponse response = new BaseResponse(
                    OperationCode.SUCCESSFUL_Operation.getCode(),
                    OperationCode.SUCCESSFUL_Operation.getDescription(),
                    token,
                    OperationCode.SUCCESSFUL_Operation.getHttpStatus()
            );

            return ResponseEntity.status(response.getHttpStatus()).body(response);

        } catch (BadCredentialsException e) {
            BaseResponse response = new BaseResponse(
                    OperationCode.LOGIN_Invalid.getCode(),
                    OperationCode.LOGIN_Invalid.getDescription(),
                    null,
                    OperationCode.LOGIN_Invalid.getHttpStatus()
            );
            return ResponseEntity.status(response.getHttpStatus()).body(response);
        }
    }



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

    @PutMapping("/{idUsuario}/alterar-senha")
    public ResponseEntity<BaseResponse> alterarSenha(@PathVariable("idUsuario") String idUsuario,
                                                     @RequestBody AlterarSenhaDTO alterarSenhaDTO,
                                                     @AuthenticationPrincipal Usuario usuarioLogado) {

        if (alterarSenhaDTO.novaSenha().equals(alterarSenhaDTO.senhaAtual())) {
            BaseResponse response = new BaseResponse(
                    OperationCode.PASSWORD_ReuseNotAllowed.getCode(),
                    OperationCode.PASSWORD_ReuseNotAllowed.getDescription(),
                    null,
                    OperationCode.PASSWORD_ReuseNotAllowed.getHttpStatus()
            );
            return ResponseEntity.status(response.getHttpStatus()).body(response);
        }

        try {
            log.warn("Alteração de senha solicitada para usuário {}", idUsuario);
            adminService.alterarSenha(idUsuario, alterarSenhaDTO, usuarioLogado);

            BaseResponse response = new BaseResponse(
                    OperationCode.SUCCESSFUL_Operation.getCode(),
                    OperationCode.SUCCESSFUL_Operation.getDescription(),
                    null,
                    OperationCode.SUCCESSFUL_Operation.getHttpStatus()
            );
            return ResponseEntity.status(response.getHttpStatus()).body(response);

        } catch (java.nio.file.AccessDeniedException accessDeniedException) {
            BaseResponse response = new BaseResponse(
                    OperationCode.ACCESS_Denid.getCode(),
                    OperationCode.ACCESS_Denid.getDescription(),
                    null,
                    OperationCode.ACCESS_Denid.getHttpStatus()
            );
            return ResponseEntity.status(response.getHttpStatus()).body(response);

        } catch (Exception exception) {
            log.error("Erro inesperado ao alterar senha de usuário {}", idUsuario, exception);
            BaseResponse response = new BaseResponse(
                    OperationCode.INTERNAL_ServerError.getCode(),
                    OperationCode.INTERNAL_ServerError.getDescription(),
                    null,
                    OperationCode.INTERNAL_ServerError.getHttpStatus()
            );
            return ResponseEntity.status(response.getHttpStatus()).body(response);
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
