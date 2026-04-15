package com.senac.pi.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.senac.pi.entities.User;
import com.senac.pi.repositories.UserRepository;

@RestController
@RequestMapping(value = "/users")
public class RegraAtividadeResource {
	
	@Autowired
	private UserRepository repository;

	//buscar todos
	@GetMapping
	public ResponseEntity<List<User>> findAll(){
		List<User> list = repository.findAll();
		return ResponseEntity.ok().body(list);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id){
		User obj = repository.findById(id).orElseThrow();
		return ResponseEntity.ok().body(obj);
	}
	
	
}
