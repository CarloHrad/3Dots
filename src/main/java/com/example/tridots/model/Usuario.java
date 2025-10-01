package com.example.tridots.model;

import com.example.tridots.enums.Cargo;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "usuario")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idUsuario;
    //id_aluno
    private String nome;
    private String emailInstitucional;
    private String password;

    @Enumerated(EnumType.STRING)
    private Cargo cargo;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.cargo == Cargo.ADMINISTRADOR) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"));
        } else if (this.cargo == Cargo.ALUNO) {
            return List.of(new SimpleGrantedAuthority("ROLE_ALUNO"));
        } else {
            return List.of();
        }
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return this.emailInstitucional;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
