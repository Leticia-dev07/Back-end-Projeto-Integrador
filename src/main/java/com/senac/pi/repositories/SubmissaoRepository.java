package com.senac.pi.repositories;

import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.senac.pi.entities.Submissao;

public interface SubmissaoRepository extends JpaRepository<Submissao, Long> {

    // Conta submissões de um aluno, em uma categoria, num período, que não foram rejeitadas
    @Query("SELECT COUNT(obj) FROM Submissao obj WHERE obj.aluno.id = :alunoId " +
           "AND obj.categoria.id = :categoriaId " +
           "AND obj.dataEnvio >= :dataInicio " +
           "AND obj.status != 3") // 3 é o código para REJEITADO no nosso Enum
    long countByAlunoAndCategoriaInPeriod(Long alunoId, Long categoriaId, Instant dataInicio);
}