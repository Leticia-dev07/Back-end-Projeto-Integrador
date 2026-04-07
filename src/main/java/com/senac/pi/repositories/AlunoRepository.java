package com.senac.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.senac.pi.entities.Aluno;

public interface AlunoRepository extends JpaRepository<Aluno, Long >{

}
