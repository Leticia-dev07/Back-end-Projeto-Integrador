package com.senac.pi.entities;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import com.senac.pi.entities.enums.StatusSubmissao;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_submissao")
public class Submissao implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Instant dataEnvio;
	private Integer status;

	private Integer horasAproveitadas;
	private String observacaoCoordenador;

	@ManyToOne
    @JoinColumn(name = "coordenador_id")
    private Coordenador coordenador; 

    @ManyToOne
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

	@ManyToOne
	@JoinColumn(name = "categoria_id")
	private Categoria categoria;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "certificado_id")
	private Certificado certificado;

	public Submissao() {
	}

	public Submissao(Long id, Instant dataEnvio, StatusSubmissao status, Integer horasAproveitadas,
			String observacaoCoordenador, Aluno aluno, Categoria categoria) {
		this.id = id;
		this.dataEnvio = dataEnvio;
		setStatus(status);
		this.horasAproveitadas = horasAproveitadas;
		this.observacaoCoordenador = observacaoCoordenador;
		this.aluno = aluno;
		this.categoria = categoria;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Instant getDataEnvio() {
		return dataEnvio;
	}

	public void setDataEnvio(Instant dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	public StatusSubmissao getStatus() {
		return status != null ? StatusSubmissao.valueOf(status) : null;
	}

	public void setStatus(StatusSubmissao status) {
		if (status != null)
			this.status = status.getCode();
	}

	public Integer getHorasAproveitadas() {
		return horasAproveitadas;
	}

	public void setHorasAproveitadas(Integer horasAproveitadas) {
		this.horasAproveitadas = horasAproveitadas;
	}

	public String getObservacaoCoordenador() {
		return observacaoCoordenador;
	}

	public void setObservacaoCoordenador(String observacaoCoordenador) {
		this.observacaoCoordenador = observacaoCoordenador;
	}
	
	public Coordenador getCoordenador() {
        return coordenador;
    }

    public void setCoordenador(Coordenador coordenador) {
        this.coordenador = coordenador;
    }

	public Aluno getAluno() {
		return aluno;
	}

	public void setAluno(Aluno aluno) {
		this.aluno = aluno;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public Certificado getCertificado() {
		return certificado;
	}

	public void setCertificado(Certificado certificado) {
		this.certificado = certificado;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Submissao))
			return false;
		Submissao other = (Submissao) obj;
		return Objects.equals(id, other.id);
	}
}