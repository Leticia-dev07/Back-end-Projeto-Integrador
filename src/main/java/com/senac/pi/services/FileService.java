package com.senac.pi.services;

import java.io.IOException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class FileService {

    private static final Logger log = LoggerFactory.getLogger(FileService.class);

    @Autowired
    private S3Client s3Client;

    @Value("${storage.s3.bucket-name}")
    private String bucketName;

    @Value("${storage.s3.project-id}")
    private String projectId;

    public String saveFile(MultipartFile file) {
        log.info("### S3 ### Iniciando upload para o projeto: {}", projectId);

        try {
            String originalFileName = file.getOriginalFilename();
            String fileExtension = (originalFileName != null && originalFileName.contains(".")) 
                ? originalFileName.substring(originalFileName.lastIndexOf(".")) : "";
            
            String newFileName = UUID.randomUUID().toString() + fileExtension;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(newFileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            
            // URL Pública formatada para o seu projeto no Supabase
            return String.format("https://%s.supabase.co/storage/v1/object/public/%s/%s", 
                    projectId, bucketName, newFileName);

        } catch (IOException e) {
            log.error("### S3 ### Erro crítico no upload: {}", e.getMessage());
            throw new RuntimeException("Erro ao processar arquivo para o S3", e);
        }
    }
}