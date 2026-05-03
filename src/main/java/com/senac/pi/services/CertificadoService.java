package com.senac.pi.services;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.senac.pi.DTO.CertificadoDTO;
import com.senac.pi.entities.Certificado;
import com.senac.pi.repositories.CertificadoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CertificadoService {

    // Logger manual para evitar erros de compilação na IDE
    private static final Logger log = LoggerFactory.getLogger(CertificadoService.class);

    @Autowired
    private CertificadoRepository repository;

    @Transactional(readOnly = true)
    public List<CertificadoDTO> findAll() {
        log.info("Buscando todos os certificados registrados no sistema...");
        List<Certificado> list = repository.findAll();
        log.info("{} certificados recuperados com sucesso.", list.size());
        return list.stream().map(CertificadoDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CertificadoDTO findById(Long id) {
        log.info("Consultando certificado ID: {}", id);
        Certificado entity = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Erro: Certificado com ID {} não existe.", id);
                    return new EntityNotFoundException("Certificado não encontrado");
                });
        return new CertificadoDTO(entity);
    }
    
    // O insert do certificado geralmente é chamado pelo SubmissaoService
    @Transactional
    public Certificado insert(Certificado obj) {
        log.info("Iniciando a persistência de um novo certificado no banco de dados...");
        try {
            Certificado savedObj = repository.save(obj);
            log.info("Certificado salvo com sucesso! ID Gerado: {}", savedObj.getId());
            return savedObj;
        } catch (Exception e) {
            log.error("Falha técnica ao salvar certificado: {}", e.getMessage());
            throw e;
        }
    }
}