package com.senac.pi.entities;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_regra_atividade")
public class RegraAtividade implements Serializable{
	private static final long serialVersionUID = 1L;

	private Long id;
	private String area;
	private Integer limiteHoras;
	private Boolean exigeCompravante;
	
	public RegraAtividade() {
	}

	public RegraAtividade(Long id, String area, Integer limiteHoras, Boolean exigeCompravante) {
		super();
		this.id = id;
		this.area = area;
		this.limiteHoras = limiteHoras;
		this.exigeCompravante = exigeCompravante;
	}

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

	public Integer getLimiteHoras() {
		return limiteHoras;
	}

	public void setLimiteHoras(Integer limiteHoras) {
		this.limiteHoras = limiteHoras;
	}

	public Boolean getExigeCompravante() {
		return exigeCompravante;
	}

	public void setExigeCompravante(Boolean exigeCompravante) {
		this.exigeCompravante = exigeCompravante;
	}

	@Override
	public int hashCode() {
		return Objects.hash(area, exigeCompravante, id, limiteHoras);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RegraAtividade other = (RegraAtividade) obj;
		return Objects.equals(area, other.area) && Objects.equals(exigeCompravante, other.exigeCompravante)
				&& Objects.equals(id, other.id) && Objects.equals(limiteHoras, other.limiteHoras);
	}
	
	
	
}
