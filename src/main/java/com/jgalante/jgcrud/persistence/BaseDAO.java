package com.jgalante.jgcrud.persistence;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import javax.transaction.Transactional;

import com.jgalante.jgcrud.annotation.DataRepository;
import com.jgalante.jgcrud.entity.BaseEntity;
import com.jgalante.util.Reflections;

public class BaseDAO<T extends BaseEntity> implements
		Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	@DataRepository
	private EntityManager em;

	private Class<T> entityClass;

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

	protected EntityManager getEntityManager() {
		return em;
	}

	public T find(Object id) {
		return this.em.find(getEntityClass(), id);
	}
	
	@Transactional
	public void save(T entity) {
		getEntityManager().merge(entity);		
	}
	
	public List<T> findAll() {
		CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(getEntityClass());
		cq.select(cq.from(getEntityClass()));
		return getEntityManager().createQuery(cq).getResultList();
	}

}
