package com.jgalante.jgcrud.producer;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.faces.context.FacesContext;

@RequestScoped
public class FacesContextProducer implements Serializable {

	private static final long serialVersionUID = 1L;

	@Default
	@Produces
	public FacesContext getFacesContext(InjectionPoint ip) {
		return FacesContext.getCurrentInstance();
	}

}
