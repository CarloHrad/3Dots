package com.example.tridots.controller;

import com.example.tridots.model.Arquivo;
import com.example.tridots.repository.ArquivoRepository;
import com.example.tridots.service.ArquivoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable String id) throws IOException {
        Arquivo arquivo = arquivoService.findById(id); // vamos criar esse método abaixo
        byte[] conteudo = arquivoService.uploadArquivo(id);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + arquivo.getNomeArquivo() + "\"")
                .header("Content-Type", arquivo.getTipoArquivo())
                .body(conteudo);
    }



    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonFormatError(HttpMessageNotReadableException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Requisição inválida — JSON mal formatado ou Campo invalidado");
        body.put("message", "Verifique se o corpo JSON está correto. Erro de leitura: " + ex.getMostSpecificCause().getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
