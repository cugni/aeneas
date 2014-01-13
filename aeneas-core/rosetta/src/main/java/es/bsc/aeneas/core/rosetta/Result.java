/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.rosetta;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ccugnasc
 */
public class Result {

    private boolean success;
    private boolean hasData;
    private Map<String, String> meta;
    public Object result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean hasData() {
        return hasData;
    }

    public void setHasData(boolean hasData) {
        this.hasData = hasData;
    }

    public  Map<String, String> getMeta() {
        return meta;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Result merge(Result other) {
        Result n = new Result();
        n.meta = new HashMap<String, String>();
        n.meta.putAll(this.meta);
        n.meta.putAll(other.meta);
        n.hasData = this.hasData || other.hasData;
        if (n.hasData) {
            if (this.hasData && other.hasData) {
                if (this.result instanceof List && other.result instanceof List) {
                    n.result = ((List) this.result).addAll(((List) other.result));
                } else if (this.result instanceof List) {
                    n.result = ((List) this.result).add(other.result);
                } else if (other.result instanceof List) {
                    n.result = ((List) other.result).add(this.result);
                } else {
                    n.result = Arrays.asList(other.result, this.result);
                }
            } else if (this.hasData) {
                n.result = this.result;
            } else {
                n.result = other.result;
            }

        }
        return n;


    }

    public static Result mergeAll(Result... other) {
        checkArgument(other.length > 0);
        Result r1 = other[0];
        for (int i = 1; i < other.length; i++) {
            r1 = r1.merge(other[i]);
        }
        return r1;

    }

    public static Result mergeAll(List<Result> others) {
        return mergeAll(others.toArray(new Result[others.size()]));
    }

    public Result merge(Result... other) {
        checkArgument(other.length > 0);
        Result r1 = this;
        for (int i = 0; i < other.length; i++) {
            r1 = r1.merge(other[i]);
        }
        return r1;

    }
}
