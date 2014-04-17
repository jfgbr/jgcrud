package com.jgalante.jgcrud.controller;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import com.jgalante.jgcrud.annotation.DAO;
import com.jgalante.jgcrud.entity.BaseEntity;
import com.jgalante.jgcrud.exception.ControllerException;
import com.jgalante.jgcrud.persistence.BaseDAO;
import com.jgalante.util.Reflections;

public class BaseController<T extends BaseEntity, D extends BaseDAO<T>> implements Serializable{

	private static final long serialVersionUID = 1L;

	@Inject
	@DAO
	private BaseDAO<T> dao;
	
	private Class<T> entityClass;

	
	public String save(T entity) {
		if (validateEntity(entity)) {
			getDAO().save(entity);
		} else {
			throw new ControllerException("error.entity.invalid");
		}
		return null;
	}
	
	public List<T> findAll() {
		return getDAO().findAll();
	}
	
	public Boolean validateEntity(T entity) {
		return true;
	}
	

	protected Class<T> getEntityClass() {
		if (this.entityClass == null) {
			this.entityClass = Reflections.getGenericTypeArgument(
					this.getClass(), 0);
		}

		return this.entityClass;
	}

	@SuppressWarnings("unchecked")
	public void setEntityClass(Class<? extends BaseEntity> entityClass) {
		this.entityClass = (Class<T>) entityClass;
	}

	public BaseDAO<T> getDAO() {
		return dao;
	}
	
}
