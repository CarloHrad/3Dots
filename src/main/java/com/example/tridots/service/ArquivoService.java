package com.example.tridots.service;

import com.example.tridots.model.Arquivo;
import com.example.tridots.repository.ArquivoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ArquivoService {

    @Value("${storage.location}")
    private String localDir;

    @Autowired
    ArquivoRepository arquivoRepository;

    private static final Logger log = LoggerFactory.getLogger(ArquivoService.class);


    public Arquivo salvar(MultipartFile file) throws IOException {

        String nomeArquivo = file.getOriginalFilename();
        if (nomeArquivo == null || !nomeArquivo.contains(".")) {
            throw new IllegalArgumentException("Arquivo inválido, sem extensão: " + nomeArquivo);
        }

        String extensao = nomeArquivo.substring(nomeArquivo.lastIndexOf('.') + 1).toLowerCase();
        List<String> extensoesPermitidas = List.of("stl", "obj", "fbx", "3mf", "dae", "txt");

        if (!extensoesPermitidas.contains(extensao)) {
            throw new IllegalArgumentException("Tipo de arquivo não permitido: " + extensao);
        }



        Path dir = Paths.get(localDir).toAbsolutePath();
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        String newName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path dirFile = dir.resolve(newName);
        file.transferTo(dirFile.toFile());

        log.warn("Passado o caminho do arquivo");


        Arquivo arquivo = new Arquivo();
        arquivo.setNomeArquivo(file.getOriginalFilename());
        arquivo.setTipoArquivo(file.getContentType());
        arquivo.setTamanho(file.getSize());
        arquivo.setCaminho(dirFile.toString());

        log.warn("Arquivo salvo com sucesso!");
        return arquivoRepository.save(arquivo);
    }

    public List<Arquivo> findAll() {
        return arquivoRepository.findAll();
    }

    public byte[] uploadArquivo(String id) throws IOException {
        Arquivo arquivo = arquivoRepository.findById(id).orElseThrow(() -> new RuntimeException("Arquivo inexistente"));

        Path filePath = Paths.get(arquivo.getCaminho());
        return Files.readAllBytes(filePath);
    }

}
