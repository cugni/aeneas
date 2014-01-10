/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.nodeagent.model;

import javax.annotation.Resource;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 *
 * @author ccugnasc
 */
@Resource
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class AETest implements Comparable<AETest> {

    private String name;
    private Long start;
    private Long stop;

    public AETest(String context, String name, Long start, Long stop) {
        this.name = name;
        this.start = start;
        this.stop = stop;
    }

    public AETest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getStop() {
        return stop;
    }

    public void setStop(Long stop) {
        this.stop = stop;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AETest other = (AETest) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(AETest o) {
        if (start == null) {
            return -1;
        }
        if (o.start == null) {
            return 1;
        }
        return start.compareTo(o.start);
    }
}
