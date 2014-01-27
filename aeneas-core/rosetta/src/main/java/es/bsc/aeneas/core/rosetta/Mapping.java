/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.rosetta;

import es.bsc.aeneas.core.model.gen.DestType;
import es.bsc.aeneas.core.model.util.TransformerUtil;

/**
 *
 * @author ccugnasc
 */
public class Mapping {

    private final Object value;
    private final DestType dest;
    private Object res = null;

    public Object getTransformedValue() {
        if (res != null) {
            return res;
        }
        res = TransformerUtil.transform(dest, value);
        return res;
    }

    public Mapping(DestType dest, Object value) {
        this.value = value;
        this.dest = dest;
    }

    public DestType getDest() {
        return dest;
    }
}
