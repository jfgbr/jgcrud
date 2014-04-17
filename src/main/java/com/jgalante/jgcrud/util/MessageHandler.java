package com.jgalante.jgcrud.util;

import java.io.Serializable;
import java.util.MissingResourceException;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import com.jgalante.jgcrud.annotation.ResourceBundled;

@RequestScoped
public class MessageHandler implements Serializable{

	private static final long serialVersionUID = 1L;

	@Inject
	private FacesContext facesContext;
	
	@Inject
	@ResourceBundled(basename="messages",lang="en",country="US")
	private ResourceBundle bundle;

	private void addMessage(Severity severity, String key, Object... params) {
		try {
			facesContext.addMessage(null, new FacesMessage(severity, bundle.getString(key,params), null));
		} catch (MissingResourceException e){
			facesContext.addMessage(null, new FacesMessage(severity, key, null));
		}
	}
	
	private void addMessage(String complemento, Boolean posicao, Severity severity, String key, Object... params) {
		try {
			if (posicao)
				facesContext.addMessage(null, new FacesMessage(severity, complemento + " " + bundle.getString(key,params), null));
			else {
				facesContext.addMessage(null, new FacesMessage(severity, bundle.getString(key,params) + " " + complemento, null));
			}
		} catch (MissingResourceException e){
			facesContext.addMessage(null, new FacesMessage(key));
		}
	}
	
	public void info(String complemento, Boolean pos, String key, Object... params) {
		addMessage(complemento, pos, FacesMessage.SEVERITY_INFO, key, params);
	}
	
	public void info(String key, Object... params) {
		addMessage(FacesMessage.SEVERITY_INFO, key,params);
	}

	public void warn(String key, Object... params) {
		addMessage(FacesMessage.SEVERITY_WARN, key,params);
	}

	public void error(String key, Object... params) {
		addMessage(FacesMessage.SEVERITY_ERROR, key,params);
	}
	
	public void fatal(String key, Object... params) {
		addMessage(FacesMessage.SEVERITY_FATAL, key,params);
	}

	public FacesContext getFacesContext() {
		return facesContext;
	}

}
