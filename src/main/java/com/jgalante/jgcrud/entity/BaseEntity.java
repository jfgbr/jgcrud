package com.jgalante.jgcrud.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseEntity { //implements Serializable{
	
//	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

//	@Temporal(TemporalType.TIMESTAMP)
//	private Date dataRegistro;
//
//	@Temporal(TemporalType.TIMESTAMP)
//	private Date dataAlteracao;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

//	public Date getDataRegistro() {
//		return dataRegistro;
//	}
//
//	public void setDataRegistro(Date dataRegistro) {
//		this.dataRegistro = dataRegistro;
//	}
//
//	public Date getDataAlteracao() {
//		return dataAlteracao;
//	}
//
//	public void setDataAlteracao(Date dataAlteracao) {
//		this.dataAlteracao = dataAlteracao;
//	}
//
//	@Transient
//	public abstract String getTextoExibicao();
//	
//	@PrePersist
//	public void inicializaDataBase() {
//		if (dataRegistro == null) {
//			dataRegistro = new Date();
//		}
//		dataAlteracao = dataRegistro;
//	}
//
//	@PreUpdate
//	public void atualizaDataBase() {
//		dataAlteracao = new Date();
//	}
//	
//	public abstract List<String> getOrderBy(); 

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
//		result = prime
//				* result
//				+ ((getTextoExibicao() == null) ? 0 : getTextoExibicao().hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseEntity other = (BaseEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
