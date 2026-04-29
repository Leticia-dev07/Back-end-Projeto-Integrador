package com.senac.pi.resources;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.senac.pi.DTO.SubmissaoDTO;
import com.senac.pi.entities.Submissao;
import com.senac.pi.services.SubmissaoService;

@RestController
@RequestMapping(value = "/submissoes")
public class SubmissaoResource {

    @Autowired
    private SubmissaoService service;

    @GetMapping
    public ResponseEntity<List<SubmissaoDTO>> findAll() {
        List<SubmissaoDTO> list = service.findAll();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<SubmissaoDTO> findById(@PathVariable Long id) {
        SubmissaoDTO dto = service.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    // ATUALIZADO: Agora aceita Multipart (JSON + Arquivo)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SubmissaoDTO> insert(
            @RequestPart("submissao") Submissao obj, 
            @RequestPart("file") MultipartFile file) {
        
        // Agora chamamos o service passando os dois parâmetros
        SubmissaoDTO dto = service.insert(obj, file);
        
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping(value = "/{id}/aprovar")
    public ResponseEntity<SubmissaoDTO> aprovar(@PathVariable Long id) {
        SubmissaoDTO dto = service.aprovar(id);
        return ResponseEntity.ok().body(dto);
    }

    @PutMapping(value = "/{id}/rejeitar")
    public ResponseEntity<SubmissaoDTO> rejeitar(@PathVariable Long id, @RequestBody String observacao) {
        SubmissaoDTO dto = service.rejeitar(id, observacao);
        return ResponseEntity.ok().body(dto);
    }
}