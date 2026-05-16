package com.senac.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.senac.pi.entities.Curso;

public interface CursoRepository extends JpaRepository<Curso, Long> {
    
    boolean existsByNome(String nome);
}