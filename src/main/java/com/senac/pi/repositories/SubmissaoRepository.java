package com.senac.pi.repositories;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.senac.pi.entities.Submissao;

public interface SubmissaoRepository extends JpaRepository<Submissao, Long> {

    // Conta submissões de um aluno, em uma categoria, para um CURSO ESPECÍFICO, num período
    @Query("SELECT COUNT(obj) FROM Submissao obj WHERE obj.aluno.id = :alunoId " +
           "AND obj.curso.id = :cursoId " + 
           "AND obj.categoria.id = :categoriaId " +
           "AND obj.dataEnvio >= :dataInicio " +
           "AND obj.status != 3") 
    long countByAlunoAndCategoriaAndCursoInPeriod(Long alunoId, Long categoriaId, Long cursoId, Instant dataInicio);
    
    // Método útil para listar as submissões separadas por curso no Front-end (Histórico do Aluno):
    List<Submissao> findByAlunoIdAndCursoId(Long alunoId, Long cursoId);

    // NOVO: Método para listar todas as submissões de um curso específico (Painel do Coordenador)
    List<Submissao> findByCursoId(Long cursoId);
    
 // Método útil para listar TODAS as submissões de um aluno (independente do curso)
    List<Submissao> findByAlunoId(Long alunoId);
}