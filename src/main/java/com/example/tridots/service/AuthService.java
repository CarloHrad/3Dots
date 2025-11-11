package com.example.tridots.service;

import com.example.tridots.OperationCode.OperationCode;
import com.example.tridots.model.Aluno;
import com.example.tridots.model.Usuario;
import com.example.tridots.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService implements UserDetailsService {
    @Autowired
    UsuarioRepository usuarioRepository;

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Usuario user = usuarioRepository.findByEmailInstitucional(username);

        if (user == null) {
            log.error("Usuário não pôde ser encontrado");
            throw new UsernameNotFoundException(OperationCode.LOGIN_NotFound.getDescription());
        }

        String role = "ROLE_" + user.getCargo().name();

        return user;
    }

}
