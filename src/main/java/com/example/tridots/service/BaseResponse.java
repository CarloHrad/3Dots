package com.example.tridots.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BaseResponse {
    private String operationCode;
    private String returnDescription;
    private Object responseObject;
    @JsonIgnore
    private HttpStatus httpStatus;
}
