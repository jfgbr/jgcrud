package com.jgalante.jgcrud.controller;

import com.jgalante.jgcrud.entity.BaseEntity;
import com.jgalante.jgcrud.persistence.BaseDAO;
import com.jgalante.jgcrud.persistence.GenericDAO;
import com.jgalante.jgcrud.stereotype.BusinessController;

@BusinessController
public class GenericController extends BaseController<BaseEntity, GenericDAO> {

	private static final long serialVersionUID = 1L;

//	private BaseDAO<BaseEntity> delegateDAO;
	
	@Override
	public BaseDAO<BaseEntity> getDAO() {
//		if (this.delegateDAO == null) {
			if (!getEntityClass().equals(super.getDAO().getEntityClass())) {
				super.getDAO().setEntityClass(getEntityClass());
			}
			return super.getDAO();
//		}
//		return delegateDAO;
	}
	
//	public void setDAO(BaseDAO<BaseEntity> dao) {
//		this.delegateDAO = dao;
//	}
}
