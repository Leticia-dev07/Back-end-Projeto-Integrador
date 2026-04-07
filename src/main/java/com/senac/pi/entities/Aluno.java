package com.senac.pi.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_aluno")
public class Aluno extends User{
	
	private static final long serialVersionUID = 1L;
	private String matricula;
	
	public Aluno(){
	}

	public Aluno(Long id, String name, String email, String senhaHash, String matricula) {
		super(id, name, email, senhaHash);
		this.matricula = matricula;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}
	
}
