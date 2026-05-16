package com.senac.pi.DTO;

import java.time.Instant;
import com.senac.pi.entities.Submissao;

public record SubmissaoDTO(
    Long id,
    Instant dataEnvio,
    String status,
    Integer horasAproveitadas,
    String observacaoCoordenador,
    String nomeAluno,
    String nomeCategoria,
    String urlCertificado
) {
    public SubmissaoDTO(Submissao entity) {
        this(
            entity.getId(),
            entity.getDataEnvio(),
            entity.getStatus() != null ? entity.getStatus().name() : "PENDENTE",
            entity.getHorasAproveitadas() != null ? entity.getHorasAproveitadas() : 0,
            entity.getObservacaoCoordenador(),
            entity.getAluno() != null ? entity.getAluno().getName() : "Não identificado",
            entity.getCategoria() != null ? entity.getCategoria().getArea() : "Sem categoria",
            
            //pega a URL diretamente da Submissao
            entity.getUrlArquivo()
        );
    }
}