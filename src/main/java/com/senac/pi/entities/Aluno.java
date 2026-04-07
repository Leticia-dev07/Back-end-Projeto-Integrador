package com.senac.pi.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_aluno")
public class Aluno extends User{
	
	private static final long serialVersionUID = 1L;
	private String matricula;
	private String turma;
	
	public Aluno(){
	}

	public Aluno(Long id, String name, String email, String senhaHash, String matricula, String turma) {
		super(id, name, email, senhaHash);
		this.matricula = matricula;
		this.setTurma(turma);
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public String getTurma() {
		return turma;
	}

	public void setTurma(String turma) {
		this.turma = turma;
	}
	
}
