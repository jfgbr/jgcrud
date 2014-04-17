package com.jgalante.jgcrud.util;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;

public class ResourceBundle extends java.util.ResourceBundle implements Serializable {

	private static final long serialVersionUID = 1L;

	private String baseName;

	private transient java.util.ResourceBundle delegate;

	private final Locale locale;

	private java.util.ResourceBundle getDelegate() {
		if (delegate == null) {
			try {
				ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
				delegate = ResourceBundle.getBundle(baseName, locale, classLoader);

			} catch (MissingResourceException mre) {
				delegate = ResourceBundle.getBundle(baseName, locale);
			}
		}

		return delegate;
	}

	/**
	 * Constructor that set values of baseName and locale.
	 * 
	 * @param baseName
	 * 			the base name to construct the complete bundle name.
	 * 
	 * @param locale
	 * 			locale to define the choosen bundle.
	 */
	public ResourceBundle(String baseName, Locale locale) {
		this.baseName = baseName;
		this.locale = locale;
	}
	
	/**
	 * Constructor that set values of baseName and locale.
	 * 
	 * @param baseName
	 * 			the base name to construct the complete bundle name.
	 * 
	 */
	public ResourceBundle(String baseName) {
		this.baseName = baseName;
		this.locale = Locale.getDefault();;
	}

	@Override
	public boolean containsKey(String key) {
		return getDelegate().containsKey(key);
	}

	@Override
	public Enumeration<String> getKeys() {
		return getDelegate().getKeys();
	}

	@Override
	public Locale getLocale() {
		return getDelegate().getLocale();
	}

	@Override
	public Set<String> keySet() {
		return getDelegate().keySet();
	}

	public String getString(String key, Object... params) {
		return Util.getString(getString(key), params);
	}

	@Override
	protected Object handleGetObject(String key) {
		Object result;

		try {
			Method method = getDelegate().getClass().getMethod("handleGetObject", String.class);

			method.setAccessible(true);
			result = method.invoke(delegate, key);
			method.setAccessible(false);

		} catch (Exception cause) {
			throw new RuntimeException(cause);
		}

		return result;
	}
}

