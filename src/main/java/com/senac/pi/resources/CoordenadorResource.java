package com.senac.pi.resources;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.senac.pi.DTO.CoordenadorDTO;
import com.senac.pi.entities.Coordenador;
import com.senac.pi.services.CoordenadorService;

@RestController
@RequestMapping("/coordenadores")
public class CoordenadorResource {

    @Autowired
    private CoordenadorService service;

    @GetMapping
    public ResponseEntity<List<CoordenadorDTO>> findAll() {
        List<CoordenadorDTO> list = service.findAll();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CoordenadorDTO> findById(@PathVariable Long id) {
        CoordenadorDTO dto = service.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping
    public ResponseEntity<CoordenadorDTO> insert(@RequestBody Coordenador obj) {
        CoordenadorDTO dto = service.insert(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.id()).toUri(); // No Record, acessamos .id() em vez de .getId()
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CoordenadorDTO> update(@PathVariable Long id, @RequestBody Coordenador obj) {
        CoordenadorDTO dto = service.update(id, obj);
        return ResponseEntity.ok().body(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}