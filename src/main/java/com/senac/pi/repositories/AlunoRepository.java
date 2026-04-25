package com.senac.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.senac.pi.entities.Aluno;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    
    // Verifica se o aluno já possui o curso em sua lista de cursos
    @Query("SELECT COUNT(a) > 0 FROM Aluno a JOIN a.cursos c WHERE a.id = :alunoId AND c.id = :cursoId")
    boolean existsByAlunoIdAndCursoId(Long alunoId, Long cursoId);
}