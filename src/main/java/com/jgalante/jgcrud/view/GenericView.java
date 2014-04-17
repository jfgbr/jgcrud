package com.jgalante.jgcrud.view;

import java.util.List;

import javax.validation.ConstraintViolationException;

import com.jgalante.jgcrud.controller.GenericController;
import com.jgalante.jgcrud.controller.BaseController;
import com.jgalante.jgcrud.entity.BaseEntity;
import com.jgalante.jgcrud.exception.ControllerException;
import com.jgalante.jgcrud.persistence.BaseDAO;

public class GenericView extends BaseView<BaseEntity, GenericController> {

	private static final long serialVersionUID = 1L;

	@Override
	public BaseController<BaseEntity, BaseDAO<BaseEntity>> getController() {
		super.getController().setEntityClass(getEntityClass());
		return super.getController();
	}

	public String save() {
		return null;
	}
	
	public void save(BaseEntity entity) {
		try {
			getController().save(entity);
		} catch (ControllerException e) {
			messages.error(e.getMessage());
		} catch (ConstraintViolationException e) {
			handleContraintViolation(e.getConstraintViolations());
		}
		
	}

	public List<? extends BaseEntity> findAll(Class<? extends BaseEntity> entityClass) {
		return getController(entityClass).findAll();
	}

	protected BaseController<BaseEntity, BaseDAO<BaseEntity>> getController(Class<? extends BaseEntity> entityClass) {
		setEntityClass(entityClass);
		return getController();
	}
}
