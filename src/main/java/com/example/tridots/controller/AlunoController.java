package com.example.tridots.controller;

import com.example.tridots.OperationCode.OperationCode;
import com.example.tridots.dto.Alunos.*;
import com.example.tridots.model.Usuario;
import com.example.tridots.repository.AlunoRepository;
import com.example.tridots.repository.UsuarioRepository;
import com.example.tridots.security.TokenService;
import com.example.tridots.service.AlunoService;
import com.example.tridots.service.BaseResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/aluno")
public class AlunoController {
    private static final Logger log = LoggerFactory.getLogger(AlunoController.class);

    @Autowired
    AlunoService alunoService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    AlunoRepository alunoRepository;
    @Autowired
    TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse> register(@RequestBody @Valid AlunoRegisterDTO alunoRegisterDTO) {

        if (usuarioRepository.findByEmailInstitucional(alunoRegisterDTO.emailInstitucional()) != null) {
            BaseResponse response = new BaseResponse(
                    OperationCode.EMAIL_AlreadyExists.getCode(),
                    OperationCode.EMAIL_AlreadyExists.getDescription(),
                    null,
                    OperationCode.EMAIL_AlreadyExists.getHttpStatus()
            );
            return ResponseEntity.status(response.getHttpStatus()).body(response);
        }

        if (alunoRepository.findByRaAluno(alunoRegisterDTO.raAluno()) != null) {
            BaseResponse response = new BaseResponse(
                    OperationCode.RA_AlreadyExists.getCode(),
                    OperationCode.RA_AlreadyExists.getDescription(),
                    null,
                    OperationCode.RA_AlreadyExists.getHttpStatus()
            );
            return ResponseEntity.status(response.getHttpStatus()).body(response);
        }

        AlunoResponseDTO alunoResponseDTO = alunoService.createAluno(alunoRegisterDTO);

        BaseResponse response = new BaseResponse(
                OperationCode.SUCCESSFUL_Operation.getCode(),
                OperationCode.SUCCESSFUL_Operation.getDescription(),
                alunoResponseDTO,
                OperationCode.SUCCESSFUL_Operation.getHttpStatus()
        );

        log.warn("Cadastro realizado com sucesso para {}", alunoRegisterDTO.emailInstitucional());
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse> login(@RequestBody @Valid AlunoLoginDTO alunoLoginDTO) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(
                    alunoLoginDTO.emailInstitucional(), alunoLoginDTO.password()
            );
            var auth = authenticationManager.authenticate(usernamePassword);
            var token = tokenService.generateToken((Usuario) auth.getPrincipal());

            log.warn("Login realizado com sucesso para {}", alunoLoginDTO.emailInstitucional());

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

        } catch (Exception e) {
            BaseResponse response = new BaseResponse(
                    OperationCode.INTERNAL_ServerError.getCode(),
                    OperationCode.INTERNAL_ServerError.getDescription(),
                    null,
                    OperationCode.INTERNAL_ServerError.getHttpStatus()
            );
            return ResponseEntity.status(response.getHttpStatus()).body(response);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<BaseResponse> atualizarInformacoes(
            @AuthenticationPrincipal Usuario aluno, @RequestBody @Valid AlunoUpdateDTO updateDTO) {
        try {
            alunoService.updateAluno(aluno, aluno.getIdUsuario(), updateDTO.nome(), updateDTO.semestre());
            log.warn("Requisição de alteração de informações do usuário {}", aluno.getEmailInstitucional());

            BaseResponse response = new BaseResponse(
                    OperationCode.SUCCESSFUL_Operation.getCode(),
                    OperationCode.SUCCESSFUL_Operation.getDescription(),
                    null,
                    OperationCode.SUCCESSFUL_Operation.getHttpStatus()
            );
            return ResponseEntity.status(response.getHttpStatus()).body(response);

        } catch (AccessDeniedException accessDeniedException) {
            BaseResponse response = new BaseResponse(
                    OperationCode.ACCESS_Denid.getCode(),
                    OperationCode.ACCESS_Denid.getDescription(),
                    null,
                    OperationCode.ACCESS_Denid.getHttpStatus()
            );
            return ResponseEntity.status(response.getHttpStatus()).body(response);

        } catch (Exception exception) {
            log.error("Erro inesperado ao editar informações de usuário", exception);
            BaseResponse response = new BaseResponse(
                    OperationCode.INTERNAL_ServerError.getCode(),
                    OperationCode.INTERNAL_ServerError.getDescription(),
                    null,
                    OperationCode.INTERNAL_ServerError.getHttpStatus()
            );
            return ResponseEntity.status(response.getHttpStatus()).body(response);
        }
    }

    @PutMapping("/{idAluno}/alterar-senha")
    public ResponseEntity<BaseResponse> alterarSenha(@PathVariable("idAluno") String idAluno,
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
            log.warn("Alteração de senha solicitada para usuário {}", idAluno);
            alunoService.alterarSenha(idAluno, alterarSenhaDTO, usuarioLogado);

            BaseResponse response = new BaseResponse(
                    OperationCode.SUCCESSFUL_Operation.getCode(),
                    OperationCode.SUCCESSFUL_Operation.getDescription(),
                    null,
                    OperationCode.SUCCESSFUL_Operation.getHttpStatus()
            );
            return ResponseEntity.status(response.getHttpStatus()).body(response);

        } catch (AccessDeniedException accessDeniedException) {
            BaseResponse response = new BaseResponse(
                    OperationCode.ACCESS_Denid.getCode(),
                    OperationCode.ACCESS_Denid.getDescription(),
                    null,
                    OperationCode.ACCESS_Denid.getHttpStatus()
            );
            return ResponseEntity.status(response.getHttpStatus()).body(response);

        } catch (Exception exception) {
            log.error("Erro inesperado ao alterar senha de usuário {}", idAluno, exception);
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
