package com.senac.pi.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_coordenador")
public class Coordenador extends User {

	private static final long serialVersionUID = 1L;

	public Coordenador() {
	}

	public Coordenador(Long id, String name, String email, String senhaHash) {
		super(id, name, email, senhaHash);
	}
	
	
}
