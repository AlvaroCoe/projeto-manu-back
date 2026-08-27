package com.example.back_end.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class UploadService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    private static final List<String> EXTENSOES_PERMITIDAS = List.of(".jpg", ".jpeg", ".png", ".webp");

    public String salvar(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalStateException("Nenhum arquivo enviado");
        }

        String nomeOriginal = file.getOriginalFilename();
        String extensao = (nomeOriginal != null && nomeOriginal.contains("."))
                ? nomeOriginal.substring(nomeOriginal.lastIndexOf(".")).toLowerCase()
                : "";

        if (!EXTENSOES_PERMITIDAS.contains(extensao)) {
            throw new IllegalStateException("Formato de arquivo não permitido. Use JPG, PNG ou WEBP.");
        }

        try {
            Path pastaUploads = Paths.get(uploadDir);
            if (!Files.exists(pastaUploads)) {
                Files.createDirectories(pastaUploads);
            }

            String nomeArquivo = UUID.randomUUID() + extensao;
            Path caminhoArquivo = pastaUploads.resolve(nomeArquivo);
            Files.copy(file.getInputStream(), caminhoArquivo, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + nomeArquivo;
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao salvar o arquivo: " + e.getMessage());
        }
    }
}