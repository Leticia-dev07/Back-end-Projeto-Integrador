package com.senac.pi.entities;

import java.io.Serializable;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_certificado")
public class Certificado implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeArquivo; // Nome original enviado pelo aluno
    private String urlArquivo;  // Link para acessar o arquivo (ex: /certificados/1/download)
    private String tipoArquivo; // MIME type (ex: application/pdf)

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    @JsonIgnore
    private byte[] dadosArquivo; // O arquivo real em bytes para o MySQL

    @JsonIgnore
    @OneToOne(mappedBy = "certificado")
    private Submissao submissao;

    public Certificado() {
    }

    public Certificado(Long id, String nomeArquivo, String urlArquivo, String tipoArquivo, byte[] dadosArquivo) {
        this.id = id;
        this.nomeArquivo = nomeArquivo;
        this.urlArquivo = urlArquivo;
        this.tipoArquivo = tipoArquivo;
        this.dadosArquivo = dadosArquivo;
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

    public String getTipoArquivo() {
        return tipoArquivo;
    }

    public void setTipoArquivo(String tipoArquivo) {
        this.tipoArquivo = tipoArquivo;
    }

    public byte[] getDadosArquivo() {
        return dadosArquivo;
    }

    public void setDadosArquivo(byte[] dadosArquivo) {
        this.dadosArquivo = dadosArquivo;
    }

    public Submissao getSubmissao() {
        return submissao;
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