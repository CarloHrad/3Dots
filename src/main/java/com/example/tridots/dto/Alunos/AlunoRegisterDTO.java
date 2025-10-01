package com.example.tridots.dto.Alunos;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;

public record AlunoRegisterDTO(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 50, message = "Nome deve ter no máximo 50 caracteres")
        @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s]+$", message = "Nome deve ter apenas letras")
        @Column(name="nome", unique = true)
        String nome,

        @NotBlank(message = "O email é obrigatório")
        @Size(max = 50, message = "Nome deve ter no máximo 50 caracteres")
        @Email(message = "Email inválido")
        @Column(name="emailInstitucional", unique = true)
        String emailInstitucional,

        @Column(nullable = false)
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        @NotBlank(message = "A senha é obrigatório")
        @Size(min = 4, message = "Senha deve possuir no mínimo 4 caracteres")
        String password,

        @NotBlank(message = "O RA do aluno é obrigatório")
        @Size(max = 20, message = "Nome deve ter no máximo 20 caracteres")
        @Pattern(regexp = "^[0-9]+$", message = "Campo deve conter apenas números")
        @Column(name="raAluno", unique = true)
        String raAluno,

        @NotBlank(message = "O curso é obrigatório")
        @Size(max = 50, message = "Nome deve ter no máximo 50 caracteres")
        @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s]+$", message = "Nome deve ter apenas letras")
        String curso,

        @NotNull(message = "O semestre do aluno é obrigatório")
        @Min(value = 1, message = "O semestre deve ser no mínimo 1")
        @Max(value = 10, message = "O semestre deve ser no máximo 10")
        @Column(name="semestre")
        Integer semestre
) {
        //@Pattern(regexp = "^[A-Za-z0-9._%+-]+@faculdade\\.edu\\.br$",
        //         message = "O email deve ser institucional (@faculdade.edu.br)")
}
