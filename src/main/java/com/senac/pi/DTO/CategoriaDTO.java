package com.senac.pi.DTO;

import com.senac.pi.entities.Categoria;

public record CategoriaDTO(
    Long id,
    String area,
    Boolean exigeComprovante,
    Integer horasPorCertificado,
    Integer limiteSubmissoesSemestre,
    Long cursoId // Campo adicionado para o vínculo com o curso
) {
    public CategoriaDTO(Categoria entity) {
        this(
            entity.getId(),
            entity.getArea(),
            entity.getExigeComprovante(),
            entity.getHorasPorCertificado(),
            entity.getLimiteSubmissoesSemestre(),
            // Captura o ID do curso da entidade para o DTO
            entity.getCurso() != null ? entity.getCurso().getId() : null
        );
    }
}