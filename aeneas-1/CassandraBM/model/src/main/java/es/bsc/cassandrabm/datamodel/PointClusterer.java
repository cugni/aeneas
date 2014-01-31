/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.datamodel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import java.util.List;

/**
 *
 * @author cesare
 */
public class PointClusterer extends TypeClusterer<PointType> {

    private double xmul, ymul, zmul, xmin, ymin, zmin,ncluster;
   
    /*
     * This class is not multithread. The method setGrouping should be called
     * only on the instantialization of the
     */

    @Override
    public void setGrouping(PointType from, PointType to, Integer intervals) {
        ncluster = Math.pow(intervals,1/3.0);
        xmin = Math.abs(Math.abs(from.getX()) > Math.abs(to.getX()) ? to.getX() : from.getX());
        ymin = Math.abs(Math.abs(from.getY()) > Math.abs(to.getY()) ? to.getY() : from.getY());
        zmin = Math.abs(Math.abs(from.getZ()) > Math.abs(to.getZ()) ? to.getZ() : from.getZ());
        xmul = scale(from.getX(), to.getX());
        ymul = scale(from.getY(), to.getY());
        zmul = scale(from.getZ(), to.getZ());


    }

    private double scale(double from, double to) {
        if (from > to) {
            return ncluster/ Math.abs(from / to);
        } else {
            return ncluster/Math.abs(to / from);
        }
    }

    @Override
    public PointType getGroup(PointType value) {
        PointType res = new PointType();

        res.setX(Math.floor(value.getX() / xmin * xmul));
        res.setY(Math.floor(value.getY() / ymin * ymul));
        res.setZ(Math.floor(value.getZ() / zmin * zmul));
        return res;
    }

    @Override
    public List<PointType> getGroupsInterval(PointType from, PointType to) {
        PointType gf = getGroup(from);
        PointType gt = getGroup(to);
        int x = (int) Math.round(gt.getX()- gf.getX());
        int y = (int) Math.round(gt.getY() - gf.getY());
        int z = (int) Math.round(gt.getZ() - gf.getZ());
        int xadde = x >= 0 ? 1 : -1;
        int yadde = y >= 0 ? 1 : -1;
        int zadde = z >= 0 ? 1 : -1;
        Builder<PointType> l = ImmutableList.builder();
        for (int ix = 0; ix <= x*xadde; ix ++) {
            for (int iy = 0; iy <= y*yadde; iy ++) {
                for (int iz = 0; iz <= z*zadde; iz ++) {
                    PointType p = new PointType();
                    p.setX(gf.getX() + ix*xadde);
                    p.setY(gf.getY() + iy*yadde);
                    p.setZ(gf.getZ() + iz*zadde);
                    l.add(p);
                }
            }
        }
        return l.build();
    }

    @Override
    public PointType parseFromString(String s) {
        return PointType.fromString(s);
    }

  
}
