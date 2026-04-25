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
            // Verifica se o status existe para não dar erro
            entity.getStatus() != null ? entity.getStatus().name() : "PENDENTE",
            
            // Verifica as horas
            entity.getHorasAproveitadas() != null ? entity.getHorasAproveitadas() : 0,
            
            entity.getObservacaoCoordenador(),
            
            // Usa o getName() da classe User que você mandou
            entity.getAluno() != null ? entity.getAluno().getName() : "Não identificado",
            
            // Usa o getArea() da classe Categoria que você mandou
            entity.getCategoria() != null ? entity.getCategoria().getArea() : "Sem categoria",
            
            // Verifica o certificado
            entity.getCertificado() != null ? entity.getCertificado().getUrlArquivo() : null
        );
    }
}