CREATE TABLE usuario (
    id_usuario VARCHAR(36) PRIMARY KEY,
    nome VARCHAR(255),
    email_institucional VARCHAR(255),
    password VARCHAR(255),
    cargo VARCHAR(50)
);

CREATE TABLE administrador (
    id_usuario VARCHAR(36) PRIMARY KEY,
    email VARCHAR(255),
    telefone VARCHAR(50),
    CONSTRAINT fk_adm_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE aluno (
    id_usuario VARCHAR(36) PRIMARY KEY,
    ra_aluno VARCHAR(20),
    curso VARCHAR(100),
    semestre INT,
    status_matricula VARCHAR(50),
    CONSTRAINT fk_aluno_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);


