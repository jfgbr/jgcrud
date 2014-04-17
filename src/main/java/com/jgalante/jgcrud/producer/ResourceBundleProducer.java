package com.jgalante.jgcrud.producer;

import java.io.Serializable;
import java.util.Locale;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import com.jgalante.jgcrud.annotation.ResourceBundled;
import com.jgalante.jgcrud.util.ResourceBundle;

public class ResourceBundleProducer implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * This method is the factory default for ResourceBundle. It creates the ResourceBundle based on a file called
	 * messages.properties.
	 */
	@Default
	@Produces
	public ResourceBundle createDefault(InjectionPoint ip) {
		return new ResourceBundle("messages");
	}

	/**
	 * This method is the factory default for ResourceBundle. It creates the ResourceBundle based on a file called
	 * messages.properties.
	 */
	@ResourceBundled
	@Produces
	public ResourceBundle createNamed(InjectionPoint ip) {
		ResourceBundled resourceBundled = ip.getAnnotated().getAnnotation(ResourceBundled.class);
		String baseName = resourceBundled.basename();
		String lang = resourceBundled.lang();
		String country = resourceBundled.country();
		Locale locale;
		if (lang != null && country != null) {
			locale = new Locale(lang, country);
		} else {
			locale = Locale.getDefault();
		}
		return new ResourceBundle(baseName, locale);
	}
}