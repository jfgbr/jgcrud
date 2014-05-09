package com.jgalante.jgcrud.stereotype;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.inject.Stereotype;

/**
 * Identifies a <b>business controller</b> class. Business controller objects typically implement the controller design
 * pattern, i.e., they contain no data elements but methods that orchestrate interaction among business entities.
 * <p>
 * A <i>Business Controller</i> is:
 * <ul>
 * <li>defined when annotated with {@code @BusinessController}</li>
 * <li>automatically injected whenever {@code @Inject} is used</li>
 * </ul>
 * 
 * @see @Controller
 */
@Controller
@Stereotype
@Inherited
@Target(TYPE)
@Retention(RUNTIME)
public @interface BusinessController {
}