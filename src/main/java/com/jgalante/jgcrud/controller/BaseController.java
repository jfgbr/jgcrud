package com.jgalante.jgcrud.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import com.jgalante.jgcrud.annotation.DAO;
import com.jgalante.jgcrud.entity.BaseEntity;
import com.jgalante.jgcrud.exception.ControllerException;
import com.jgalante.jgcrud.model.DelegateDataModel;
import com.jgalante.jgcrud.model.Filter;
import com.jgalante.jgcrud.persistence.BaseDAO;
import com.jgalante.jgcrud.util.ClassHelper;
import com.jgalante.util.Reflections;

public class BaseController<T extends BaseEntity, D extends BaseDAO<T>>
		implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	@DAO
	private BaseDAO<T> dao;

	private DelegateDataModel<T> dataModel;

	private Class<T> entityClass;

	private List<Filter> listFilters;

	public String save(T entity) {
		if (validateEntity(entity)) {
			getDAO().save(entity);
		} else {
			throw new ControllerException("error.entity.invalid");
		}
		return null;
	}

	public void remove(T entity) {
		getDAO().remove(entity);
	}

	public List<T> findAll() {
		return getDAO().findAll();
	}

	public List<Filter> createListFiltersFromMap(Map<String, String> filters) {
		List<Filter> listFilters = new LinkedList<Filter>(); 
//		if (filters.size() > 1) {
//			listFilters.add(new Filter(Filter.Operator.AND,
//					defineFilters(filters)));
//		} else {
			listFilters = defineFilters(filters);
//		}
		return listFilters;
	}

	protected List<Filter> defineFilters(Map<String, String> filters) {

		List<Filter> listFilters = new ArrayList<Filter>();

		for (Map.Entry<String, String> filter : filters.entrySet()) {
			Class<?> originalClass = ClassHelper.getExpectedClass(
					getEntityClass(), filter.getKey());

			Filter itemFilter = new Filter(filter.getKey(),
					ClassHelper.convertIfNeeded(filter.getValue(),
							originalClass));

			if (itemFilter.getValue() == null) {
				itemFilter.setOp(Filter.Operator.IS_NULL);
			} else {
				List<String> values = Arrays.asList(itemFilter.getValue()
						.toString().split(";"));
				if (values.size() > 1) {
					if (ClassHelper.isAssignableFrom(originalClass, Date.class)) {
						itemFilter.setOp(Filter.Operator.BETWEEN);
						itemFilter.setFilters(new ArrayList<Filter>());

						SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
								"dd/MM/yyyy HH:mm:ss");

						Date minDate;
						Date maxDate;
						try {
							minDate = (Date) simpleDateFormat.parse(values.get(
									0).toString());
							maxDate = (Date) simpleDateFormat.parse(values.get(
									1).toString());
							itemFilter.getFilters().add(
									new Filter(filter.getKey(), minDate));
							itemFilter.getFilters().add(
									new Filter(filter.getKey(), maxDate));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} else {
						itemFilter.setOp(Filter.Operator.IN);
						itemFilter.setFilters(new ArrayList<Filter>());
						for (String value : values) {
							itemFilter.getFilters().add(
									new Filter(itemFilter.getKey(), ClassHelper
											.convertIfNeeded(value,
													originalClass)));
						}
					}
				} else if (ClassHelper.isAssignableFrom(originalClass,
						Boolean.class)) {
					if (new Boolean(itemFilter.getValue().toString())) {
						itemFilter.setOp(Filter.Operator.IS_TRUE);
					} else {
						itemFilter.setOp(Filter.Operator.IS_FALSE);
					}
				} else if (ClassHelper.isAssignableFrom(Enum.class,
						originalClass)) {
					itemFilter.setOp(Filter.Operator.EQUAL);
				} else if (ClassHelper.isAssignableFrom(originalClass,
						Date.class)) {

					itemFilter.setOp(Filter.Operator.BETWEEN);
					itemFilter.setFilters(new ArrayList<Filter>());

					Date minDate = (Date) itemFilter.getValue();
					Date maxDate = new Date(minDate.getTime()
							+ TimeUnit.DAYS.toMillis(1)
							- TimeUnit.SECONDS.toMillis(1));

					itemFilter.getFilters().add(
							new Filter(filter.getKey(), minDate));
					itemFilter.getFilters().add(
							new Filter(filter.getKey(), maxDate));

				} else if (ClassHelper.isAssignableFrom(originalClass,
						String.class)) {
					itemFilter.setOp(Filter.Operator.LIKE);

					if (filter.getKey().contains("cpf")
							|| filter.getKey().contains("cnpj")
							|| filter.getKey().contains("ncm")) {
						itemFilter.setValue(itemFilter.getValue().toString()
								.replace(".", "").replace("-", "")
								.replace("/", "")
								+ "%");
					} else {
						itemFilter.setValue("%"
								+ itemFilter.getValue().toString()
										.toLowerCase() + "%");
					}

				} else if (ClassHelper.isAssignableFrom(originalClass,
						BigInteger.class)) {

					itemFilter.setValue(ClassHelper.convertIfNeeded(
							itemFilter.getValue(), originalClass));

				} else if (ClassHelper.isAssignableFrom(originalClass,
						BigDecimal.class)) {

					itemFilter.setValue(ClassHelper.convertIfNeeded(
							itemFilter.getValue(), originalClass));

					Double minNum = Double.parseDouble(itemFilter.getValue()
							.toString());
					Double maxNum = new Double(minNum.intValue() + 0.9999);

					// Se o número for inteiro, aplico esse tipo de filtro
					if (((minNum % 1) == 0)) {
						itemFilter.setOp(Filter.Operator.BETWEEN);
						itemFilter.setFilters(new ArrayList<Filter>());

						itemFilter.getFilters().add(
								new Filter(filter.getKey(), new BigDecimal(
										minNum)));
						itemFilter.getFilters().add(
								new Filter(filter.getKey(), new BigDecimal(
										maxNum)));
					}

				}

			}

			listFilters.add(itemFilter);
		}
		return listFilters;
	}

	public void addFilter(Filter filter) {
		if (listFilters == null) {
			listFilters = new LinkedList<Filter>();
		}
		getListFilters().add(filter);
	}
	
	public void addFilters(List<Filter> filters) {
		if (listFilters == null) {
			listFilters = new LinkedList<>(filters);
		} else {
			getListFilters().addAll(filters);
		}
	}
	
	public List<T> search() {
		return search(0, 0);
	}
	
	public List<T> search(int first, int pageSize) {
		return search(first, pageSize, null, new LinkedList<Filter>());
	}
	
	public List<T> search(Map<String, Boolean> sort,
			List<Filter> filters) {
		return search(0, 0, sort, filters);
	}
	
	public List<T> search(int first, int pageSize, Map<String, Boolean> sort,
		 Map<String, String> filters) {
		 try {
			 addFilters(createListFiltersFromMap(filters));
			 return getDAO().search(first, pageSize, sort, getListFilters());
		 } catch (Exception e) {
			 throw new ControllerException(e.getMessage());
		 }
	 }

	public List<T> search(int first, int pageSize, Map<String, Boolean> sort,
			List<Filter> filters) {
		try {
			addFilters(filters);
			return getDAO().search(first, pageSize, sort, getListFilters());
		} catch (Exception e) {
			throw new ControllerException(e.getMessage());
		}
	}

	public int rowCount() {
		try {
			return getDAO().getRowCount();
		} catch (Exception e) {
			throw new ControllerException(e.getMessage());
		}
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

	protected void setDAO(BaseDAO<T> dao) {
		this.dao = dao;
	}

	@SuppressWarnings("unchecked")
	public DelegateDataModel<T> getDataModel() {
		if (dataModel == null) {
			dataModel = new DelegateDataModel<T>(
					(BaseController<T, BaseDAO<T>>) this);
		}
		return dataModel;
	}

	public void setDataModel(DelegateDataModel<T> dataModel) {
		this.dataModel = dataModel;
	}

	protected List<Filter> getListFilters() {
		return listFilters;
	}

	
}
