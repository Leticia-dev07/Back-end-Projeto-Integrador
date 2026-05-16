package com.senac.pi.resources;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.senac.pi.DTO.CoordenadorDTO;
import com.senac.pi.services.CoordenadorService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/coordenadores")
@CrossOrigin(origins = "*")
@Slf4j
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
    public ResponseEntity<CoordenadorDTO> insert(@RequestBody CoordenadorDTO dto) {
        CoordenadorDTO newDto = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(newDto.id()).toUri(); 
        return ResponseEntity.created(uri).body(newDto);
    }

    @PostMapping(value = "/{coordId}/cursos/{cursoId}")
    public ResponseEntity<Void> vincularCurso(@PathVariable Long coordId, @PathVariable Long cursoId) {
        service.vincularCurso(coordId, cursoId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint para desvincular um coordenador específico de um curso específico.
     */
    @DeleteMapping(value = "/{coordId}/cursos/{cursoId}")
    public ResponseEntity<Void> desvincularCurso(@PathVariable Long coordId, @PathVariable Long cursoId) {
        service.desvincularCurso(coordId, cursoId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CoordenadorDTO> update(@PathVariable Long id, @RequestBody CoordenadorDTO dto) {
        CoordenadorDTO updatedDto = service.update(id, dto);
        return ResponseEntity.ok().body(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}