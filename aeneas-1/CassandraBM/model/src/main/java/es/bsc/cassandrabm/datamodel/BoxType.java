package es.bsc.cassandrabm.datamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BoxType implements Serializable {

    public final static int NUMBER_OF_POINTS = 3;
    private List<PointType> points = new ArrayList<PointType>(NUMBER_OF_POINTS);

    public List<PointType> getPoints() {
        return points;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(30);
        for (PointType p : points) {
            sb.append(p).append(",");
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((points == null) ? 0 : points.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BoxType other = (BoxType) obj;
        if (points == null) {
            if (other.points != null) {
                return false;
            }
        } else {
            if (!points.equals(other.points)) {
                return false;
            }
        }
        return true;
    }
}
