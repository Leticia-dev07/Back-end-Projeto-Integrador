package com.senac.pi.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_super_admin")
public class SuperAdmin extends User {

	private static final long serialVersionUID = 1L;

	public SuperAdmin() {
	}

	public SuperAdmin(Long id, String name, String email, String senhaHash) {
		super(id, name, email, senhaHash);
	}
	
	
	
}
