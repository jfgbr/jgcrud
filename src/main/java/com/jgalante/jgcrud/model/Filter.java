package com.jgalante.jgcrud.model;

import java.util.List;

public class Filter {

	private String key;

	private Object value;

	private Operator op;

	private String alias;

	private Sort sort;

	private List<Filter> filters;

	public enum Operator {
		AND, OR, LIKE, EQUAL, NOT_EQUAL, BETWEEN, IN, NOT_IN, IS_NULL, IS_NOT_NULL, IS_TRUE, IS_FALSE, JOIN
	}
	
	public enum Sort {
		ASC, DESC, NONE
	}

	public Filter(String key, Object value) {
		this.key = key;
		this.value = value;
		this.op = Operator.EQUAL;
		this.sort = Sort.NONE;
		this.filters = null;
	}
	
	public Filter(String key, Operator op) {
		this.key = key;
		this.op = op;
	}

	public Filter(String key, Object value, Sort sort) {
		this.key = key;
		this.value = value;
		this.op = Operator.EQUAL;
		this.sort = sort;
		this.filters = null;
	}

	public Filter(String key, Object value, Operator op) {
		this.key = key;
		this.value = value;
		this.op = op;
		this.sort = Sort.NONE;
		this.filters = null;
	}

	public Filter(Operator op, List<Filter> filters) {
		this.key = null;
		this.value = null;
		this.op = op;
		this.sort = Sort.NONE;
		this.filters = filters;
	}

	public Filter(String key, Object value, Operator op, List<Filter> filters) {
		this.key = key;
		this.value = value;
		this.op = op;
		this.sort = Sort.NONE;
		this.filters = filters;
	}

	public Filter(String key, Object value, Operator op, Sort sort,
			List<Filter> filters) {
		this.key = key;
		this.value = value;
		this.op = op;
		this.sort = sort;
		this.filters = filters;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Operator getOp() {
		return op;
	}

	public void setOp(Operator op) {
		this.op = op;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Sort getSort() {
		return sort;
	}

	public void setSort(Sort sort) {
		this.sort = sort;
	}

	public List<Filter> getFilters() {
		return filters;
	}

	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("key[");
		sb.append(key);
		sb.append("];value[");
		sb.append(value);
		sb.append("];operator[");
		sb.append((op != null)?op.name():null);
		sb.append("]");
		return key;
	}
}
