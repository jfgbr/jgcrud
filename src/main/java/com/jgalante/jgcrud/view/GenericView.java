package com.jgalante.jgcrud.view;

import java.util.List;

import com.jgalante.jgcrud.controller.BaseController;
import com.jgalante.jgcrud.controller.GenericController;
import com.jgalante.jgcrud.entity.BaseEntity;
import com.jgalante.jgcrud.persistence.BaseDAO;

public class GenericView extends BaseView<BaseEntity, GenericController> {

	private static final long serialVersionUID = 1L;
	
	private BaseController<BaseEntity, BaseDAO<BaseEntity>> delegateController;

	@Override
	public BaseController<BaseEntity, BaseDAO<BaseEntity>> getController() {
		if (this.delegateController == null) {
			super.getController().setEntityClass(getEntityClass());
			return super.getController();
		}
		return this.delegateController;
	}


	public List<? extends BaseEntity> findAll(Class<? extends BaseEntity> entityClass) {
		return getController(entityClass).findAll();
	}

	protected BaseController<BaseEntity, BaseDAO<BaseEntity>> getController(Class<? extends BaseEntity> entityClass) {
		setEntityClass(entityClass);
		return getController();
	}
	
	public void setController(BaseController<BaseEntity, BaseDAO<BaseEntity>> controller) {
		this.delegateController = controller;
	}
}
