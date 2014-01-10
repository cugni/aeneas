/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.codegenerator.query;

/**
 *
 * @author cesare
 */
public class QueryNotImplementedException extends UnsupportedOperationException{

    public QueryNotImplementedException(Throwable cause) {
        super(cause);
    }

    public QueryNotImplementedException(String message, Throwable cause) {
        super(message, cause);
    }

    public QueryNotImplementedException(String message) {
        super(message);
    }

    public QueryNotImplementedException() {
    }
    
}
