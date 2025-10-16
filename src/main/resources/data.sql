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

INSERT INTO usuario (id_usuario, nome, email_institucional, password, cargo)
VALUES ('4c6d7682-fc56-4d15-b666-2c160cd7f473',
        'João Administrador', 'admin@tridots.com', '$2a$10$l4dueddS0Qc2w44G320RfOcFK/QG1Zjx6SS0W8LyrmRxIXqI.wmm6', -- senha: "123456"
        'ADMINISTRADOR');

INSERT INTO administrador (id_usuario, email, telefone)
VALUES ('4c6d7682-fc56-4d15-b666-2c160cd7f473','admin@tridots.com','(11) 99999-9999');

INSERT INTO usuario (id_usuario, nome, email_institucional, password, cargo) VALUES
('f5536bf2-e7ae-4092-b1c9-3d9b5716febc', 'José Manuel', 'jose.manuel23@tridots.com', '$2b$10$NiJ5f/mIxX.4aZSIUjHP2e723xPbAmD8eJIGIOv.fZ.SNf8eGla6G', 'ALUNO');

INSERT INTO aluno (id_usuario, ra_aluno, curso, semestre, status_matricula) VALUES
('f5536bf2-e7ae-4092-b1c9-3d9b5716febc', '123456789', 'Análise de Sistemas', '3', 'ATIVA');