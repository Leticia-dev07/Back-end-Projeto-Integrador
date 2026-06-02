package com.senac.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.senac.pi.entities.Categoria;
import com.senac.pi.entities.Curso;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    boolean existsByAreaAndCurso(String area, Curso curso);
    List<Categoria> findByCursoId(Long cursoId);  
}
