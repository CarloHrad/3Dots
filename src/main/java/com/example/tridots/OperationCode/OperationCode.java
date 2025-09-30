package com.example.tridots.OperationCode;

import org.springframework.http.HttpStatus;

public enum OperationCode {

    SUCCESSFUL_Operation("00", "Sucesso na Operação", HttpStatus.OK),
    UNAUTHORIZED("01", "Usuário não autenticado", HttpStatus.UNAUTHORIZED),
    ACCESS_Denid("02", "Acesso negado.", HttpStatus.FORBIDDEN),
    LOGIN_NotFound("10", "Login não encontrado na base de dados", HttpStatus.NOT_FOUND),
    EMAIL_AlreadyExists("11", "E-mail já cadastrado", HttpStatus.CONFLICT),
    LOGIN_Invalid("12", "Credenciais incorretas", HttpStatus.BAD_REQUEST),
    PEDIDO_NotFound("20", "Pedido não encontrado", HttpStatus.NOT_FOUND),
    PEDIDO_InvalidStatusForAction("21", "Ação não permitida para este status de pedido", HttpStatus.BAD_REQUEST),
    INVALID_RequestValue("22", "Requisição de Inserção inválido", HttpStatus.BAD_REQUEST),
    FILE_UploadFailed("30", "Falha ao efetuar upload do arquivo", HttpStatus.INTERNAL_SERVER_ERROR),
    ARGUMENT_Null("31", "Campo não pode ser nulo", HttpStatus.BAD_REQUEST),
    COMMENT_NotFound("40", "Comentário não encontrado", HttpStatus.NOT_FOUND),
    COMMENT_DoesNotBelong("41", "Comentário não pertence a este pedido", HttpStatus.BAD_REQUEST),
    INTERNAL_ServerError("99", "Erro interno no servidor", HttpStatus.INTERNAL_SERVER_ERROR);

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
