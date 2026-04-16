package com.senac.pi.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_curso")
public class Curso implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private Integer cargaHorariaMax;

    @JsonIgnore // ⚠️ evita loop infinito
    @ManyToMany
    @JoinTable(
        name = "tb_curso_aluno",
        joinColumns = @JoinColumn(name = "curso_id"),
        inverseJoinColumns = @JoinColumn(name = "aluno_id")
    )
    private Set<Aluno> alunos = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "tb_curso_coordenador",
        joinColumns = @JoinColumn(name = "curso_id"),
        inverseJoinColumns = @JoinColumn(name = "coordenador_id")
    )
    private Set<Coordenador> coordenadores = new HashSet<>();
    
    @OneToMany(mappedBy = "curso")
    private Set<RegraAtividade> regras = new HashSet<>();

    public Curso() {
    }

    public Curso(Long id, String nome, Integer cargaHorariaMax) {
        this.id = id;
        this.nome = nome;
        this.cargaHorariaMax = cargaHorariaMax;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
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

    public void setCargaHorariaMax(Integer cargaHorariaMax) {
        this.cargaHorariaMax = cargaHorariaMax;
    }

    // ✅ Getters importantes
    public Set<Aluno> getAlunos() {
        return alunos;
    }

    public Set<Coordenador> getCoordenadores() {
        return coordenadores;
    }
    
    public Set<RegraAtividade> getRegras() {
        return regras;
    }

    // ✅ Métodos auxiliares
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