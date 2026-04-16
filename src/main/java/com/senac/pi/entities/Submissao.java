package com.senac.pi.entities;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_submissao")
public class Submissao implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant dataEnvio;
    private String status;

    // 🔹 RELAÇÃO COM ALUNO (muitas submissões para 1 aluno)
    @ManyToOne
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    // 🔹 RELAÇÃO COM COORDENADOR (quem valida)
    @ManyToOne
    @JoinColumn(name = "coordenador_id")
    private Coordenador coordenador;

    // 🔹 RELAÇÃO COM ATIVIDADE (1 para 1)
    @ManyToOne
    @JoinColumn(name = "atividade_id")
    private AtividadeComplementar atividade;

    // 🔹 RELAÇÃO COM CERTIFICADO (opcional)
    @OneToOne
    @JoinColumn(name = "certificado_id")
    private Certificado certificado;

    // 🔹 RELAÇÃO COM NOTIFICAÇÕES
    @JsonIgnore
    @OneToMany(mappedBy = "submissao")
    private Set<NotificacaoEmail> notificacoes = new HashSet<>();

    public Submissao() {
    }

    public Submissao(Long id, Instant dataEnvio, String status) {
        this.id = id;
        this.dataEnvio = dataEnvio;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Instant getDataEnvio() {
        return dataEnvio;
    }

    public String getStatus() {
        return status;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public Coordenador getCoordenador() {
        return coordenador;
    }

    public AtividadeComplementar getAtividade() {
        return atividade;
    }

    public Certificado getCertificado() {
        return certificado;
    }

    public Set<NotificacaoEmail> getNotificacoes() {
        return notificacoes;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDataEnvio(Instant dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public void setCoordenador(Coordenador coordenador) {
        this.coordenador = coordenador;
    }

    public void setAtividade(AtividadeComplementar atividade) {
        this.atividade = atividade;
    }

    public void setCertificado(Certificado certificado) {
        this.certificado = certificado;
    }

    public void setNotificacoes(Set<NotificacaoEmail> notificacoes) {
        this.notificacoes = notificacoes;
    }

    // ✅ Métodos auxiliares
    public void addNotificacao(NotificacaoEmail notificacao) {
        notificacoes.add(notificacao);
    }

    public void removeNotificacao(NotificacaoEmail notificacao) {
        notificacoes.remove(notificacao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Submissao)) return false;
        Submissao other = (Submissao) obj;
        return Objects.equals(id, other.id);
    }
}