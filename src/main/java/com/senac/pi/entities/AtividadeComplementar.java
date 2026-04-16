package com.senac.pi.entities;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_atividade_complementar")
public class AtividadeComplementar implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo;
    private String descricao;
    private Integer cargaHorariaSolicitada;

    // 🔥 RELAÇÃO COM SUBMISSÃO (1 atividade → várias submissões)
    @JsonIgnore
    @OneToMany(mappedBy = "atividade")
    private java.util.Set<Submissao> submissoes = new java.util.HashSet<>();

    public AtividadeComplementar() {
    }

    public AtividadeComplementar(Long id, String tipo, String descricao, Integer cargaHorariaSolicitada) {
        this.id = id;
        this.tipo = tipo;
        this.descricao = descricao;
        this.cargaHorariaSolicitada = cargaHorariaSolicitada;
    }

    public Long getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getCargaHorariaSolicitada() {
        return cargaHorariaSolicitada;
    }

    public java.util.Set<Submissao> getSubmissoes() {
        return submissoes;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setCargaHorariaSolicitada(Integer cargaHorariaSolicitada) {
        this.cargaHorariaSolicitada = cargaHorariaSolicitada;
    }

    public void setSubmissoes(java.util.Set<Submissao> submissoes) {
        this.submissoes = submissoes;
    }

    // ✅ Helpers
    public void addSubmissao(Submissao submissao) {
        submissoes.add(submissao);
    }

    public void removeSubmissao(Submissao submissao) {
        submissoes.remove(submissao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AtividadeComplementar)) return false;
        AtividadeComplementar other = (AtividadeComplementar) obj;
        return Objects.equals(id, other.id);
    }
}