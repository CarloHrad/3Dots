package com.example.tridots.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DiscriminatorValue("ADMINISTRADOR")
@Entity
@Table(name = "administrador")
@PrimaryKeyJoinColumn(name = "idUsuario")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Administrador extends Usuario {
    @Column(name = "email", length = 11)
    private String email;
    @Column(name = "telefone")
    private String telefone;
}
