package com.senac.pi.entities;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.senac.pi.entities.enums.UserRole;

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
    private Integer horasAcumuladas = 0;

    @JsonIgnore
    @ManyToMany(mappedBy = "alunos")
    private Set<Curso> cursos = new HashSet<>();
    
    @JsonIgnore // Adicionado para evitar loop infinito no JSON
    @OneToMany(mappedBy = "aluno")
    private Set<Submissao> submissoes = new HashSet<>();

    public Aluno() {
    }

    public Aluno(Long id, String name, String email, String senhaHash, String matricula, String turma, Integer horasAcumuladas) {
        // Passando o UserRole.ALUNO para a classe pai
        super(id, name, email, senhaHash, UserRole.ALUNO); 
        this.matricula = matricula;
        this.turma = turma;
        this.horasAcumuladas = (horasAcumuladas == null) ? 0 : horasAcumuladas;
    }

    // Getters e Setters
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
    
    public Integer getHorasAcumuladas() {
        return horasAcumuladas;
    }

    public void setHorasAcumuladas(Integer horasAcumuladas) {
        this.horasAcumuladas = horasAcumuladas;
    }

    public Set<Curso> getCursos() {
        return cursos;
    }

    public Set<Submissao> getSubmissoes() {
        return submissoes;
    }

    // Métodos auxiliares
    public void addCurso(Curso curso) {
        cursos.add(curso);
    }

    public void removeCurso(Curso curso) {
        cursos.remove(curso);
    }
}