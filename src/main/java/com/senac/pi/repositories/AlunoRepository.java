package com.senac.pi.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.senac.pi.entities.Aluno;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

	// O Spring gera a query automaticamente pelo nome do método
	Optional<Aluno> findByEmail(String email);

	Optional<Aluno> findByMatricula(String matricula);
}
