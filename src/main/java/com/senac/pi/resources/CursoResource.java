package com.senac.pi.resources;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.senac.pi.entities.Curso;
import com.senac.pi.repositories.CursoRepository;

@RestController
@RequestMapping("/cursos")
public class CursoResource {

    @Autowired
    private CursoRepository repository;

    @GetMapping
    public ResponseEntity<List<Curso>> findAll() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Curso> findById(@PathVariable Long id) {
        Optional<Curso> curso = repository.findById(id);
        return curso.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Curso> insert(@RequestBody Curso curso) {
        curso = repository.save(curso);
        URI uri = URI.create("/cursos/" + curso.getId());
        return ResponseEntity.created(uri).body(curso);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Curso> update(@PathVariable Long id, @RequestBody Curso obj) {
        Optional<Curso> optional = repository.findById(id);

        if (optional.isPresent()) {
            Curso entity = optional.get();

            entity.setNome(obj.getNome());
            entity.setCargaHorariaMax(obj.getCargaHorariaMax());

            return ResponseEntity.ok(repository.save(entity));
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}