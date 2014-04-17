package com.jgalante.jgcrud.controller;

import com.jgalante.jgcrud.entity.BaseEntity;
import com.jgalante.jgcrud.persistence.GenericDAO;
import com.jgalante.jgcrud.persistence.BaseDAO;

public class GenericController extends BaseController<BaseEntity, GenericDAO> {

	private static final long serialVersionUID = 1L;

	@Override
	public BaseDAO<BaseEntity> getDAO() {
		super.getDAO().setEntityClass(getEntityClass());
		return super.getDAO();
	}
	
}
