package com.senac.pi.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.senac.pi.entities.NotificacaoEmail;
import com.senac.pi.repositories.NotificacaoEmailRepository;

@RestController
@RequestMapping(value = "/notificacaoEmail")
public class NotificacaoEmailResource {
	
	@Autowired
	private NotificacaoEmailRepository repository;

	//buscar todos
	@GetMapping
	public ResponseEntity<List<NotificacaoEmail>> findAll(){
		List<NotificacaoEmail> list = repository.findAll();
		return ResponseEntity.ok().body(list);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<NotificacaoEmail> findById(@PathVariable Long id){
		NotificacaoEmail obj = repository.findById(id).orElseThrow();
		return ResponseEntity.ok().body(obj);
	}
	
	
}
