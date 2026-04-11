package com.senac.pi.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.senac.pi.entities.Submissao;
import com.senac.pi.repositories.SubmissaoRepository;

@RestController
@RequestMapping(value = "/submissaos")
public class SubmissaoResource {

	@Autowired
	private SubmissaoRepository repository;

	// buscar todos
	@GetMapping
	public ResponseEntity<List<Submissao>> findAll() {
		List<Submissao> list = repository.findAll();
		return ResponseEntity.ok().body(list);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Submissao> findById(@PathVariable Long id) {
		Submissao obj = repository.findById(id).orElseThrow();
		return ResponseEntity.ok().body(obj);
	}

}
