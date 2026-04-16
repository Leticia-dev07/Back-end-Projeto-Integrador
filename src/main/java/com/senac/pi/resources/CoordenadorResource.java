package com.senac.pi.resources;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.senac.pi.entities.Coordenador;
import com.senac.pi.repositories.CoordenadorRepository;

@RestController
@RequestMapping("/coordenadores")
public class CoordenadorResource {

    @Autowired
    private CoordenadorRepository repository;

    // 🔹 Buscar todos
    @GetMapping
    public ResponseEntity<List<Coordenador>> findAll() {
        return ResponseEntity.ok(repository.findAll());
    }

    // 🔹 Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<Coordenador> findById(@PathVariable Long id) {
        Optional<Coordenador> obj = repository.findById(id);
        return obj.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Inserir
    @PostMapping
    public ResponseEntity<Coordenador> insert(@RequestBody Coordenador obj) {
        obj = repository.save(obj);
        URI uri = URI.create("/coordenadores/" + obj.getId());
        return ResponseEntity.created(uri).body(obj);
    }

    // 🔹 Atualizar
    @PutMapping("/{id}")
    public ResponseEntity<Coordenador> update(@PathVariable Long id, @RequestBody Coordenador obj) {
        Optional<Coordenador> optional = repository.findById(id);

        if (optional.isPresent()) {
            Coordenador entity = optional.get();

            entity.setName(obj.getName());
            entity.setEmail(obj.getEmail());
            entity.setSenhaHash(obj.getSenhaHash());

            return ResponseEntity.ok(repository.save(entity));
        }

        return ResponseEntity.notFound().build();
    }

    // 🔹 Deletar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}