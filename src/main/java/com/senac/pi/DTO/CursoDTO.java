package com.senac.pi.DTO;

import com.senac.pi.entities.Curso;

public record CursoDTO(
    Long id,
    String nome,
    String descricao,
    Integer cargaHorariaMax
) {
    // Construtor para converter Entity -> DTO
    public CursoDTO(Curso entity) {
        this(
            entity.getId(),
            entity.getNome(),
            entity.getDescricao(),
            entity.getCargaHorariaMax()
        );
    }
}