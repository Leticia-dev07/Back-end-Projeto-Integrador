package com.senac.pi.entities;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_regra_atividade")
public class RegraAtividade implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String area;
    private Integer limiteHoras;
    private Boolean exigeComprovante;

    // 🔥 RELACIONAMENTO COM CURSO (OBRIGATÓRIO PELO DIAGRAMA)
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Curso curso;

    public RegraAtividade() {
    }

    public RegraAtividade(Long id, String area, Integer limiteHoras, Boolean exigeComprovante) {
        this.id = id;
        this.area = area;
        this.limiteHoras = limiteHoras;
        this.exigeComprovante = exigeComprovante;
    }

    public Long getId() {
        return id;
    }

    public String getArea() {
        return area;
    }

    public Integer getLimiteHoras() {
        return limiteHoras;
    }

    public Boolean getExigeComprovante() {
        return exigeComprovante;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setLimiteHoras(Integer limiteHoras) {
        this.limiteHoras = limiteHoras;
    }

    public void setExigeComprovante(Boolean exigeComprovante) {
        this.exigeComprovante = exigeComprovante;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof RegraAtividade)) return false;
        RegraAtividade other = (RegraAtividade) obj;
        return Objects.equals(id, other.id);
    }
}