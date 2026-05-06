package com.senac.pi.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    private static final Logger log = LoggerFactory.getLogger(FileService.class);

    private final Path rootLocation = Paths.get("uploads/certificados");

    public String saveFile(MultipartFile file) {
        log.info("### FILE ### Iniciando processo de upload de arquivo...");
        
        try {
            // 1. Cria o diretório se ele não existir
            if (!Files.exists(rootLocation)) {
                log.info("### FILE ### Diretório '{}' não encontrado. Criando pastas...", rootLocation);
                Files.createDirectories(rootLocation);
            }

            // 2. Valida se o arquivo está vazio
            if (file.isEmpty()) {
                log.warn("### FILE ### Tentativa de upload de arquivo vazio negada.");
                throw new RuntimeException("Falha ao salvar arquivo vazio.");
            }

            // 3. Gera um nome único usando UUID para evitar sobrescrita
            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            
            String newFileName = UUID.randomUUID().toString() + fileExtension;
            log.info("### FILE ### Arquivo recebido: '{}'. Nome gerado para armazenamento: '{}'", originalFileName, newFileName);

            // 4. Define o caminho completo do arquivo
            Path destinationFile = this.rootLocation.resolve(
                    Paths.get(newFileName))
                    .normalize().toAbsolutePath();

            // 5. Copia o arquivo para a pasta de destino
            log.info("### FILE ### Gravando arquivo no disco em: {}", destinationFile);
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            log.info("### FILE ### Arquivo salvo com sucesso no servidor.");
            
            // Retornamos apenas o nome do arquivo para salvar no banco de dados (campo urlArquivo)
            return newFileName;

        } catch (IOException e) {
            log.error("### FILE ### Erro crítico ao gravar arquivo: {}", e.getMessage());
            throw new RuntimeException("Erro ao armazenar o arquivo: " + e.getMessage());
        }
    }

    // Método opcional para deletar arquivos caso uma submissão seja removida
    public void deleteFile(String fileName) {
        log.info("### FILE ### Solicitada exclusão do arquivo: {}", fileName);
        try {
            Path file = rootLocation.resolve(fileName);
            boolean deleted = Files.deleteIfExists(file);
            
            if (deleted) {
                log.info("### FILE ### Arquivo '{}' removido fisicamente do disco.", fileName);
            } else {
                log.warn("### FILE ### Tentativa de exclusão falhou: Arquivo '{}' não foi localizado.", fileName);
            }
        } catch (IOException e) {
            log.error("### FILE ### Erro ao tentar deletar o arquivo '{}': {}", fileName, e.getMessage());
            throw new RuntimeException("Erro ao deletar arquivo: " + e.getMessage());
        }
    }
}