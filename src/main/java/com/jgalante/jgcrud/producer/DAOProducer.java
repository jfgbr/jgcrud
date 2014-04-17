package com.jgalante.jgcrud.producer;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;

import com.jgalante.jgcrud.annotation.DAO;
import com.jgalante.jgcrud.entity.BaseEntity;
import com.jgalante.jgcrud.persistence.BaseDAO;
import com.jgalante.jgcrud.util.Util;

public class DAOProducer implements Serializable {

    private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Produces
	@Dependent
	@DAO
	public <T extends BaseEntity> BaseDAO<T> create(InjectionPoint ip, BeanManager bm) {
		
		if (ip.getAnnotated().isAnnotationPresent(DAO.class)) {
			Type[] typeArguments = ((ParameterizedType) ((Class<?>) ip
					.getBean().getBeanClass()).getGenericSuperclass())
					.getActualTypeArguments();
			Class<T> entityClass = (Class<T>) typeArguments[0];
			Class<? extends BaseDAO<T>> daoClass = (Class<? extends BaseDAO<T>>) typeArguments[1];
            try {
//				Constructor<? extends GenericDAO<T>> constructor = gDAOClass.getDeclaredConstructor();
//				return constructor.newInstance();
            	BaseDAO<T> genericDAO = (BaseDAO<T>) Util.getBeanByType(daoClass, bm);
            	genericDAO.setEntityClass(entityClass);
				return genericDAO;
			} catch (Exception e) {
				e.printStackTrace();
			}
            
            return null;
        }
        throw new IllegalArgumentException("Annotation @DAO is required when injecting GenericDAO or subclasses");
    }
 

	
}
