package com.senac.pi.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    // Define o caminho da pasta onde os certificados serão salvos
    // 'uploads' ficará na raiz do seu projeto
    private final Path rootLocation = Paths.get("uploads/certificados");

    public String saveFile(MultipartFile file) {
        try {
            // 1. Cria o diretório se ele não existir
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }

            // 2. Valida se o arquivo está vazio
            if (file.isEmpty()) {
                throw new RuntimeException("Falha ao salvar arquivo vazio.");
            }

            // 3. Gera um nome único usando UUID para evitar sobrescrita
            // Exemplo: 550e8400-e29b-41d4-a716-446655440000_certificado.pdf
            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            
            String newFileName = UUID.randomUUID().toString() + fileExtension;

            // 4. Define o caminho completo do arquivo
            Path destinationFile = this.rootLocation.resolve(
                    Paths.get(newFileName))
                    .normalize().toAbsolutePath();

            // 5. Copia o arquivo para a pasta de destino
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            // Retornamos apenas o nome do arquivo para salvar no banco de dados (campo urlArquivo)
            return newFileName;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao armazenar o arquivo: " + e.getMessage());
        }
    }

    // Método opcional para deletar arquivos caso uma submissão seja removida
    public void deleteFile(String fileName) {
        try {
            Path file = rootLocation.resolve(fileName);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao deletar arquivo: " + e.getMessage());
        }
    }
}