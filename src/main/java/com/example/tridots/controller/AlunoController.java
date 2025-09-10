package com.example.tridots.controller;

import com.example.tridots.dto.AlunoRegisterDTO;
import com.example.tridots.dto.AlunoResponseDTO;
import com.example.tridots.model.Aluno;
import com.example.tridots.service.AlunoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/aluno")
public class AlunoController {
    private static final Logger log = LoggerFactory.getLogger(AlunoController.class);

    @Autowired
    AlunoService alunoService;

    @PostMapping("/register")
    public ResponseEntity<AlunoResponseDTO> register(@RequestBody @Valid AlunoRegisterDTO alunoRegisterDTO) {
        try {
            AlunoResponseDTO alunoResponseDTO = alunoService.createAluno(alunoRegisterDTO);
            log.warn("Conta de Aluno requisitada");
            return ResponseEntity.status(HttpStatus.CREATED).body(alunoResponseDTO);
        } catch (Exception exception) {
            return ResponseEntity.badRequest().build();
        }

    }
}
