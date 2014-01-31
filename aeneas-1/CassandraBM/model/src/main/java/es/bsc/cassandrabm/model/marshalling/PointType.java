package es.bsc.cassandrabm.model.marshalling;

import java.io.Serializable;

public class PointType implements Serializable, Comparable<PointType>  {

     

    private Double x;
    private Double y;
    private Double z;

    public PointType() {
    }

    public PointType(Double x, Double y, Double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getZ() {
        return z;
    }

    public void setZ(Double z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "{" + x + "," + y + "," + z + "}";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((x == null) ? 0 : x.hashCode());
        result = prime * result + ((y == null) ? 0 : y.hashCode());
        result = prime * result + ((z == null) ? 0 : z.hashCode());
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
        if (!getClass().equals(obj.getClass())) {
            return false;
        }
        PointType other = (PointType) obj;
        if (x == null) {
            if (other.x != null) {
                return false;
            }
        } else if (!x.equals(other.x)) {
            return false;
        }
        if (y == null) {
            if (other.y != null) {
                return false;
            }
        } else if (!y.equals(other.y)) {
            return false;
        }
        if (z == null) {
            if (other.z != null) {
                return false;
            }
        } else if (!z.equals(other.z)) {
            return false;
        }
        return true;
    }

   
    @Override
    public int compareTo(PointType o) {
        
        if (o.equals(this)) {
            return 0;
        } else {
            if(this.x.equals(o.x)){
                if(this.y.equals(o.y)){
                    if(this.z.equals(o.z)){
                        return 0;
                    }else{
                        return z.compareTo(o.z);
                    }
                }else{
                    return y.compareTo(o.y);
                }
            }else{
                return x.compareTo(o.x);
            }
        }
    }
    public boolean isInterior(PointType p, Double area){
        if(x>=p.x-area&&x<=p.x+area&& //
           y>=p.y-area&&y<=p.y+area &&//
                z>=p.z-area&&z<=p.z+area) {
            return true;
        }
        return false;
        
    }
    public double getNorm(){
        return Math.sqrt(x*x+y*y+z*z);
                
    }
    public static PointType fromString(String s) {
        try {
            String[] split = s.replaceAll("[\\{\\}]", "").split(",");
            PointType p = new PointType();
            p.setX(Double.parseDouble(split[0]));
            p.setY(Double.parseDouble(split[1]));
            p.setZ(Double.parseDouble(split[2]));
            return p;
        } catch (Exception e) {
            throw new IllegalArgumentException("The format of the string " + s + " is not valid.");
        }
    }

  
    public PointType duplicate(){
       PointType p=new PointType();
       p.setX(x);
       p.setY(y);
       p.setZ(z);
       return p;
    }
}

