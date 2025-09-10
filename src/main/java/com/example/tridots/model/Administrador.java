package com.example.tridots.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "administrador")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Administrador extends Usuario {
    private String email;
    private String telefone;
}
