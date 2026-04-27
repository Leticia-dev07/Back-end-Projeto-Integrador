package com.senac.pi.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_curso")
public class Curso implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nome;
    
    private String descricao;

    private Integer cargaHorariaMax;

    @JsonIgnore
    @ManyToMany(mappedBy = "cursos")
    private Set<Aluno> alunos = new HashSet<>();
    
    @ManyToMany
    @JoinTable(
        name = "tb_curso_coordenador",
        joinColumns = @JoinColumn(name = "curso_id"),
        inverseJoinColumns = @JoinColumn(name = "coordenador_id")
    )
    private Set<Coordenador> coordenadores = new HashSet<>();
    
    @OneToMany(mappedBy = "curso")
    private Set<Categoria> categoria = new HashSet<>();

    public Curso() {
    }

    public Curso(Long id, String nome, String descricao, Integer cargaHorariaMax) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.cargaHorariaMax = cargaHorariaMax;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getCargaHorariaMax() {
        return cargaHorariaMax;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setCargaHorariaMax(Integer cargaHorariaMax) {
        this.cargaHorariaMax = cargaHorariaMax;
    }

    public Set<Aluno> getAlunos() {
        return alunos;
    }

    public Set<Coordenador> getCoordenadores() {
        return coordenadores;
    }
    
    public Set<Categoria> getCategoria() {
        return categoria;
    }

    public void addAluno(Aluno aluno) {
        alunos.add(aluno);
    }

    public void removeAluno(Aluno aluno) {
        alunos.remove(aluno);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Curso)) return false;
        Curso other = (Curso) obj;
        return Objects.equals(id, other.id);
    }
}