package com.senac.pi.entities;

import java.io.Serializable;
<<<<<<< HEAD
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

=======
import java.util.Objects;
>>>>>>> 605a1f1f0e30830dd253152ec3f1ec4a130018bc
import com.senac.pi.entities.enums.UserRole;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_user")
@Inheritance(strategy = InheritanceType.JOINED)
<<<<<<< HEAD
public abstract class User implements Serializable, UserDetails {
=======
public abstract class User implements Serializable {
>>>>>>> 605a1f1f0e30830dd253152ec3f1ec4a130018bc
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
<<<<<<< HEAD
    
    @Column(unique = true) // Garante que e-mails não se repitam no login
=======
>>>>>>> 605a1f1f0e30830dd253152ec3f1ec4a130018bc
    private String email;

    @Column(name = "senha_hash")
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    public User() {}

    public User(Long id, String name, String email, String senhaHash, UserRole role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.senhaHash = senhaHash;
        this.role = role;
    }

<<<<<<< HEAD
    // --- MÉTODOS DA INTERFACE USERDETAILS (OBRIGATÓRIOS PARA SEGURANÇA) ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Converte o enum (ALUNO, COORDENADOR) em uma autoridade do Spring (ROLE_ALUNO)
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return senhaHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    // --- GETTERS E SETTERS ---

=======
    // Getters e Setters
>>>>>>> 605a1f1f0e30830dd253152ec3f1ec4a130018bc
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User other = (User) obj;
        return Objects.equals(id, other.id);
    }
}