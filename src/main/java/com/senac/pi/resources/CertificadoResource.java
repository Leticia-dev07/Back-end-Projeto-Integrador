package com.senac.pi.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.senac.pi.entities.Certificado;
import com.senac.pi.repositories.CertificadoRepository;

@RestController
@RequestMapping(value = "/certificados")
public class CertificadoResource {
	
	@Autowired
	private CertificadoRepository repository;

	//buscar todos
	@GetMapping
	public ResponseEntity<List<Certificado>> findAll(){
		List<Certificado> list = repository.findAll();
		return ResponseEntity.ok().body(list);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Certificado> findById(@PathVariable Long id){
		Certificado obj = repository.findById(id).orElseThrow();
		return ResponseEntity.ok().body(obj);
	}
	
	
}
