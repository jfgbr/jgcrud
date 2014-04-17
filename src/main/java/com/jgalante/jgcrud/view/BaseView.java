package com.jgalante.jgcrud.view;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.jgalante.jgcrud.annotation.Controller;
import com.jgalante.jgcrud.controller.BaseController;
import com.jgalante.jgcrud.entity.BaseEntity;
import com.jgalante.jgcrud.exception.ControllerException;
import com.jgalante.jgcrud.persistence.BaseDAO;
import com.jgalante.jgcrud.util.MessageHandler;
import com.jgalante.util.Reflections;

public class BaseView<T extends BaseEntity, C extends BaseController<T, ? extends BaseDAO<T>>> implements Serializable{

	private static final long serialVersionUID = 1L;

	@Inject
	@Controller
	private BaseController<T, BaseDAO<T>> controller;
	
	private Class<? extends BaseEntity> entityClass;
	
	private T entity;
	private List<T> entities;
	
	@Inject
	protected MessageHandler messages;
	
	public String save() {
		return null;
	}
	
	public void save(T entity) {
		try {
			getController().save(entity);
		} catch (ControllerException e) {
			messages.error(e.getMessage());
		} catch (ConstraintViolationException e) {
			handleContraintViolation(e.getConstraintViolations());
		}
		
	}
	
	public List<T> findAll() {
		return getController().findAll();
	}
	
	protected void handleContraintViolation(Set<ConstraintViolation<?>> constraintViolations) {
		for (ConstraintViolation<?> constraintViolation : constraintViolations) {
			messages.error(constraintViolation.getMessage());
		}
	}

	public T getEntity() {
		return entity;
	}

	public void setEntity(T entity) {
		this.entity = entity;
	}
	
	@SuppressWarnings("unchecked")
	protected Class<T> getEntityClass() {
		if (this.entityClass == null) {
			if (this.entity != null) {
				this.entityClass = entity.getClass();
			} else {
				this.entityClass = Reflections.getGenericTypeArgument(
					this.getClass(), 0);
			}
		}

		return (Class<T>) this.entityClass;
	}

	@SuppressWarnings("unchecked")
	public void setEntityClass(Class<? extends BaseEntity> entityClass) {
		this.entityClass = (Class<T>) entityClass;
	}

	public List<T> getEntities() {
		return entities;
	}

	public void setEntities(List<T> entities) {
		this.entities = entities;
	}

	public BaseController<T, BaseDAO<T>> getController() {
		return controller;
	}
	
}
