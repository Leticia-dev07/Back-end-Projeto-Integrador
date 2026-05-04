package com.senac.pi.repositories;

import java.util.List;
import java.util.Optional; // <-- Nova importação

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.senac.pi.entities.Aluno;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    
    /**
     * Verifica se o aluno já possui o curso em sua lista de cursos (tabela de associação).
     */
    @Query("SELECT COUNT(a) > 0 FROM Aluno a JOIN a.cursos c WHERE a.id = :alunoId AND c.id = :cursoId")
    boolean existsByAlunoIdAndCursoId(@Param("alunoId") Long alunoId, @Param("cursoId") Long cursoId);

    /**
     * Busca todos os alunos vinculados a um curso específico.
     * Utilizado para listar os alunos na página de Perfil do Curso.
     */
    @Query("SELECT a FROM Aluno a JOIN a.cursos c WHERE c.id = :cursoId")
    List<Aluno> findByCursoId(@Param("cursoId") Long cursoId);

    // --- VERIFICAÇÕES DE DUPLICIDADE ---

    /**
     * NOVO: Busca o aluno retornando um Optional para tratarmos no Service na hora de vincular
     */
    Optional<Aluno> findByEmail(String email);

    /**
     * Verifica se já existe um aluno cadastrado com este e-mail.
     */
    boolean existsByEmail(String email);

    /**
     * Verifica se já existe um aluno cadastrado com esta matrícula.
     */
    boolean existsByMatricula(String matricula);
}