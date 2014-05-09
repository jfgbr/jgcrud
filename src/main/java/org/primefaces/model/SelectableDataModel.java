package org.primefaces.model;

public interface SelectableDataModel<T> {
    
    public Object getRowKey(T object);
    
    public T getRowData(String rowKey);
}
