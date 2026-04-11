package com.senac.pi.entities;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_atividade_complementar")
public class AtividadeComplementar implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String tipo;
	private String descricao;
	private Integer cargaHorariaSolicitada;
	
	public AtividadeComplementar(){
	}

	public AtividadeComplementar(Long id, String tipo, String descricao, Integer cargaHorariaSolicitada) {
		super();
		this.id = id;
		this.tipo = tipo;
		this.descricao = descricao;
		this.cargaHorariaSolicitada = cargaHorariaSolicitada;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getCargaHorariaSolicitada() {
		return cargaHorariaSolicitada;
	}

	public void setCargaHorariaSolicitada(Integer cargaHorariaSolicitada) {
		this.cargaHorariaSolicitada = cargaHorariaSolicitada;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AtividadeComplementar other = (AtividadeComplementar) obj;
		return Objects.equals(id, other.id);
	}

	
}
