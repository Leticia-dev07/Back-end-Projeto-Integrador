package com.senac.pi.entities;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_notificacao_email")
public class NotificacaoEmail implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String destinatario;
    private String assunto;
    private String corpo;

    // 🔥 RELAÇÃO COM SUBMISSÃO
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "submissao_id")
    private Submissao submissao;

    public NotificacaoEmail() {
    }

    public NotificacaoEmail(Long id, String destinatario, String assunto, String corpo) {
        this.id = id;
        this.destinatario = destinatario;
        this.assunto = assunto;
        this.corpo = corpo;
    }

    public Long getId() {
        return id;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public String getAssunto() {
        return assunto;
    }

    public String getCorpo() {
        return corpo;
    }

    public Submissao getSubmissao() {
        return submissao;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public void setCorpo(String corpo) {
        this.corpo = corpo;
    }

    public void setSubmissao(Submissao submissao) {
        this.submissao = submissao;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof NotificacaoEmail)) return false;
        NotificacaoEmail other = (NotificacaoEmail) obj;
        return Objects.equals(id, other.id);
    }
}