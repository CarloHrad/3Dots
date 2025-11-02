package com.example.tridots.OperationCode;

import org.springframework.http.HttpStatus;

public enum OperationCode {

    SUCCESSFUL_Operation("00", "Sucesso na Operação", HttpStatus.OK),
    UNAUTHORIZED("01", "Usuário não autenticado", HttpStatus.UNAUTHORIZED),
    ACCESS_Denid("02", "Acesso negado.", HttpStatus.FORBIDDEN),
    LOGIN_NotFound("10", "Login não encontrado na base de dados", HttpStatus.NOT_FOUND),
    EMAIL_AlreadyExists("11", "E-mail já cadastrado", HttpStatus.CONFLICT),
    RA_AlreadyExists("12", "RA de estudante já cadastrado", HttpStatus.CONFLICT),
    PASSWORD_ReuseNotAllowed("13", "A nova senha não pode ser igual à senha atual", HttpStatus.CONFLICT),
    LOGIN_Invalid("14", "Credenciais incorretas", HttpStatus.BAD_REQUEST),
    PEDIDO_NotFound("20", "Pedido não encontrado", HttpStatus.NOT_FOUND),
    PEDIDO_InvalidStatusForAction("21", "Ação não permitida para este status de pedido", HttpStatus.BAD_REQUEST),
    INVALID_RequestValue("22", "Requisição de Inserção inválido", HttpStatus.BAD_REQUEST),
    FILE_UploadFailed("30", "Falha ao efetuar upload do arquivo", HttpStatus.INTERNAL_SERVER_ERROR),
    ARGUMENT_NullOrEmpty("31", "Campo não pode ser nulo", HttpStatus.BAD_REQUEST),
    COMMENT_NotFound("40", "Comentário(s) não encontrado(s)", HttpStatus.NOT_FOUND),
    COMMENT_DoesNotBelong("41", "Comentário não pertence a este pedido", HttpStatus.BAD_REQUEST),
    ARGUMENT_NotFound("42", "Não pôde ser encontrado a(s) entidade(s) relacionada(s)", HttpStatus.NOT_FOUND),
    INTERNAL_ServerError("99", "Erro interno no servidor", HttpStatus.INTERNAL_SERVER_ERROR),
    VARIABLE_MAX_CHARACTER("100", "Ultrapassado o limite máximo de caracteres", HttpStatus.BAD_REQUEST);


    private final String code;
    private final String description;
    private final HttpStatus httpStatus;

    OperationCode(String code, String description, HttpStatus httpStatus) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }

    public String getCode() {return code;}

    public String getDescription() {return description;}

    public HttpStatus getHttpStatus() {return httpStatus;}
}
