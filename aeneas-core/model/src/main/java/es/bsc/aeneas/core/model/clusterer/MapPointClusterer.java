/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.model.clusterer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import es.bsc.aeneas.core.model.MapPointType;
 
import java.util.List;

/**
 *
 * @author cesare
 */
public class MapPointClusterer extends TypeClusterer<MapPointType> {

    private double xmul, ymul, xmin, ymin,ncluster;
   
    /*
     * This class is not multithread. The method setGrouping should be called
     * only on the instantialization of the
     */

    @Override
    public void setGrouping(MapPointType from, MapPointType to, Integer intervals) {
        ncluster = Math.pow(intervals,1/3.0);
        xmin = Math.abs(Math.abs(from.getLat()) > Math.abs(to.getLat()) ? to.getLat() : from.getLat());
        ymin = Math.abs(Math.abs(from.getLon()) > Math.abs(to.getLon()) ? to.getLon() : from.getLon());
        xmul = scale(from.getLat(), to.getLat());
        ymul = scale(from.getLon(), to.getLon());


    }

    private double scale(double from, double to) {
        if (from > to) {
            return ncluster/ Math.abs(from / to);
        } else {
            return ncluster/Math.abs(to / from);
        }
    }

    @Override
    public MapPointType getGroup(MapPointType value) {
        MapPointType res = new MapPointType();

        res.setLat(Math.floor(value.getLat() / xmin * xmul));
        res.setLon(Math.floor(value.getLon() / ymin * ymul));
       
        return res;
    }

    @Override
    public List<MapPointType> getGroupsInterval(MapPointType from, MapPointType to) {
        MapPointType gf = getGroup(from);
        MapPointType gt = getGroup(to);
        int x = (int) Math.round(gt.getLat()- gf.getLat());
        int y = (int) Math.round(gt.getLon() - gf.getLon());
      
        int xadde = x >= 0 ? 1 : -1;
        int yadde = y >= 0 ? 1 : -1;
        Builder<MapPointType> l = ImmutableList.builder();
        for (int ix = 0; ix <= x*xadde; ix ++) {
            for (int iy = 0; iy <= y*yadde; iy ++) {
                    MapPointType p = new MapPointType();
                    p.setLat(gf.getLat() + ix*xadde);
                    p.setLon(gf.getLon() + iy*yadde);
                    l.add(p);
              
            }
        }
        return l.build();
    }

    @Override
    public MapPointType parseFromString(String s) {
        return MapPointType.fromString(s);
    }

  
}
