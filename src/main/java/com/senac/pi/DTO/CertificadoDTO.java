package com.senac.pi.DTO;

import com.senac.pi.entities.Certificado;

public record CertificadoDTO(
    Long id,
    String nomeArquivo,
    String urlArquivo
) {
    public CertificadoDTO(Certificado entity) {
        this(entity.getId(), entity.getNomeArquivo(), entity.getUrlArquivo());
    }
}