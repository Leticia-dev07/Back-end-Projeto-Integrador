package com.senac.pi.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_categoria")
public class Categoria implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String area;
	private Boolean exigeComprovante;

	private Integer horasPorCertificado;
	private Integer limiteSubmissoesSemestre;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "curso_id")
	private Curso curso;

	// ESTA É A LINHA QUE RESOLVE O ERRO:
	// O 'mappedBy' deve ser exatamente o nome do atributo lá na classe Submissao
	@JsonIgnore
	@OneToMany(mappedBy = "categoria")
	private List<Submissao> submissoes = new ArrayList<>();

	public Categoria() {
	}

	public Categoria(Long id, String area, Boolean exigeComprovante, Integer horasPorCertificado,
			Integer limiteSubmissoesSemestre) {
		this.id = id;
		this.area = area;
		this.exigeComprovante = exigeComprovante;
		this.horasPorCertificado = horasPorCertificado;
		this.limiteSubmissoesSemestre = limiteSubmissoesSemestre;
	}

	// Getters e Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public Boolean getExigeComprovante() {
		return exigeComprovante;
	}

	public void setExigeComprovante(Boolean exigeComprovante) {
		this.exigeComprovante = exigeComprovante;
	}

	public Integer getHorasPorCertificado() {
		return horasPorCertificado;
	}

	public void setHorasPorCertificado(Integer horasPorCertificado) {
		this.horasPorCertificado = horasPorCertificado;
	}

	public Integer getLimiteSubmissoesSemestre() {
		return limiteSubmissoesSemestre;
	}

	public void setLimiteSubmissoesSemestre(Integer limiteSubmissoesSemestre) {
		this.limiteSubmissoesSemestre = limiteSubmissoesSemestre;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public List<Submissao> getSubmissoes() {
		return submissoes;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Categoria))
			return false;
		Categoria other = (Categoria) obj;
		return Objects.equals(id, other.id);
	}
}