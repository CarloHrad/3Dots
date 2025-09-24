package com.example.tridots.repository;

import com.example.tridots.model.Aluno;
import com.example.tridots.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    Usuario findByEmailInstitucional(String emailInstitucional);
}
