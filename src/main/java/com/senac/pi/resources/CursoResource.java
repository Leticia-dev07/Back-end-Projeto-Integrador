package com.senac.pi.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.senac.pi.entities.Curso;
import com.senac.pi.repositories.CursoRepository;

@RestController
@RequestMapping(value = "/cursos")
public class CursoResource {

	@Autowired
	private CursoRepository repository;

	// buscar todos
	@GetMapping
	public ResponseEntity<List<Curso>> findAll() {
		List<Curso> list = repository.findAll();
		return ResponseEntity.ok().body(list);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Curso> findById(@PathVariable Long id) {
		Curso obj = repository.findById(id).orElseThrow();
		return ResponseEntity.ok().body(obj);
	}

}
