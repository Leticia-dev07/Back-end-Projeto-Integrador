package com.senac.pi.resources;

	import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.senac.pi.entities.Aluno;
import com.senac.pi.repositories.AlunoRepository;


	@RestController
	@RequestMapping("/alunos")
	public class AlunoResource {

	    @Autowired
	    private AlunoRepository repository;

	    @GetMapping
	    public ResponseEntity<List<Aluno>> findAll() {
	        List<Aluno> list = repository.findAll()
	                .stream()
	                .filter(u -> u instanceof Aluno)
	                .map(u -> (Aluno) u)
	                .toList();

	        return ResponseEntity.ok().body(list);
	    }

	    @PostMapping
	    public ResponseEntity<Aluno> insert(@RequestBody Aluno aluno) {
	        aluno = repository.save(aluno);
	        return ResponseEntity.ok().body(aluno);
	    }
	}

