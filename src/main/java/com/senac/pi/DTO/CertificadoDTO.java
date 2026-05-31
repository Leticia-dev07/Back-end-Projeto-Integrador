package com.senac.pi.DTO;

import com.senac.pi.entities.Certificado;

public record CertificadoDTO(
    Long id,
    String nomeAlunoOcr,
    String nomeCursoOcr,
    Integer cargaHorariaOcr,
    String dataConclusaoOcr
) {
    public CertificadoDTO(Certificado entity) {
        this(
            entity.getId(),
            entity.getNomeAlunoOcr(),
            entity.getNomeCursoOcr(),
            entity.getCargaHorariaOcr(),
            entity.getDataConclusaoOcr()
        );
    }
}