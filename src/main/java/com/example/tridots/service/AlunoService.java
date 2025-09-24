package com.example.tridots.service;

import com.example.tridots.dto.AlterarSenhaDTO;
import com.example.tridots.dto.AlunoRegisterDTO;
import com.example.tridots.dto.AlunoResponseDTO;
import com.example.tridots.enums.Cargo;
import com.example.tridots.enums.StatusMatricula;
import com.example.tridots.model.Aluno;
import com.example.tridots.model.Usuario;
import com.example.tridots.repository.AlunoRepository;
import com.example.tridots.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AlunoService {

    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    AlunoRepository alunoRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    private static final Logger log = LoggerFactory.getLogger(AlunoService.class);


    public AlunoResponseDTO createAluno(AlunoRegisterDTO alunoRegisterDTO) {

        Aluno aluno = new Aluno();
        aluno.setNome(alunoRegisterDTO.nome());
        aluno.setEmailInstitucional(alunoRegisterDTO.emailInstitucional());
        aluno.setPassword(passwordEncoder.encode(alunoRegisterDTO.password()));
        aluno.setCargo(Cargo.ALUNO);
        aluno.setRaAluno(alunoRegisterDTO.raAluno());
        aluno.setCurso(alunoRegisterDTO.curso());
        aluno.setSemestre(alunoRegisterDTO.semestre());
        aluno.setStatusMatricula(StatusMatricula.ATIVA);

        Aluno newAluno = usuarioRepository.save(aluno);
        log.warn("Aluno cadastrado com sucesso!");

        //retonardo criação de response do aluno
        return new AlunoResponseDTO(
                aluno.getIdUsuario(),
                aluno.getNome(),
                aluno.getEmailInstitucional(),
                aluno.getRaAluno(),
                aluno.getCurso(),
                aluno.getSemestre(),
                aluno.getStatusMatricula(),
                aluno.getCargo()
        );
    }

    public void updateAluno(Usuario usuarioLogado, String idAluno, String nome, Integer semestre) throws AccessDeniedException {
        Aluno aluno = alunoRepository.findById(idAluno).orElseThrow(() -> {
            log.error("O aluno com o vigente ID não pôde ser encontrado");
            return new RuntimeException("Aluno não encontrado");
        });

        if (!aluno.getIdUsuario().equals(usuarioLogado.getIdUsuario())) {
            String user = usuarioLogado.getIdUsuario();
            log.warn("Tentativa de atualização não autorizada. Usuário {} tentou atualizar outro aluno", user);
            throw new AccessDeniedException("Você não tem permissão para alterar esta conta.");
        }

        aluno.setNome(nome);
        aluno.setSemestre(semestre);
        alunoRepository.save(aluno);
        log.warn("Dados de aluno foram atualizados com sucesso!");
    }

    @Transactional
    public void alterarSenha(String idAluno, AlterarSenhaDTO alterarSenhaDTO, Usuario usuarioLogado) throws AccessDeniedException {
        if (!usuarioLogado.getIdUsuario().equals(idAluno)) {
            log.error("Houve uma falha durante a requisição de alteração de senha");
            throw new AccessDeniedException("Falha de autenticação durante mudança de senha");
        }

        Aluno aluno = alunoRepository.findById(idAluno).orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        if (!passwordEncoder.matches(alterarSenhaDTO.senhaAtual(), aluno.getPassword())) {
            log.error("A senha atual está incorreta. Tente novamente");
            throw new RuntimeException("Senha atual incorreta");
        }

        aluno.setPassword(passwordEncoder.encode(alterarSenhaDTO.novaSenha()));
        alunoRepository.save(aluno);
        log.warn("Senha atualizada com sucesso!");
    }

    public List<Aluno> getAlunos() {
        log.info("Requisitando todos os alunos: ");
        return usuarioRepository.findAll()
                .stream()
                .filter(u -> u instanceof Aluno)
                .map(u -> (Aluno) u)
                .toList();
    }




}
