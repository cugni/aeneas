/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.workloader.distributions;

import es.bsc.aeneas.workloader.controller.Range;

/**
 *
 * @author cesare
 */
public interface RangeGenerator<T extends Comparable> {
    public Range<T> getNextRange();
}
