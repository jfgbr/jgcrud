package com.jgalante.jgcrud.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;
import javax.persistence.EntityManager;

/**
 * This is the CDI {@link Qualifier} that can be used to determine what objects
 * qualify for which injection points. For this application, it is used to
 * qualify the {@link EntityManager} injection point.
 * 
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
public @interface Controller {}
