package com.example.tridots.controller;

import com.example.tridots.OperationCode.OperationCode;
import com.example.tridots.dto.Alunos.AlunoLoginDTO;
import com.example.tridots.dto.Alunos.AlunoRegisterDTO;
import com.example.tridots.enums.Cargo;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

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

        BaseResponse alunoResponseDTO = alunoService.createAluno(alunoRegisterDTO);

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
            Usuario usuario = (Usuario) auth.getPrincipal();
            var token = tokenService.generateToken((Usuario) auth.getPrincipal());

            log.warn("Login realizado com sucesso para {}", alunoLoginDTO.emailInstitucional());

            BaseResponse response = new BaseResponse(
                    OperationCode.SUCCESSFUL_Operation.getCode(),
                    OperationCode.SUCCESSFUL_Operation.getDescription(),
                    Map.of(
                            "token", token,
                            "role", usuario.getCargo().name()
                    ),
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
}
