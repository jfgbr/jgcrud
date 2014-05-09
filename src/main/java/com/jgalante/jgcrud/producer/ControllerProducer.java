package com.jgalante.jgcrud.producer;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;

import com.jgalante.jgcrud.annotation.Controller;
import com.jgalante.jgcrud.controller.BaseController;
import com.jgalante.jgcrud.entity.BaseEntity;
import com.jgalante.jgcrud.persistence.BaseDAO;
import com.jgalante.jgcrud.util.Util;

public class ControllerProducer implements Serializable{

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Produces
	@Dependent
	@Controller
	public <T extends BaseEntity> BaseController<T,BaseDAO<T>> create(InjectionPoint ip, BeanManager bm) {
		
		if (ip.getAnnotated().isAnnotationPresent(Controller.class)) {
			Class<T> entityClass;
			Class<? extends BaseController<T,BaseDAO<T>>> controllerClass = null;
			try {
				Type[] typeArguments = getTypeArguments(((Class<?>) ip.getBean().getBeanClass()));
				
//						((ParameterizedType) ((Class<?>)((Class<?>) ip
//						.getBean().getBeanClass()).getGenericSuperclass()).getGenericSuperclass())
//						.getActualTypeArguments();
				entityClass = (Class<T>) typeArguments[0];
				controllerClass = (Class<? extends BaseController<T,BaseDAO<T>>>) typeArguments[1];
			} catch (Exception e) {
				entityClass = (Class<T>) BaseEntity.class;
				Class<?> tmpClass = BaseController.class;
				controllerClass = (Class<? extends BaseController<T, BaseDAO<T>>>) tmpClass;
			}
			
            try {
//				Constructor<? extends GenericDAO<T>> constructor = gDAOClass.getDeclaredConstructor();
//				return constructor.newInstance();
            	BaseController<T,BaseDAO<T>> genericController = (BaseController<T,BaseDAO<T>>) Util.getBeanByType(controllerClass, bm);
            	genericController.setEntityClass(entityClass);
//            	((BaseView<T, BaseController<T,BaseDAO<T>>>) ip.getBean().getTypes().iterator().next()).setDataModel(new DelegateDataModel<T>(genericController));
//            	((BaseView<T, BaseController<T,BaseDAO<T>>>) ip.getBean()).setDataModel(new DelegateDataModel<T>(genericController));
//            	BaseView<T, com.jgalante.jgcrud.controller.GenericController>
				return genericController;
			} catch (Exception e) {
				e.printStackTrace();
			}
            
            return null;
        }
        throw new IllegalArgumentException("Annotation @Controller is required when injecting GenericController or subclasses");
    }
	
	private Type getSuperClassType(Type type) {
		return ((Class<?>) type).getGenericSuperclass();
	}
	
	private Type[] getTypeArguments(Type type) {
		Type superClassType = getSuperClassType(type);
		Type[] typeArguments = null;
		try {
			typeArguments = ((ParameterizedType) (superClassType))
					.getActualTypeArguments();
			
		} catch (Exception e) {
			typeArguments = getTypeArguments(superClassType);
		}
		return typeArguments;
	}
	
}
