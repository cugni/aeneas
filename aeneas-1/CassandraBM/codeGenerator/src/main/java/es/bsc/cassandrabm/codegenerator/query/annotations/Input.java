/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.codegenerator.query.annotations;

/**
 *
 * @author cesare
 */
public @interface Input {

    String name();

    String from();

    String to() ;
    
    String step() default "";
    
    String wideMax() default "";

    Class type();

    boolean interval() default false;
}
