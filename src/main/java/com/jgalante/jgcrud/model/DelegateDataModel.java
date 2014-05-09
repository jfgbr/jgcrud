package com.jgalante.jgcrud.model;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import com.jgalante.jgcrud.controller.BaseController;
import com.jgalante.jgcrud.entity.BaseEntity;
import com.jgalante.jgcrud.model.Filter.Operator;
import com.jgalante.jgcrud.persistence.BaseDAO;
import com.jgalante.jgcrud.util.Util;

public class DelegateDataModel<T extends BaseEntity> extends LazyDataModel<T> {

	private static final long serialVersionUID = 1L;

	protected List<T> datasource;

	protected int currentPage;

	private boolean primeira = true;

	private Field fieldId;
	
	protected BaseController<T, BaseDAO<T>> delegate;

	private String fetchDataSource = "search";
	
	private List<String> joinFields;
	
	public DelegateDataModel(BaseController<T, BaseDAO<T>> delegate) {
		this.delegate = delegate;
	}
	
	public DelegateDataModel(BaseController<T, BaseDAO<T>> delegate,
			List<String> joinFields) {
		super();
		this.delegate = delegate;
		this.joinFields = joinFields;
	}
	
	public DelegateDataModel(BaseController<T, BaseDAO<T>> delegate,
			String joinFields) {
		super();
		this.delegate = delegate;
		if (joinFields != null) {
			this.joinFields = new LinkedList<String>(Arrays.asList(joinFields.split(",")));
		}
	}

	public void reset() {
		datasource = null;
		currentPage = 0;
		primeira = true;
		fieldId = null;
		fetchDataSource = "search";
		setWrappedData(null);		
	}
	
	@SuppressWarnings("unchecked")
	public List<T> fetchDataSource(int first, int pageSize, Map<String, Boolean> sort, Map<String, String> filters) {
		try {
//			Method search = delegate.getClass().getMethod(fetchDataSource, int.class, int.class, Map.class, Map.class);
			Method search = delegate.getClass().getMethod(fetchDataSource, int.class, int.class, Map.class, List.class);
			List<Filter> listFilters = delegate.createListFiltersFromMap(filters);
			listFilters.addAll(createListFilterForJoinFields());
			return (List<T>) search.invoke(delegate, first, pageSize, sort, listFilters);
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}

		return delegate.search(first, pageSize, sort, filters);
	}

	private List<Filter> createListFilterForJoinFields() {
		List<Filter> listFilters = new LinkedList<Filter>();
		if (joinFields != null) {
			for (String joinField : joinFields) {
				listFilters.add(new Filter(joinField, null, Operator.JOIN));	
			}
		}
		return listFilters;
	}

	public String getFetchDataSource() {
		return this.fetchDataSource;
	}

	public void setFetchDataSource(String fetchDataSource) {
		this.fetchDataSource = fetchDataSource;
	}

	public int getTotalSize() {
		return delegate.rowCount();
	}

	@Override
	public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {

		Map<String, Boolean> sort = null;
		if (sortField != null) {
			sort = new HashMap<String, Boolean>();
			sort.put(sortField, SortOrder.DESCENDING.equals(sortOrder));
		}
		return load(first, pageSize, sort, filters);
	}

	public void setDatasource(List<T> datasource) {
		this.datasource = datasource;
	}

	@Override
	public List<T> load(int first, int pageSize, List<SortMeta> multiSortMeta, Map<String, String> filters) {
		Map<String, Boolean> sort = null;
		if (multiSortMeta != null) {
			sort = new HashMap<String, Boolean>();
			for (SortMeta sortMeta : multiSortMeta) {
				sort.put(sortMeta.getSortField(), SortOrder.DESCENDING.equals(sortMeta.getSortOrder()));
			}
		}
		return load(first, pageSize, sort, filters);
	}

	@SuppressWarnings("unchecked")
	private List<T> load(int first, int pageSize, Map<String, Boolean> sort, Map<String, String> filters) {

		if (datasource == null) {
			datasource = new ArrayList<T>();
		}

		List<T> wrappedData = (List<T>) getWrappedData();

		if (primeira && wrappedData != null && !wrappedData.isEmpty()) {
			datasource = (List<T>) getWrappedData();
		} else {
			datasource = fetchDataSource(first, pageSize, sort, filters);
		}
		primeira = false;
		this.setRowCount(getTotalSize());

		currentPage = calculateCurrentPage(first, pageSize);

		setWrappedData(datasource);

		return datasource;
	}

	protected int calculateCurrentPage(int first, int pageSize) {
		return ((first / pageSize) + 1);
	}
	
	@Override
	public T getRowData(String rowKey) {
		for (T bean : datasource) {
			try {
//				if (getId(bean).equals(ClassHelper.convertIfNeeded(rowKey, getFieldId(bean).getType()))) {
					return bean;
//				}
			} catch (IllegalArgumentException e) {
			} catch (ClassCastException e) {
			} catch (UnsupportedOperationException e) {
			} 
//			catch (IllegalAccessException e) {
//			}
		}

		return super.getRowData(rowKey);
	}

	@Override
	public Object getRowKey(T object) {
		try {
			return getId(object);
		} catch (IllegalArgumentException e) {
		} catch (UnsupportedOperationException e) {
		} catch (IllegalAccessException e) {
		}
		return super.getRowKey(object);
	}

	private Field getFieldId(T object) throws UnsupportedOperationException {
		if (fieldId == null) {
			fieldId = Util.getField(object.getClass(), "id");
			if (fieldId == null) {
				throw new UnsupportedOperationException(
						"getRowData(String rowKey) and getRowKey(T object) must be implemented when basic rowKey algorithm is not used.");
			}
		}
		return fieldId;
	}

	private Object getId(T object) throws IllegalArgumentException, IllegalAccessException, UnsupportedOperationException {
		return getFieldId(object).get(object);
	}

	public void addJoinField(String field) {
		if (joinFields == null) {
			joinFields = new LinkedList<String>();
		}
		joinFields.add(field);
	}
	
	public void removeJoinField(String field) {
		if (joinFields != null) {
			joinFields.remove(field);
		}
	}
}
