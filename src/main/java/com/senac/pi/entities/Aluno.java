package com.senac.pi.entities;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_aluno")
public class Aluno extends User {

    private static final long serialVersionUID = 1L;

    private String matricula;
    private String turma;

    @JsonIgnore
    @ManyToMany(mappedBy = "alunos")
    private Set<Curso> cursos = new HashSet<>();
    
    @OneToMany(mappedBy = "aluno")
    private Set<Submissao> submissões = new HashSet<>();

    public Aluno() {
    }

    public Aluno(Long id, String name, String email, String senhaHash, String matricula, String turma) {
        super(id, name, email, senhaHash);
        this.matricula = matricula;
        this.turma = turma;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getTurma() {
        return turma;
    }

    public void setTurma(String turma) {
        this.turma = turma;
    }

    // ✅ Getter importante
    public Set<Curso> getCursos() {
        return cursos;
    }

    // ✅ Métodos auxiliares (boa prática)
    public void addCurso(Curso curso) {
        cursos.add(curso);
    }

    public void removeCurso(Curso curso) {
        cursos.remove(curso);
    }
}