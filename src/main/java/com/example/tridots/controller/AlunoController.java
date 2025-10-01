package com.example.tridots.controller;

import com.example.tridots.OperationCode.OperationCode;
import com.example.tridots.dto.*;
import com.example.tridots.dto.Alunos.*;
import com.example.tridots.model.Aluno;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.util.List;

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

        } catch (AccessDeniedException e) {
            BaseResponse response = new BaseResponse(
                    OperationCode.ACCESS_Denid.getCode(),
                    OperationCode.ACCESS_Denid.getDescription(),
                    null,
                    OperationCode.ACCESS_Denid.getHttpStatus()
            );
            return ResponseEntity.status(response.getHttpStatus()).body(response);

        } catch (Exception e) {
            log.error("Erro inesperado ao editar informações de usuário", e);
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

        } catch (AccessDeniedException e) {
            BaseResponse response = new BaseResponse(
                    OperationCode.ACCESS_Denid.getCode(),
                    OperationCode.ACCESS_Denid.getDescription(),
                    null,
                    OperationCode.ACCESS_Denid.getHttpStatus()
            );
            return ResponseEntity.status(response.getHttpStatus()).body(response);

        } catch (Exception e) {
            log.error("Erro inesperado ao alterar senha de usuário {}", idAluno, e);
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
