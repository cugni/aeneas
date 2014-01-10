package es.bsc.aeneas.datamodel;

import java.io.Serializable;

public class MapPointType implements Serializable, Comparable<MapPointType>  {

     

    private Double lat;
    private Double lon;

    public MapPointType() {
    }

    public MapPointType(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    


  

    @Override
    public String toString() {
        return "{" + lat + "," + lon +  "}";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((lat == null) ? 0 : lat.hashCode());
        result = prime * result + ((lon == null) ? 0 : lon.hashCode());
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
        MapPointType other = (MapPointType) obj;
        if (lat == null) {
            if (other.lat != null) {
                return false;
            }
        } else if (!lat.equals(other.lat)) {
            return false;
        }
        if (lon == null) {
            if (other.lon != null) {
                return false;
            }
        } else if (!lon.equals(other.lon)) {
            return false;
        }
        
        return true;
    }

   
    @Override
    public int compareTo(MapPointType o) {
        
        if (o.equals(this)) {
            return 0;
        } else {
            if(this.lat.equals(o.lat)){
                if(this.lon.equals(o.lon)){
                       return 0;
                }else{
                    return lon.compareTo(o.lon);
                }
            }else{
                return lat.compareTo(o.lat);
            }
        }
    }
    public boolean isInterior(MapPointType p, Double area){
        if(lat>=p.lat-area&&lat<=p.lat+area&& //
           lon>=p.lon-area&&lon<=p.lon+area 
                ) {
            return true;
        }
        return false;
        
    }
    public double getNorm(){
        return Math.sqrt(lat*lat+lon*lon);
                
    }
    public static MapPointType fromString(String s) {
        try {
            String[] split = s.replaceAll("[\\{\\}]", "").split(",");
            MapPointType p = new MapPointType();
            p.setLat(Double.parseDouble(split[0]));
            p.setLon(Double.parseDouble(split[1]));
            return p;
        } catch (Exception e) {
            throw new IllegalArgumentException("The format of the string " + s + " is not valid.");
        }
    }

  
    public MapPointType duplicate(){
       MapPointType p=new MapPointType();
       p.setLat(lat);
       p.setLon(lon);
       return p;
    }
}

