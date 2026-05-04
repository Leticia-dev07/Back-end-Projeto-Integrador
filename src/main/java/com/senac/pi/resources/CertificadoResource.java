package com.senac.pi.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.senac.pi.DTO.CertificadoDTO;
import com.senac.pi.entities.Certificado;
import com.senac.pi.repositories.CertificadoRepository;
import com.senac.pi.services.CertificadoService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/certificados")
@CrossOrigin(origins = "*")
@Slf4j
public class CertificadoResource {

    @Autowired
    private CertificadoService service;

    @Autowired
    private CertificadoRepository repository;

    @GetMapping
    public ResponseEntity<List<CertificadoDTO>> findAll() {
        List<CertificadoDTO> list = service.findAll();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<CertificadoDTO> findById(@PathVariable Long id) {
        CertificadoDTO dto = service.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    // NOVA ROTA: Download / Visualização do arquivo salvo no Banco de Dados
    @GetMapping(value = "/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        Certificado cert = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Arquivo não encontrado"));

        return ResponseEntity.ok()
                // Define que o arquivo vai abrir na mesma aba (inline) usando o nome original
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + cert.getNomeArquivo() + "\"")
                // Define o Content-Type como PDF, PNG, etc para o navegador entender
                .contentType(MediaType.parseMediaType(cert.getTipoArquivo()))
                .body(cert.getDadosArquivo()); // Retorna os bytes do banco de dados
    }
}