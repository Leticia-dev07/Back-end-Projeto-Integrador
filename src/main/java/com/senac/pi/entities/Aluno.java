package com.senac.pi.entities;

<<<<<<< HEAD
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

=======
import java.util.HashSet;
import java.util.Set;

>>>>>>> 605a1f1f0e30830dd253152ec3f1ec4a130018bc
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
<<<<<<< HEAD
public class Aluno extends User implements UserDetails {
=======
public class Aluno extends User {
>>>>>>> 605a1f1f0e30830dd253152ec3f1ec4a130018bc

    private static final long serialVersionUID = 1L;

    @Column(unique = true, nullable = false)
    private String matricula;
    
<<<<<<< HEAD
    @Column(nullable = false)
=======
    @Column(unique = true, nullable = false)
>>>>>>> 605a1f1f0e30830dd253152ec3f1ec4a130018bc
    private String turma;
    
    private Integer horasAcumuladas = 0;

    @ManyToMany
    @JoinTable(
        name = "tb_curso_aluno",
        joinColumns = @JoinColumn(name = "aluno_id"),
        inverseJoinColumns = @JoinColumn(name = "curso_id")
    )
    private Set<Curso> cursos = new HashSet<>();
    
<<<<<<< HEAD
    @JsonIgnore 
=======
    @JsonIgnore // Adicionado para evitar loop infinito no JSON
>>>>>>> 605a1f1f0e30830dd253152ec3f1ec4a130018bc
    @OneToMany(mappedBy = "aluno")
    private Set<Submissao> submissoes = new HashSet<>();

    public Aluno() {
    }

    public Aluno(Long id, String name, String email, String senhaHash, String matricula, String turma, Integer horasAcumuladas) {
<<<<<<< HEAD
=======
        // Passando o UserRole.ALUNO para a classe pai
>>>>>>> 605a1f1f0e30830dd253152ec3f1ec4a130018bc
        super(id, name, email, senhaHash, UserRole.ALUNO); 
        this.matricula = matricula;
        this.turma = turma;
        this.horasAcumuladas = (horasAcumuladas == null) ? 0 : horasAcumuladas;
    }

<<<<<<< HEAD
    // --- MÉTODOS DA INTERFACE USERDETAILS (SPRING SECURITY) ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Retorna a autoridade baseada no enum UserRole (ex: ROLE_ALUNO)
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.getRole().name()));
    }

    @Override
    public String getPassword() {
        return this.getSenhaHash(); // O Spring Security usará isso para validar o BCrypt
    }

    @Override
    public String getUsername() {
        return this.getEmail(); // O e-mail será o nosso login (username)
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Conta não expirada
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Conta não bloqueada
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Senha não expirada
    }

    @Override
    public boolean isEnabled() {
        return true; // Usuário ativo
    }

    // --- GETTERS E SETTERS ---

=======
    // Getters e Setters
>>>>>>> 605a1f1f0e30830dd253152ec3f1ec4a130018bc
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

<<<<<<< HEAD
=======
    // Métodos auxiliares
>>>>>>> 605a1f1f0e30830dd253152ec3f1ec4a130018bc
    public void addCurso(Curso curso) {
        cursos.add(curso);
    }

    public void removeCurso(Curso curso) {
        cursos.remove(curso);
    }
}