package com.senac.pi.DTO;

import com.senac.pi.entities.Categoria;

public record CategoriaDTO(
    Long id,
    String area,
    Boolean exigeComprovante,
    Integer horasPorCertificado,
    Integer limiteSubmissoesSemestre
) {
    public CategoriaDTO(Categoria entity) {
        this(
            entity.getId(),
            entity.getArea(),
            entity.getExigeComprovante(),
            entity.getHorasPorCertificado(),
            entity.getLimiteSubmissoesSemestre()
        );
    }
}