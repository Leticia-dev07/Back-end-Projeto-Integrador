package com.senac.pi.entities;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "tb_certificado_ocr")
public class Certificado implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeAlunoOcr;
    private String nomeCursoOcr;
    private Integer cargaHorariaOcr;
    private String dataConclusaoOcr;

    @JsonIgnore // Evita loop infinito caso o objeto seja serializado diretamente
    @OneToOne
    @JoinColumn(name = "submissao_id")
    private Submissao submissao;

    public Certificado() {
    }

    public Certificado(Long id, String nomeAlunoOcr, String nomeCursoOcr, Integer cargaHorariaOcr, 
                       String dataConclusaoOcr, Submissao submissao) {
        this.id = id;
        this.nomeAlunoOcr = nomeAlunoOcr;
        this.nomeCursoOcr = nomeCursoOcr;
        this.cargaHorariaOcr = cargaHorariaOcr;
        this.dataConclusaoOcr = dataConclusaoOcr;
        this.submissao = submissao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeAlunoOcr() {
        return nomeAlunoOcr;
    }

    public void setNomeAlunoOcr(String nomeAlunoOcr) {
        this.nomeAlunoOcr = nomeAlunoOcr;
    }

    public String getNomeCursoOcr() {
        return nomeCursoOcr;
    }

    public void setNomeCursoOcr(String nomeCursoOcr) {
        this.nomeCursoOcr = nomeCursoOcr;
    }

    public Integer getCargaHorariaOcr() {
        return cargaHorariaOcr;
    }

    public void setCargaHorariaOcr(Integer cargaHorariaOcr) {
        this.cargaHorariaOcr = cargaHorariaOcr;
    }

    public String getDataConclusaoOcr() {
        return dataConclusaoOcr;
    }

    public void setDataConclusaoOcr(String dataConclusaoOcr) {
        this.dataConclusaoOcr = dataConclusaoOcr;
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
        if (obj == null || getClass() != obj.getClass()) return false;
        Certificado other = (Certificado) obj;
        return Objects.equals(id, other.id);
    }
}