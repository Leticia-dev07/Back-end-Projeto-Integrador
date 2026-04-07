package com.senac.pi.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.senac.pi.entities.Coordenador;
import com.senac.pi.repositories.UserRepository;

@RestController
@RequestMapping("/coordenadores")
public class CoordenadorResource {

	@Autowired
	private UserRepository repository;

	@PostMapping
	public ResponseEntity<Coordenador> insert(@RequestBody Coordenador obj) {
		obj = repository.save(obj);
		return ResponseEntity.ok().body(obj);
	}
}
