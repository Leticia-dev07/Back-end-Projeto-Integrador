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
	    String urlCertificado,
	    String nomeCurso,
	    CertificadoDTO dadosOcr
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
	            entity.getUrlArquivo(),
	            entity.getCurso() != null ? entity.getCurso().getNome() : "Sem curso",
	            entity.getCertificado() != null ? new CertificadoDTO(entity.getCertificado()) : null
	        );
	    }
	}