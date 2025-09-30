package com.example.tridots.controller;

import com.example.tridots.dto.*;
import com.example.tridots.dto.Alunos.*;
import com.example.tridots.model.Aluno;
import com.example.tridots.model.Usuario;
import com.example.tridots.repository.UsuarioRepository;
import com.example.tridots.security.TokenService;
import com.example.tridots.service.AlunoService;
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
import org.springframework.web.bind.annotation.*;

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
    TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<AlunoResponseDTO> register(@RequestBody @Valid AlunoRegisterDTO alunoRegisterDTO) {

        if(this.usuarioRepository.findByEmailInstitucional(alunoRegisterDTO.emailInstitucional()) != null) {
            return ResponseEntity.badRequest().build();
        }

        AlunoResponseDTO alunoResponseDTO = alunoService.createAluno(alunoRegisterDTO);
        log.warn("Cadastro de conta de Aluno requisitado");
        return ResponseEntity.status(HttpStatus.CREATED).body(alunoResponseDTO);

    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> login(@RequestBody @Valid AlunoLoginDTO alunoLoginDTO) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(
                    alunoLoginDTO.emailInstitucional(), alunoLoginDTO.password()
            );
            var auth = this.authenticationManager.authenticate(usernamePassword);
            var token = tokenService.generateToken((Usuario) auth.getPrincipal());
            log.warn("Login de conta de Aluno requisitado");
            log.warn("Login realizado com sucesso para {}", alunoLoginDTO.emailInstitucional());
            return ResponseEntity.ok(new UserLoginResponseDTO(token));

        } catch (BadCredentialsException ex) {
            log.warn("Tentativa de login falhou para: {}", alunoLoginDTO.emailInstitucional());
            log.error("Erro durante processo de autenticação");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (AuthenticationException ex) {
            log.error("Erro de autenticação para {}: {}", alunoLoginDTO.emailInstitucional(), ex.getMessage());
            log.error("Não foi possível autenticar. Verifique suas credenciais.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<AlunoResponseDTO> atualizarInformacoes(
            @AuthenticationPrincipal Usuario aluno, @RequestBody @Valid AlunoUpdateDTO updateDTO) throws AccessDeniedException {

        alunoService.updateAluno(aluno, aluno.getIdUsuario(), updateDTO.nome(), updateDTO.semestre());
        log.warn("Requisição de alteração de informações de conta de usuário realizadas");
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{idAluno}/alterar-senha")
    public ResponseEntity<?> alterarSenha(@PathVariable("idAluno") String idAluno, @RequestBody AlterarSenhaDTO alterarSenhaDTO,
                                          @AuthenticationPrincipal Usuario usuarioLogado) throws AccessDeniedException {
        log.warn("Alteração de senha de conta de Aluno requisitado");
        alunoService.alterarSenha(idAluno, alterarSenhaDTO, usuarioLogado);
        return ResponseEntity.noContent().build();
    }


}
