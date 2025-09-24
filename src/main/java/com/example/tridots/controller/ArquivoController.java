package com.example.tridots.controller;

import com.example.tridots.model.Arquivo;
import com.example.tridots.repository.ArquivoRepository;
import com.example.tridots.service.ArquivoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/arquivo")
public class ArquivoController {
    @Autowired
    ArquivoService arquivoService;

    private static final Logger log = LoggerFactory.getLogger(ArquivoController.class);

    List<String> extensoes = List.of("stl", "obj", "fbx", "3mf", "dae", "txt");

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            log.error("O arquivo anexado está vazio ou inexistente");
            return ResponseEntity.badRequest().body("Arquivo vazio!");
        }

        String nomeArquivo = file.getOriginalFilename();
        String extensao = nomeArquivo.substring(nomeArquivo.lastIndexOf('.')+1).toLowerCase();

        if (!extensoes.contains(extensao)) {
            log.error("O tipo de arquivo anexado não é suportado");
            return ResponseEntity.badRequest().body("Tipo de arquivo não permitido!");
        }

        Arquivo newArquivo = arquivoService.salvar(file);
        log.warn("Arquivo salvo com sucesso!");
        return ResponseEntity.ok(newArquivo);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Arquivo>> getAll() {
        List<Arquivo> arquivos = arquivoService.findAll();
        log.warn("Requisitando todos os documentos...");
        return ResponseEntity.ok(arquivos);
    }
}
