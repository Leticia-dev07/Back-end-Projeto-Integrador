package com.senac.pi.resources;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.senac.pi.entities.Aluno;
import com.senac.pi.repositories.AlunoRepository;

@RestController
@RequestMapping("/alunos")
public class AlunoResource {

    @Autowired
    private AlunoRepository repository;

    @GetMapping
    public ResponseEntity<List<Aluno>> findAll() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aluno> findById(@PathVariable Long id) {
        Optional<Aluno> aluno = repository.findById(id);
        return aluno.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Aluno> insert(@RequestBody Aluno aluno) {
        aluno = repository.save(aluno);
        URI uri = URI.create("/alunos/" + aluno.getId());
        return ResponseEntity.created(uri).body(aluno);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Aluno> update(@PathVariable Long id, @RequestBody Aluno obj) {
        Optional<Aluno> optional = repository.findById(id);

        if (optional.isPresent()) {
            Aluno entity = optional.get();

            entity.setName(obj.getName());
            entity.setEmail(obj.getEmail());
            entity.setSenhaHash(obj.getSenhaHash());
            entity.setMatricula(obj.getMatricula());
            entity.setTurma(obj.getTurma());

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