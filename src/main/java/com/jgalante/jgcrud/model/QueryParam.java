package com.jgalante.jgcrud.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import com.jgalante.jgcrud.model.Filter.Operator;

public class QueryParam implements Serializable {

	private static final long serialVersionUID = 4585134761403626269L;

	protected int rowCount;
	private String rootAlias = "obj";
	private String uniqueId = "id";

	private String fromClause = "";
	private String whereClause = "";
	
	private Map<String, Boolean> orderBy;
	private Map<String, Object> filters;

	private Map<String, String> alias = new LinkedHashMap<String, String>();
	private Map<String, String> aliasParameter = new LinkedHashMap<String, String>();
	
	private boolean includeJoins = true;

	private List<Filter> listFilter;
	
	private List<Filter> joinFields;
	
	private Class<?> beanClass;
	
	public QueryParam(boolean includeJoins, Class<?> beanClass) {
		super();
		this.includeJoins = includeJoins;
		this.beanClass = beanClass;
	}

	public QueryParam(Class<?> beanClass) {
		super();
		this.beanClass = beanClass;
	}

	public String generateAlias(String root, String key) {
		
		String[] parts = (key+".").split("\\.");
		String tmpRoot = (!root.isEmpty()?root+".":"");
		String newAlias = "";// = root;//(!root.isEmpty()?root:"");
		for (String part : parts) {
//			newAlias = tmpRoot + part;
//			if (!newAlias.isEmpty()) {
//				newAlias += ".";
//			}
			newAlias = alias.get(tmpRoot + part);
			
			Integer idCount = alias.size();
			if (newAlias == null){
				StringBuilder sb = new StringBuilder();
				sb.append("alias_");
				sb.append(idCount.toString());
				newAlias = sb.toString();
	//			if (root != null && !root.isEmpty()) {
	//				sb.append(root).append(".");
	//			}
				alias.put(tmpRoot + part, newAlias);
				tmpRoot = newAlias+".";
			}
			
		}
		return newAlias;
	}
	
	public String generateAliasParameter(String key) {
		String newAliasParameter = aliasParameter.get(key);
		Integer idCount = aliasParameter.size();
		if (newAliasParameter == null){
			StringBuilder sb = new StringBuilder("param_");
			sb.append(idCount.toString());
			newAliasParameter = sb.toString();
			aliasParameter.put(key, newAliasParameter);
		}
		return newAliasParameter;
	}

	public String createAlias(String path) {
		// StringBuilder sb = new StringBuilder();
		// sb.append(queryParam.getRootAlias());
		// sb.append(".");
		// sb.append(path);
		String alias = path;
		int lastPos = -1;
		int pos = path.indexOf('.');
		boolean created = false;
		String root = getRootAlias();
		String newAlias = "";
		while (pos != -1) {
			created = true;
			String currentPath = path.substring(0, pos);

			if (getAlias().containsKey(currentPath)) {
				newAlias = getAlias().get(currentPath);
			} else {
				String prop = (!newAlias.isEmpty() ? newAlias + "." : "")
						+ path.substring(lastPos + 1, pos);
				newAlias = generateAlias(root, prop);
				if (path.indexOf('.', pos + 1) == -1) {
					newAlias = newAlias + path.substring(pos);
				}
			}
			alias = newAlias;// alias.replaceFirst(currentPath, newAlias);
			root = "";

			lastPos = pos;
			pos = path.indexOf('.', pos + 1);

		}

		if (!created) {
			StringBuilder sb = new StringBuilder();
			sb.append(getRootAlias());
			sb.append(".");
			sb.append(path);
			alias = sb.toString();
//			alias = generateAlias(root, path);
		} else {
			// // if (path.indexOf('.', pos + 1) == -1) {
			// alias = alias + path.substring(lastPos);
			// // }
		}

		return alias;
	}
	
	public String createSearchJPQL() {
		String select = generateSelectClause();
		String where = generateWhereClause();
		String orderBy = generateOrderByClause();
		String from = generateFromClause();

		StringBuilder sb = new StringBuilder();
		sb.append(select);
		sb.append(" ");
		sb.append(from);
		sb.append(" ");
		sb.append(where);
		sb.append(" ");
		sb.append(orderBy);
		return sb.toString();
	}

	public String createCountJPQL() {
		String where = generateWhereClause();
		String from = generateFromClause().replaceAll("FETCH ", "");

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT COUNT(*");
		// sb.append(getRootAlias());
		// sb.append(".");
		// sb.append(getUniqueId());
		sb.append(") ");
		sb.append(from);
		sb.append(" ");
		sb.append(where);

		return sb.toString();
	}

	protected String generateSelectClause() {
		StringBuilder sb = new StringBuilder("SELECT ");
		sb.append(getRootAlias());

		return sb.toString();
	}

	protected String generateWhereClause() {

		if (!getWhereClause().isEmpty()) {
			return getWhereClause();
		}

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		if (getListFilter() != null) {
			joinFields = new LinkedList<>();
			LinkedList<Filter> tmpFilters = new LinkedList<Filter>(getListFilter());
			for (Filter filter : tmpFilters) {
				if (!Operator.JOIN.equals(filter.getOp())) {
					if (first) {
						sb.append("WHERE ");
						first = false;
					}
					sb.append(createWhere(filter));
				} else {
					joinFields.add(filter);
					getListFilter().remove(filter);
				}
			}
		}

		// Salva para usar na query do contador
		setWhereClause(sb.toString());

		return sb.toString();
	}

	private String generateOrderByClause() {
		StringBuilder sb = null;
		boolean first = true;

		if (getOrderBy() != null) {
			for (Map.Entry<String, Boolean> order : getOrderBy().entrySet()) {
				if (first) {
					sb = new StringBuilder(" ORDER BY ");
					first = false;
				} else {
					sb.append(", ");
				}
				sb.append(createAlias(order.getKey()));
				sb.append(order.getValue() ? " DESC" : " ASC");
			}
		}

		if (first) {
			return "";
		}

		return sb.toString();
	}

	private String generateFromClause() {
		if (!getFromClause().isEmpty()) {
			return getFromClause();
		}
		StringBuilder sb = new StringBuilder(" FROM ");
		sb.append(this.beanClass.getName()).append(" ")
				.append(getRootAlias());

		sb.append(generateJoinClause());

		// Salva para usar na query do contador
		setFromClause(sb.toString());

		return sb.toString();
	}

	private String generateJoinClause() {
		StringBuilder sb = new StringBuilder();
		if (includeJoins) {
//			String root = getRootAlias();
			
			for (Filter filter : joinFields) {
//				if (Operator.JOIN.equals(filter.getOp())) {
					String alias = createAlias(filter.getKey());//generateAlias(root, filter.getKey());
					sb.append(" LEFT JOIN FETCH ");
//					sb.append(root);
//					sb.append(".");
//					sb.append(filter.getKey());
//					sb.append(" ");
					sb.append(alias);					
//				}
			}
//			List<Field> fields = ClassHelper.getJoinFields(beanClass);
//			for (Field field : fields) {
//				listAlias.put(root+"."+field.getName(), generateAlias(root,field.getName()));
//			}
		}
		
//		for (Map.Entry<String, String> alias : listAlias.entrySet()) {
//			sb.append(" LEFT JOIN ");
//			// sb.append(queryParam.getRootAlias());
//			// sb.append(".");
//			sb.append(alias.getKey());
//			sb.append(" ");
//			sb.append(alias.getValue());
//		}

		return sb.toString();
	}
	
	public String createWhere(Filter filter) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		Integer count = 0;
		try {
			switch (filter.getOp()) {
			case AND:
				first = true;
				sb.append(" ( ");
				for (Filter filterItem : filter.getFilters()) {
					if (!first) {
						sb.append(" AND ");
					} else {
						first = false;
					}
					sb.append(createWhere(filterItem));
				}
				sb.append(" ) ");

				break;

			case OR:
				first = true;
				sb.append(" ( ");
				for (Filter filterItem : filter.getFilters()) {
					if (!first) {
						sb.append(" OR ");
					} else {
						first = false;
					}
					sb.append(createWhere(filterItem));
				}
				sb.append(" ) ");
				break;

			case BETWEEN:
				sb.append(" ( ");
				filter.setAlias(generateAliasParameter(filter
						.getKey()));

				writePath(filter, sb);
				sb.append(" BETWEEN ");
				for (Filter filterItem : filter.getFilters()) {
					if (!first) {
						sb.append(" AND ");
					} else {
						first = false;
					}
					filterItem.setAlias(filter.getAlias() + "_" + count++);
					writeAliasParameter(filterItem, sb);
				}
				sb.append(" ) ");
				break;

			case LIKE:
				filter.setAlias(generateAliasParameter(filter
						.getKey()));
				sb.append(" LCASE(");
				writePath(filter, sb);
				sb.append(") like ");
				writeAliasParameter(filter, sb);
				break;

			case EQUAL:
				filter.setAlias(generateAliasParameter(filter
						.getKey()));
				writePath(filter, sb);
				sb.append(" = ");
				writeAliasParameter(filter, sb);
				break;

			case NOT_EQUAL:
				filter.setAlias(generateAliasParameter(filter
						.getKey()));
				writePath(filter, sb);
				sb.append(" != ");
				writeAliasParameter(filter, sb);
				break;

			case IN:
				filter.setAlias(generateAliasParameter(filter
						.getKey()));
				writePath(filter, sb);
				sb.append(" IN ( ");
				count = 0;
				first = true;
				for (Filter filterItem : filter.getFilters()) {
					if (!first) {
						sb.append(",");
					} else {
						first = false;
					}
					filterItem.setAlias(filter.getAlias() + "_" + count++);
					writeAliasParameter(filterItem, sb);
				}
				sb.append(" ) ");
				break;

			case NOT_IN:
				filter.setAlias(generateAliasParameter(filter
						.getKey()));
				writePath(filter, sb);
				sb.append(" NOT IN ( ");
				count = 0;
				first = true;
				for (Filter filterItem : filter.getFilters()) {
					if (!first) {
						sb.append(",");
					} else {
						first = false;
					}
					filterItem.setAlias(filter.getAlias() + "_" + count++);
					writeAliasParameter(filterItem, sb);
				}
				sb.append(" ) ");
				break;

			case IS_NULL:
				writePath(filter, sb);
				sb.append(" IS NULL ");
				break;

			case IS_NOT_NULL:
				writePath(filter, sb);
				sb.append(" IS NOT NULL ");
				break;

			case IS_TRUE:
				writePath(filter, sb);
				sb.append(" = 1 ");
				break;

			case IS_FALSE:
				writePath(filter, sb);
				sb.append(" = 0 ");
				break;

			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	private void writePath(Filter filter, StringBuilder sb) {
		sb.append(createAlias(filter.getKey()));
	}

	private void writeAliasParameter(Filter filter, StringBuilder sb) {
		sb.append(":");
		sb.append(filter.getAlias());
		sb.append(" ");
	}
	
	public void updateParameter(Query query) {
		updateParameter(query, getListFilter());		
	}
	
	public void updateParameter(Query query, List<Filter> filters) {
		for (Filter filter : filters) {
			if (filter.getFilters() == null) {
				if (filter.getValue() != null && filter.getAlias() != null) {
					query.setParameter(filter.getAlias(), filter.getValue());
				}
			} else {
				updateParameter(query, filter.getFilters());
			}
		}
	}
	
	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public String getRootAlias() {
		return rootAlias;
	}

	public void setRootAlias(String rootAlias) {
		this.rootAlias = rootAlias;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getFromClause() {
		return fromClause;
	}

	public void setFromClause(String fromClause) {
		this.fromClause = fromClause;
	}

	public String getWhereClause() {
		return whereClause;
	}

	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}

	public Map<String, Boolean> getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Map<String, Boolean> orderBy) {
		this.orderBy = orderBy;
	}

	public Map<String, Object> getFilters() {
		return filters;
	}

	public void setFilters(Map<String, Object> filters) {
		this.filters = filters;
	}

	public List<Filter> getListFilter() {
		return listFilter;
	}

	public void setListFilter(List<Filter> listFilter) {
		this.listFilter = listFilter;
	}
	
	public Map<String, String> getAlias() {
		return alias;
	}

	public Map<String, String> getAliasParameter() {
		return aliasParameter;
	}

	public List<Filter> getJoinFields() {
		return joinFields;
	}

	public boolean isIncludeJoins() {
		return includeJoins;
	}

	public void setIncludeJoins(boolean includeJoins) {
		this.includeJoins = includeJoins;
	}
	
}
