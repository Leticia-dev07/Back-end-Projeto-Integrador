package com.senac.pi.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.senac.pi.entities.RegraAtividade;
import com.senac.pi.repositories.RegraAtividadeRepository;

@RestController
@RequestMapping(value = "/regraAtividades")
public class RegraAtividadeResource {

	@Autowired
	private RegraAtividadeRepository repository;

	// buscar todos
	@GetMapping
	public ResponseEntity<List<RegraAtividade>> findAll() {
		List<RegraAtividade> list = repository.findAll();
		return ResponseEntity.ok().body(list);
	}

	@GetMapping("/{id}")
	public ResponseEntity<RegraAtividade> findById(@PathVariable Long id) {
		RegraAtividade obj = repository.findById(id).orElseThrow();
		return ResponseEntity.ok().body(obj);
	}

}
