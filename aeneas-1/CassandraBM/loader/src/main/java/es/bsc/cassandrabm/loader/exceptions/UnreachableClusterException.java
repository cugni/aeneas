/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.loader.exceptions;

/**
 *
 * @author cesare
 */
public class UnreachableClusterException extends Exception {

    public UnreachableClusterException(Throwable cause) {
        super(cause);
    }

    public UnreachableClusterException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnreachableClusterException(String message) {
        super(message);
    }

    public UnreachableClusterException() {
    }
    
}
