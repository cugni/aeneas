/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.codegenerator.query.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author cesare
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ReturnVars {
    Var[] value();
}
