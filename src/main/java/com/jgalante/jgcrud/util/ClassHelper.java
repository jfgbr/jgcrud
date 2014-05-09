package com.jgalante.jgcrud.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.el.PropertyNotFoundException;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

public class ClassHelper {

	/**
	 * Obtém o tipo da propriedade da classe. A propriedade pode ser somente
	 * ("nome") ou ("empresa.nome").
	 * 
	 * @param rootClass
	 *            a classe investigada
	 * @param propertyPath
	 *            o caminho da propriedade
	 * @return o tipo da propriedade passada
	 * 
	 * @throws PropertyNotFoundException
	 *             se a classe não tiver a propriedade passada
	 */
	@SuppressWarnings("rawtypes")
	public static Class<?> getExpectedClass(Class<?> rootClass,
			String propertyPath) throws PropertyNotFoundException {
		if (propertyPath == null || "".equals("propertyPath"))
			return rootClass;
		String[] chain = propertyPath.split("\\.");

		Class<?> klass = rootClass;

		for (String property : chain) {
			String getMethod = "get" + property.substring(0, 1).toUpperCase()
					+ property.substring(1);
			String isMethod = "is" + property.substring(0, 1).toUpperCase()
					+ property.substring(1);
			boolean found = false;

			for (Method method : klass.getMethods()) {
				if (method.getParameterTypes().length == 0
						&& (method.getName().equals(getMethod) || method
								.getName().equals(isMethod))) {
					klass = method.getReturnType();
					
					Type t = method.getGenericReturnType();
				    if(t instanceof ParameterizedType){
				    	//http://stackoverflow.com/questions/182872/how-to-test-whether-method-return-type-matches-liststring
				    	
				    	 ParameterizedType pt = (ParameterizedType)t;
				         Type[] actualGenericParameters = pt.getActualTypeArguments();
				         for (int i = 0; i < actualGenericParameters.length; i++) {
				             if (actualGenericParameters[i] instanceof Class) {
//				                 System.out.println(((Class) actualGenericParameters[i]).getName());
				                 klass = (Class) actualGenericParameters[i];
				                 break;
				             } else {
				                 System.out.println("\tFailure generic parameter is not a class");
				             }
				         }
				    }
					
					found = true;
					break;
				}
			}
			if (!found)
				throw new PropertyNotFoundException("Could not find property '"
						+ propertyPath + "' on class " + rootClass + ".");
		}

		return klass;
	}

	/**
	 * Obtém uma instância da classe com o valor passados por parametro.
	 * 
	 * @param value
	 *            o valor a ser convertido para classe
	 * @param type
	 *            a classe que receberá o valor
	 * @return a instância da classe passada
	 * @throws ClassCastException
	 *             se não for possível converter o valor para a classe
	 */
	public static Object convertIfNeeded(Object value, Class<?> type)
			throws ClassCastException {
		if (value == null)
			return value;
		
		if (type.isInstance(value))
			return value;
		
		if (isAssignableFrom(String.class, type)) {
			return value.toString();
		}
		
		if (isAssignableFrom(Boolean.class, type)) {
			return new Boolean(value.toString());
		}

		if (isAssignableFrom(Date.class, type)) {
			SimpleDateFormat simpleDateFormat;
			
			if(value.toString().contains(";")){
				List<String> values = Arrays.asList(value.toString().split(";"));
				String data1 = values.get(0).toString().concat(" 00:00:00");
				String data2 = values.get(1).toString().concat(" 23:59:59");
				return data1.concat(";").concat(data2);				
			}else{
				if (!value.toString().contains(":")) {
					value = value.toString().concat(" 00:00:00");
				}
				
				if (value.toString().contains("/")) {
					simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				} else {
					simpleDateFormat = new SimpleDateFormat("ddMMyyyy HH:mm:ss");
				}
				try {
					return simpleDateFormat.parse(value.toString());
				} catch (ParseException e) {
					return null;
				}
			}

		}
////TODO: verify if it is needed and the best place
//		if (isAssignableFrom(CNPJ.class, type)) {
//			return FormatHelper.converteStringToCnpj(value.toString());
//		}

		try {
			if (isAssignableFrom(Number.class, type) || isAssignableFrom(Number.class, type.getSuperclass())) {
				// converte para o padrão de casas decimais
				String newValue = value.toString().replaceAll("/.", "").replaceAll(",", ".");
				if (type.equals(Double.class)) {
					return new Double(newValue.toString());
				} else if (type.equals(Float.class)) {
					return new Float(newValue.toString());
				} else if (type.equals(Long.class)) {
					return new Long(newValue.toString());
				} else if (type.equals(Integer.class)) {
					return new Integer(newValue.toString());
				} else if (type.equals(Short.class)) {
					return new Short(newValue.toString());
				} else if (type.equals(BigInteger.class)) {
					return new BigInteger(newValue.toString());
				} else {
					
						return type.getConstructor(String.class).newInstance(
								newValue.toString());
					
				}
			}
		} catch (NumberFormatException e) {			
		} catch (IllegalArgumentException e) {
		} catch (SecurityException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		} catch (NoSuchMethodException e) {
		}

		try {
			return value.toString();
		} catch (Exception e) {
			throw new ClassCastException("Unable to convert value of type "
					+ value.getClass().getName() + " to type " + type.getName());
		}
	}

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
	
	public static List<Field> getJoinFields(Class<?> type) {
		final List<Field> fields = getAllFields(new LinkedList<Field>(), type); 
		List<Field> result = new LinkedList<>();
		for (Field field : fields) {
			if (verifyJoinFieldAnotation(field)) { 
				result.add(field);
			}			
		}
		return result;
	}

	public static Field getJoinField(Class<?> type, String fieldName) {
		final List<Field> fields = getAllFields(new LinkedList<Field>(), type); 
		
		for (Field field : fields) {
			if (verifyJoinFieldAnotation(field)) {
				try {
					field.setAccessible(true);
					if (field.getName().equals(fieldName)) {
						return field;
					}
	
				} catch (IllegalArgumentException e) {
					continue;
	
				}
			}
		}
		return null;
	}
	
	private static boolean verifyJoinFieldAnotation(Field field) {
		if (field.isAnnotationPresent(OneToMany.class)
				|| field.isAnnotationPresent(ManyToOne.class)
				|| field.isAnnotationPresent(OneToOne.class)
				|| field.isAnnotationPresent(ManyToMany.class)) {
			
			field.setAccessible(true);
			return true;
			
		} else {
			return false;
		}
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

	/**
	 * Identifica se a classe é uma instância da classe esperada
	 * @param assignableClass instância da classe
	 * @param expectedClass classe esperada
	 * @return verdadeiro ou falso
	 */
	public static boolean isAssignableFrom(Class<?> assignableClass,
			Class<?> expectedClass) {
		return assignableClass.isAssignableFrom(expectedClass);
	}

	/**
	 * Converte um {@link InputStream} para um {@link File}
	 * @param inputStream
	 * @return file
	 */
	//TODO: verify if it is needed and the best place
//	public static File convertInputStreamToFile(InputStream inputStream) {
//		try {
//			File tempFile = File.createTempFile("tempFile", "tmp");
//			FileOutputStream out;
//			out = new FileOutputStream(tempFile);
//			IOUtils.copy(inputStream, out);
//			return tempFile;
//		} catch (FileNotFoundException e1) {
//		} catch (IOException e1) {
//		}
//		return null;
//	}
	
	/**
	 * Verifica se o arquivo informado é do tipo esperado
	 * 
	 * @param file um arquivo {@link File}
	 * @param expectedContentType um array de {@link String} com os tipos possíveis
	 * @return verdadeiro ou falso
	 */
	//TODO: verify if it is needed and the best place
//	public static Boolean isAssignableFrom(File file, String... expectedContentType) {
//		try {
//			String mimeType = Magic.getMagicMatch(file, false).getMimeType();
//			for (String contentType : expectedContentType) {
//				if (mimeType.contains(contentType))
//					return true;
//			}
//		} catch (MagicParseException e) {
//		} catch (MagicMatchNotFoundException e) {
//		} catch (MagicException e) {
//		} catch (Exception e) {
//		}
//		
//		return false;
//	}
}
