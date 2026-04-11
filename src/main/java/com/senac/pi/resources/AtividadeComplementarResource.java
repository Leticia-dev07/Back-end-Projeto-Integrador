package com.senac.pi.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.senac.pi.entities.AtividadeComplementar;
import com.senac.pi.repositories.AtividadeComplementarRepository;

@RestController
@RequestMapping(value = "/atividadeComplementars")
public class AtividadeComplementarResource {
	
	@Autowired
	private AtividadeComplementarRepository repository;

	//buscar todos
	@GetMapping
	public ResponseEntity<List<AtividadeComplementar>> findAll(){
		List<AtividadeComplementar> list = repository.findAll();
		return ResponseEntity.ok().body(list);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<AtividadeComplementar> findById(@PathVariable Long id){
		AtividadeComplementar obj = repository.findById(id).orElseThrow();
		return ResponseEntity.ok().body(obj);
	}
	
	
}
