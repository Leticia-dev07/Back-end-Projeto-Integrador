package com.senac.pi.entities;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_certificado")
public class Certificado implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeArquivo;
    private String urlArquivo;
    private Boolean processadoOcr;

    // 🔥 lado inverso do relacionamento
    @JsonIgnore
    @OneToOne(mappedBy = "certificado")
    private Submissao submissao;

    public Certificado() {
    }

    public Certificado(Long id, String nomeArquivo, String urlArquivo, Boolean processadoOcr) {
        this.id = id;
        this.nomeArquivo = nomeArquivo;
        this.urlArquivo = urlArquivo;
        this.processadoOcr = processadoOcr;
    }

    public Long getId() {
        return id;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public String getUrlArquivo() {
        return urlArquivo;
    }

    public Boolean getProcessadoOcr() {
        return processadoOcr;
    }

    public Submissao getSubmissao() {
        return submissao;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public void setUrlArquivo(String urlArquivo) {
        this.urlArquivo = urlArquivo;
    }

    public void setProcessadoOcr(Boolean processadoOcr) {
        this.processadoOcr = processadoOcr;
    }

    public void setSubmissao(Submissao submissao) {
        this.submissao = submissao;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Certificado)) return false;
        Certificado other = (Certificado) obj;
        return Objects.equals(id, other.id);
    }
}