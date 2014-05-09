package com.jgalante.jgcrud.persistence;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.transaction.Transactional;

import org.hibernate.Hibernate;
import org.primefaces.model.LazyDataModel;

import com.jgalante.jgcrud.annotation.DataRepository;
import com.jgalante.jgcrud.entity.BaseEntity;
import com.jgalante.jgcrud.model.Filter;
import com.jgalante.jgcrud.model.QueryParam;
import com.jgalante.jgcrud.util.ClassHelper;
import com.jgalante.util.Reflections;

public class BaseDAO<T extends BaseEntity> implements
		Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	@DataRepository
	private EntityManager em;

	private Class<T> entityClass;

	private QueryParam queryParam;

	public Class<T> getEntityClass() {
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
	
	/**
	 * Busca padrão de acordo com os parametros do {@link LazyDataModel}.
	 * Converte o {@code Map<String, String>} para {@code List<Filter>}
	 * 
	 * @param first
	 * @param pageSize
	 * @param sort
	 * @param filters
	 * @return
	 */
//	public List<T> search(int first, int pageSize, Map<String, Boolean> sort,
//			Map<String, String> filters) {
//
//		List<Filter> listFilters = createListFiltersFromMap(filters);
//		return search(first, pageSize, sort, listFilters);
//	}
//
//	public List<Filter> createListFiltersFromMap(Map<String, String> filters) {
//		List<Filter> listFilters = new ArrayList<Filter>();
//		if (filters.size() > 1) {
//			listFilters.add(new Filter(Filter.Operator.AND,
//					defineFilters(filters)));
//		} else {
//			listFilters = defineFilters(filters);
//		}
//		return listFilters;
//	}
	
	@Transactional
	public List<T> search(int first, int pageSize, Map<String, Boolean> sort,
			List<Filter> filters) {

		TypedQuery<T> querySearch = createQuery(sort, filters);
		Query queryCount = null;
		List<T> data = new ArrayList<T>();
		try {
			if (pageSize != 0) {
				querySearch.setFirstResult(first)
					.setMaxResults(pageSize);
			}

			queryParam.updateParameter(querySearch);

			data = querySearch.getResultList();
			
			initializeJoinFields(data);
			
//			for (Filter filter : queryParam.getJoinFields()) {
//				Field field = ClassHelper.getJoinField(getEntityClass(),
//						filter.getKey());
//				for (T entity : data) {
//					Object instance = field.get(entity);
//
//					Hibernate.initialize(instance);
//
//					if (instance instanceof Iterable) {
//						Iterator<?> it = ((Iterable<?>) instance).iterator();
//						while (it.hasNext())
//							it.next();
//					}
//				}
//			}
			
			queryCount = createCountQuery(queryParam.createCountJPQL());
			getQueryParam().setRowCount(
					((Number) queryCount.getSingleResult()).intValue());
			
		} catch (Exception e) {
			System.out.println(querySearch.toString());
			System.out.println(queryCount.toString());
			e.printStackTrace();
		}

		return data;
	}

	private Query createCountQuery(String countQuery) {
		Query queryCount = getEntityManager().createQuery(countQuery);
		queryParam.updateParameter(queryCount);
		return queryCount;
	}

	private TypedQuery<T> createQuery(Map<String, Boolean> sort,
			List<Filter> filters) {
		queryParam = new QueryParam(getEntityClass());

		queryParam.setOrderBy(sort);

		queryParam.setListFilter(filters);
	
		String jpql = queryParam.createSearchJPQL();
		TypedQuery<T> querySearch = getEntityManager()
				.createQuery(jpql, getEntityClass());
		return querySearch;
	}

	private void initializeJoinFields(List<T> data)
			throws IllegalAccessException {
		
		for (Filter filter : queryParam.getJoinFields()) {
			Field field = ClassHelper.getJoinField(getEntityClass(),
					filter.getKey());
			for (T entity : data) {
				Object instance = field.get(entity);

				Hibernate.initialize(instance);

				if (instance instanceof Iterable) {
					Iterator<?> it = ((Iterable<?>) instance).iterator();
					while (it.hasNext())
						it.next();
				}
			}
		}
	}

	public int getRowCount() {
		return getQueryParam().getRowCount();
	}
	
	@Transactional
	public void save(T entity) {
		getEntityManager().merge(entity);		
	}
	
	@Transactional
	public void remove(T entity) {
		T removeEntity = (getEntityManager().contains(entity) ? entity : getEntityManager().getReference(getEntityClass(), entity.getId()));
		getEntityManager().remove(removeEntity);
	}
	
	public List<T> findAll() {
		CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(getEntityClass());
		cq.select(cq.from(getEntityClass()));
		return getEntityManager().createQuery(cq).getResultList();
	}

	public QueryParam getQueryParam() {
		if (queryParam == null) {
			queryParam = new QueryParam(getEntityClass());
		}
		return queryParam;
	}
}
