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

import com.senac.pi.DTO.AlunoDTO;
import com.senac.pi.services.AlunoService;

@RestController
@RequestMapping(value = "/alunos")
@CrossOrigin(origins = "*")
public class AlunoResource {

    @Autowired
    private AlunoService service;

    /**
     * Retorna a lista de todos os alunos cadastrados no sistema.
     */
    @GetMapping
    public ResponseEntity<List<AlunoDTO>> findAll() {
        List<AlunoDTO> list = service.findAll();
        return ResponseEntity.ok().body(list);
    }

    /**
     * Busca um aluno por ID.
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<AlunoDTO> findById(@PathVariable Long id) {
        AlunoDTO dto = service.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    /**
     * NOVO: Retorna a lista de alunos vinculados a um curso específico.
     * Utilizado para alimentar a tabela/lista na tela de Perfil do Curso.
     */
    @GetMapping(value = "/curso/{cursoId}")
    public ResponseEntity<List<AlunoDTO>> findByCurso(@PathVariable Long cursoId) {
        List<AlunoDTO> list = service.findByCurso(cursoId);
        return ResponseEntity.ok().body(list);
    }

    /**
     * Insere um novo aluno sem vínculo imediato a um curso.
     */
    @PostMapping
    public ResponseEntity<AlunoDTO> insert(@RequestBody AlunoDTO dto) {
        dto = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    /**
     * Insere um novo aluno e já o vincula a um curso específico.
     * Endpoint usado pelo formulário de cadastro dentro do contexto de um curso.
     */
    @PostMapping(value = "/curso/{cursoId}")
    public ResponseEntity<AlunoDTO> insertComCurso(@PathVariable Long cursoId, @RequestBody AlunoDTO dto) {
        dto = service.insertComCurso(cursoId, dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    /**
     * Realiza a matrícula de um aluno já existente em um curso.
     */
    @PostMapping(value = "/{alunoId}/cursos/{cursoId}")
    public ResponseEntity<Void> matricularEmCurso(@PathVariable Long alunoId, @PathVariable Long cursoId) {
        service.matricularEmCurso(alunoId, cursoId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Atualiza dados de um aluno existente.
     */
    @PutMapping(value = "/{id}")
    public ResponseEntity<AlunoDTO> update(@PathVariable Long id, @RequestBody AlunoDTO dto) {
        dto = service.update(id, dto);
        return ResponseEntity.ok().body(dto);
    }

    /**
     * Deleta um aluno por ID.
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}