package com.senac.pi.entities;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_certificado")
public class Certificado implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nomeArquivo;
	private String urlArquivo;
	private Boolean processadoOcr;
	
	public Certificado() {
	}

	public Certificado(Long id, String nomeArquivo, String urlArquivo, Boolean processadoOcr) {
		super();
		this.id = id;
		this.nomeArquivo = nomeArquivo;
		this.urlArquivo = urlArquivo;
		this.processadoOcr = processadoOcr;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public String getUrlArquivo() {
		return urlArquivo;
	}

	public void setUrlArquivo(String urlArquivo) {
		this.urlArquivo = urlArquivo;
	}

	public Boolean getProcessadoOcr() {
		return processadoOcr;
	}

	public void setProcessadoOcr(Boolean processadoOcr) {
		this.processadoOcr = processadoOcr;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Certificado other = (Certificado) obj;
		return Objects.equals(id, other.id);
	}
	
	
	
}
