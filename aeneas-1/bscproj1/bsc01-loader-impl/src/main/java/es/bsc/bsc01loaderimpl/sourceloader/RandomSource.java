/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.bsc01loaderimpl.sourceloader;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Gauge;
import es.bsc.cassandrabm.commons.CUtils;
import es.bsc.cassandrabm.loader.DBSetter;
import es.bsc.cassandrabm.loader.SourceReader;
import es.bsc.cassandrabm.model.marshalling.BoxType;
import es.bsc.cassandrabm.model.marshalling.PointType;
import java.io.InputStreamReader;

/**
 *
 * @author ccugnasc
 */
public class RandomSource extends SourceReader {

    private final int NUMBER_OF_ATOMS = CUtils.getInt("numberofatoms", 99637);
    private final int NUMBER_OF_FRAMES = CUtils.getInt("numberofframes", 1001);
    private DBSetter db;
    //not good, but enought
    private volatile int lread = 0;
    public RandomSource() {

        Metrics.newGauge(RandomSource.class, "LineRead", new Gauge<Integer>() {
            @Override
            public Integer value() {
                return lread;
            }
        });
    }

    @Override
    public void open(InputStreamReader sr) {
    }

    @Override
    public void setDBSetter(DBSetter db) {
        this.db = db;
    }

    @Override
    public int lineRead() {
        return lread;
    }

    @Override
    public Boolean call() throws Exception {
        for (lread = 0; lread < NUMBER_OF_FRAMES; lread++) {


            db.put(NUMBER_OF_ATOMS, lread, "natoms");
            db.put(1, lread, "step");
            db.put(System.currentTimeMillis(), lread, "time");
            db.put(1, lread, "prec");
            //box


            BoxType b = new BoxType();
            for (int i = 0; i < 3; i++) {

                PointType p = new PointType();
                p.setX(CUtils.random().nextDouble());
                p.setY(CUtils.random().nextDouble());
                p.setZ(CUtils.random().nextDouble());
                b.getPoints().add(p);
            }
            db.put(b, lread, "box");
            //points


            for (int i = 0; i < NUMBER_OF_ATOMS; i++) {
                PointType p = new PointType();
                p.setX(CUtils.random().nextDouble());
                p.setY(CUtils.random().nextDouble());
                p.setZ(CUtils.random().nextDouble());

                db.put(p, lread, "points", i);
            }
        }
        return true;
    }
}
