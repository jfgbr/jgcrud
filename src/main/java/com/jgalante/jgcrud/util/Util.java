package com.jgalante.jgcrud.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Enumerated;

public class Util {
	
	/**
	 * Obtém a propriedade de uma classe que está anotado por: {@link Column}, {@link Basic}, 
	 * {@link Enumerated} ou {@link AttributeOverride}.
	 * 
	 * @param type tipo da classe pesquisada
	 * @param fieldName nome da propriedade
	 * @return a instância {@link Field} da propriedade
	 */
	public static Field getField(Class<?> type, String fieldName) {
		final List<Field> fields = getAllFields(new LinkedList<Field>(), type); 
		
		for (Field field : fields) {
			if (!field.isAnnotationPresent(Column.class)
					&& !field.isAnnotationPresent(Basic.class)
					&& !field.isAnnotationPresent(Enumerated.class)
					&& !field.isAnnotationPresent(AttributeOverride.class)) {
				continue;
			}

			try {
				field.setAccessible(true);
				if (field.getName().equals(fieldName)) {
					return field;
				}

			} catch (IllegalArgumentException e) {
				continue;

			}
		}
		return null;
	}
	
	/**
	 * Obtém todas as propriedades de uma classe.
	 * 
	 * @param fields lista com algumas propriedades
	 * @param type tipo da classe pesquisada
	 * @return lista com as novas propriedades encontradas
	 */
	public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
	    for (Field field: type.getDeclaredFields()) {
	        fields.add(field);
	    }

	    if (type.getSuperclass() != null) {
	        fields = getAllFields(fields, type.getSuperclass());
	    }

	    return fields;
	}
	
	public static Object getBeanByName(String name, BeanManager bm) { 
        Bean<?> bean = bm.getBeans(name).iterator().next();
        CreationalContext<?> ctx = bm.createCreationalContext(bean);
        Object o = bm.getReference(bean, bean.getBeanClass(), ctx);
        return o;
    }
    
    public static Object getBeanByType(Type t, BeanManager bm){
        Bean<?> bean = bm.getBeans(t).iterator().next();
        CreationalContext<?> ctx = bm.createCreationalContext(bean);
        Object o = bm.getReference(bean, bean.getBeanClass(), ctx);
        return o;
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T getBeansReference(Class<T> beanClass, BeanManager bm, Annotation... qualifiers){
        Bean<?> bean = bm.getBeans(beanClass).iterator().next();
        CreationalContext<?> ctx = bm.createCreationalContext(bean);
        Type beanType = beanClass == null ? bean.getBeanClass() : beanClass;
		InjectionPoint injectionPoint = new CustomInjectionPoint(bean, beanType, qualifiers);
		
		return (T) bm.getInjectableReference(injectionPoint, ctx);
        
//        Object o = bm.getReference(bean, bean.getBeanClass(), ctx);
//        return o;
    }
    
    static class CustomInjectionPoint implements InjectionPoint {

		private final Bean<?> bean;

		private final Type beanType;

		private final Set<Annotation> qualifiers;

		public CustomInjectionPoint(Bean<?> bean, Type beanType, Annotation... qualifiers) {
			this.bean = bean;
			this.beanType = beanType;
			this.qualifiers = new HashSet<Annotation>(Arrays.asList(qualifiers));
		}

		@Override
		public Type getType() {
			return this.beanType;
		}

		@Override
		public Set<Annotation> getQualifiers() {
			return this.qualifiers;
		}

		@Override
		public Bean<?> getBean() {
			return this.bean;
		}

		@Override
		public Member getMember() {
			return null;
		}

		@Override
		public boolean isDelegate() {
			return false;
		}

		@Override
		public boolean isTransient() {
			return false;
		}

		@Override
		public Annotated getAnnotated() {
			return new Annotated() {

				@Override
				public Type getBaseType() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public Set<Type> getTypeClosure() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				@SuppressWarnings("unchecked")
				public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
					T result = null;

					for (Annotation annotation : getAnnotations()) {
						if (annotation.annotationType() == annotationType) {
							result = (T) annotation;
							break;
						}
					}

					return result;
				}

				@Override
				public Set<Annotation> getAnnotations() {
					return qualifiers;
				}

				@Override
				public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
					return qualifiers.contains(annotationType);
				}
			};
		}
	}

	/**
	 * <p>
	 * Replaces the numbers between braces in the given string with the given
	 * parameters. The process will replace a number between braces for the
	 * parameter for which its order in the set of parameters matches with the
	 * number of the given string.
	 * <p>
	 * For exemple, if is received the following string
	 * "Treats an {0} exception" and the set of parameters
	 * {"DemoiselleException"}, the return will be the following string:
	 * "Treats an DemoiselleException exception".
	 * 
	 * @param string
	 *            with the numbers with braces to be replaced with the
	 *            parameters.
	 * @param params
	 *            parameters that will replace the number with braces in the
	 *            given string.
	 * @return String string with numbers replaced with the matching parameter.
	 */
	public static String getString(final String string, final Object... params) {
		String result = null;

		if (string != null) {
			result = new String(string);
		}

		if (params != null && string != null) {
			for (int i = 0; i < params.length; i++) {
				if (params[i] != null) {
					result = result.replaceAll("\\{" + i + "\\}",
							Matcher.quoteReplacement(params[i].toString()));
				}
			}
		}

		return result;
	}
}
