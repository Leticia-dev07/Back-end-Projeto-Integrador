package com.senac.pi.DTO;

import com.senac.pi.entities.Aluno;

public record AlunoDTO(
    Long id,
    String name,
    String email,
    String matricula,
    String turma,
    Integer horasAcumuladas,
    String senha
) {
    public AlunoDTO(Aluno entity) {
        this(
            entity.getId(),
            entity.getName(),
            entity.getEmail(),
            entity.getMatricula(),
            entity.getTurma(),
            entity.getHorasAcumuladas(),
            null
        );
    }
}