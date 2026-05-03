package com.senac.pi.DTO;

import com.senac.pi.entities.Coordenador;
import com.senac.pi.entities.Curso;

public record CursoDTO(
    Long id,
    String nome,
    String descricao,
    Integer cargaHorariaMax,
    Coordenador coordenador // Adicionamos este campo
) {
    // Construtor para converter Entity -> DTO
    public CursoDTO(Curso entity) {
        this(
            entity.getId(),
            entity.getNome(),
            entity.getDescricao(),
            entity.getCargaHorariaMax(),
            // Pegamos o primeiro coordenador do Set (se existir)
            entity.getCoordenadores().isEmpty() ? null : entity.getCoordenadores().iterator().next()
        );
    }
}