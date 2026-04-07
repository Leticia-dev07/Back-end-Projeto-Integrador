package com.senac.pi.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.senac.pi.entities.SuperAdmin;
import com.senac.pi.repositories.UserRepository;

@RestController
@RequestMapping("/admins")
public class SuperAdminResource {

	@Autowired
	private UserRepository repository;

	@PostMapping
	public ResponseEntity<SuperAdmin> insert(@RequestBody SuperAdmin obj) {
		obj = repository.save(obj);
		return ResponseEntity.ok().body(obj);
	}
}
