package com.example.tridots.service;

import com.example.tridots.dto.AlunoRegisterDTO;
import com.example.tridots.dto.AlunoResponseDTO;
import com.example.tridots.enums.Cargo;
import com.example.tridots.enums.StatusMatricula;
import com.example.tridots.model.Aluno;
import com.example.tridots.model.Usuario;
import com.example.tridots.repository.AlunoRepository;
import com.example.tridots.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlunoService {

    @Autowired
    AlunoRepository alunoRepository;
    @Autowired
    UsuarioRepository usuarioRepository;

    public AlunoResponseDTO createAluno(AlunoRegisterDTO alunoRegisterDTO) {
        Usuario usuario = new Usuario() {};
        usuario.setNome(alunoRegisterDTO.nome());
        usuario.setEmailInstitucional(alunoRegisterDTO.emailInstitucional());
        usuario.setPassword(alunoRegisterDTO.password());
        usuario.setCargo(Cargo.ALUNO);

        usuario = usuarioRepository.save(usuario);

        //CRIAR ALUNO
        Aluno aluno = new Aluno();
        aluno.setIdUsuario(usuario.getIdUsuario());
        aluno.setRaAluno(alunoRegisterDTO.raAluno());
        aluno.setCurso(alunoRegisterDTO.curso());
        aluno.setSemestre(alunoRegisterDTO.semestre());
        aluno.setStatusMatricula(StatusMatricula.ATIVA);

        aluno = alunoRepository.save(aluno);

        //RETORNAR DTO DE RESPOSTA
        return new AlunoResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNome(),
                usuario.getEmailInstitucional(),
                aluno.getRaAluno(),
                aluno.getCurso(),
                aluno.getSemestre(),
                aluno.getStatusMatricula()
        );

    }


}
