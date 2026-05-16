package com.senac.pi.entities;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.senac.pi.entities.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_aluno")
public class Aluno extends User implements UserDetails {

    private static final long serialVersionUID = 1L;

    @Column(unique = true, nullable = false)
    private String matricula;

    @Column(nullable = false)
    private String turma;

    private Integer horasAcumuladas = 0;

    @ManyToMany
    @JoinTable(
        name = "tb_curso_aluno",
        joinColumns = @JoinColumn(name = "aluno_id"),
        inverseJoinColumns = @JoinColumn(name = "curso_id")
    )
    private Set<Curso> cursos = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "aluno")
    private Set<Submissao> submissoes = new HashSet<>();

    public Aluno() {
    }

    public Aluno(Long id, String name, String email, String senhaHash,
                 String matricula, String turma, Integer horasAcumuladas) {
        super(id, name, email, senhaHash, UserRole.ALUNO);
        this.matricula = matricula;
        this.turma = turma;
        this.horasAcumuladas = (horasAcumuladas == null) ? 0 : horasAcumuladas;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
            new SimpleGrantedAuthority("ROLE_" + this.getRole().name())
        );
    }

    @Override
    public String getPassword() {
        return this.getSenhaHash();
    }

    @Override
    public String getUsername() {
        return this.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
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

    public void addCurso(Curso curso) {
        cursos.add(curso);
    }

    public void removeCurso(Curso curso) {
        cursos.remove(curso);
    }
}