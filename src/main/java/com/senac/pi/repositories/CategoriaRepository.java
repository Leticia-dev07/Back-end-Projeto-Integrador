package com.senac.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.senac.pi.entities.Categoria;
import com.senac.pi.entities.Curso;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
	// O Spring JPA consegue comparar o objeto Curso automaticamente pelo ID dele
	boolean existsByAreaAndCurso(String area, Curso curso);
}