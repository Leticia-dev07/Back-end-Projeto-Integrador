package com.senac.pi.entities;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_coordenador")
public class Coordenador extends User {

    private static final long serialVersionUID = 1L;

    @JsonIgnore
    @ManyToMany(mappedBy = "coordenadores")
    private Set<Curso> cursos = new HashSet<>();
    
    @OneToMany(mappedBy = "coordenador")
    private Set<Submissao> submissoes = new HashSet<>();

    public Coordenador() {
    }

    public Coordenador(Long id, String name, String email, String senhaHash) {
        super(id, name, email, senhaHash);
    }

    // ✅ Getter IMPORTANTE
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