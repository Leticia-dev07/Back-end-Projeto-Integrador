package com.senac.pi.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.senac.pi.DTO.CertificadoDTO;
import com.senac.pi.entities.Certificado;
import com.senac.pi.repositories.CertificadoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CertificadoService {

    @Autowired
    private CertificadoRepository repository;

    @Transactional(readOnly = true)
    public List<CertificadoDTO> findAll() {
        return repository.findAll().stream().map(CertificadoDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CertificadoDTO findById(Long id) {
        Certificado entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Certificado não encontrado"));
        return new CertificadoDTO(entity);
    }
    
    // O insert do certificado geralmente é chamado pelo SubmissaoService
    @Transactional
    public Certificado insert(Certificado obj) {
        return repository.save(obj);
    }
}