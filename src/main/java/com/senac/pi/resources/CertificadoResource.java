package com.senac.pi.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.senac.pi.DTO.CertificadoDTO;
import com.senac.pi.services.CertificadoService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/certificados")
@Slf4j
public class CertificadoResource {

    @Autowired
    private CertificadoService service;

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
}