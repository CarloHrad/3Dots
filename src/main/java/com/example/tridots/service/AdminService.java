package com.example.tridots.service;

import com.example.tridots.OperationCode.OperationCode;
import com.example.tridots.dto.Administrador.StatusUpdateDTO;
import com.example.tridots.dto.Alunos.AlterarSenhaDTO;
import com.example.tridots.dto.ArquivoDTO;
import com.example.tridots.dto.Pedidos.PedidoResponseDTO;
import com.example.tridots.model.*;
import com.example.tridots.repository.AdministradorRepository;
import com.example.tridots.repository.PedidoRepository;
import com.example.tridots.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Service
public class AdminService {

    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    AdministradorRepository administradorRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    private static final Logger log = LoggerFactory.getLogger(AdminService.class);

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Transactional
    public void alterarSenha(String idUsuario, AlterarSenhaDTO alterarSenhaDTO, Usuario usuarioLogado) throws AccessDeniedException {
        if (!usuarioLogado.getIdUsuario().equals(idUsuario)) {
            log.error("Houve uma falha durante a requisição de alteração de senha");
            throw new AccessDeniedException("Falha de autenticação durante mudança de senha");
        }

        Administrador administrador = administradorRepository.findById(idUsuario).orElse(null);

        if (administrador == null) {
            log.error("O ID não pôde ser encontrado");
            new BaseResponse(
                    OperationCode.LOGIN_NotFound.getCode(),
                    OperationCode.LOGIN_NotFound.getDescription(),
                    null,
                    OperationCode.LOGIN_NotFound.getHttpStatus()
            );
            return;
        }

        if (!passwordEncoder.matches(alterarSenhaDTO.senhaAtual(), administrador.getPassword())) {
            log.error("A senha atual está incorreta. Tente novamente");
            throw new RuntimeException("Senha atual incorreta");
        }

        administrador.setPassword(passwordEncoder.encode(alterarSenhaDTO.novaSenha()));
        administradorRepository.save(administrador);
        log.warn("Senha atualizada com sucesso!");
        new BaseResponse(
                OperationCode.SUCCESSFUL_Operation.getCode(),
                OperationCode.SUCCESSFUL_Operation.getDescription(),
                null,
                OperationCode.SUCCESSFUL_Operation.getHttpStatus()
        );
    }
}
