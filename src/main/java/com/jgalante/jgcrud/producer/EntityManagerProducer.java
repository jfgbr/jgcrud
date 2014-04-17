package com.jgalante.jgcrud.producer;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.jgalante.jgcrud.annotation.DataRepository;

//@Stateful
//@ApplicationScoped
//@RequestScoped
public class EntityManagerProducer{
	
	private EntityManager entityManager;
	
	@Produces @DataRepository
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
}
