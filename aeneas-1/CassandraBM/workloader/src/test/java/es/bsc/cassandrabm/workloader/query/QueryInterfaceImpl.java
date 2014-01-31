
package es.bsc.cassandrabm.workloader.query;

import es.bsc.cassandrabm.codegenerator.query.QueryInterface;
import es.bsc.cassandrabm.codegenerator.query.annotations.*;
import es.bsc.cassandrabm.model.gen.CollectionType;

import java.util.Map;
 
 

/**
 * Query interface of queryModel.xml created on the 17-giu-2012
 * 
 */
@TestInterface
public interface QueryInterfaceImpl
    extends QueryInterface
{


    /**
     * 
     */
    @ReturnVars({
        @Var(name = "natoms", returnType = java.lang.Integer.class, collectionType = CollectionType.SIMPLE_TYPE),
        @Var(name = "prec", returnType = java.lang.Integer.class, collectionType = CollectionType.SIMPLE_TYPE)
    })
    @InputVars({

    })
    public Map getMetadata();

    /**
     * 
     * @param number
     */
    @ReturnVars({
        @Var(name = "step", returnType = java.lang.Integer.class, collectionType = CollectionType.SIMPLE_TYPE),
        @Var(name = "box", returnType = es.bsc.cassandrabm.model.marshalling.BoxType.class, collectionType = CollectionType.SIMPLE_TYPE)
    })
    @InputVars({
        @Input(name = "number", interval = false, type = java.lang.Integer.class, from = "0", to = "1")
    })
    public Map getFrameInfo(Integer number);

    /**
     * 
     * @param fromPoint
     * @param toPoint
     * @param number
     */
    @ReturnVars({
        @Var(name = "step", returnType = java.lang.Integer.class, collectionType = CollectionType.SIMPLE_TYPE),
        @Var(name = "points", returnType = es.bsc.cassandrabm.model.marshalling.PointType.class, collectionType = CollectionType.LIST_TYPE)
    })
    @InputVars({
        @Input(name = "number", interval = false, type = java.lang.Integer.class, from = "0", to = "1"),
        @Input(name = "point", interval = true, type = java.lang.Integer.class, from = "0", to = "19",wideMax="20")
    })
    public Map getFramePoints(Integer number, Integer fromPoint, Integer toPoint);

    /**
     * 
     * @param fromFrame
     * @param fromPoint
     * @param toPoint
     * @param toFrame
     */
    @ReturnVars({
        @Var(name = "points", returnType = es.bsc.cassandrabm.model.marshalling.PointType.class, collectionType = CollectionType.TABLE_TYPE)
    })
    @InputVars({
        @Input(name = "frame", interval = true, type = java.lang.Integer.class, from = "0", to = "1",wideMax="1"),
        @Input(name = "point", interval = true, type = java.lang.Integer.class, from = "0", to = "19",wideMax="20")
    })
    public Map getFrameRangePoints(Integer fromFrame, Integer toFrame, Integer fromPoint, Integer toPoint);

    /**
     * 
     * @param fromPoint
     * @param toPoint
     * @param fromTime
     * @param toTime
     */
    @ReturnVars({
        @Var(name = "points", returnType = es.bsc.cassandrabm.model.marshalling.PointType.class, collectionType = CollectionType.TABLE_TYPE)
    })
    @InputVars({
        @Input(name = "time", interval = true, type = java.lang.Long.class, from = "4611686018427387904", to = "4611686018427387904",wideMax="1",step="1"),
        @Input(name = "point", interval = true, type = java.lang.Integer.class, from = "0", to = "19",wideMax="20")
    })
    public Map getFramePointsByTime(Long fromTime, Long toTime, Integer fromPoint, Integer toPoint);

    /**
     * 
     * @param z
     * @param y
     * @param x
     */
    @ReturnVars({
        @Var(name = "points", returnType = es.bsc.cassandrabm.model.marshalling.PointType.class, 
            collectionType = CollectionType.TABLE_TYPE)
    })
    @InputVars({
//        @Input(name = "x", interval = false, type = java.lang.Double.class, from = "7.976000", to = "2.726000",step="0.001"),
//        @Input(name = "y", interval = false, type = java.lang.Double.class, from = "11.367000", to = "3.682000",step="0.001"),
//        @Input(name = "z", interval = false, type = java.lang.Double.class, from = "8.672000", to = "0.558000",step="0.001"),
         @Input(name = "number", interval = false, type = java.lang.Integer.class, from = "0", to = "1"),
        @Input(name = "point", interval = false, type = java.lang.Integer.class, from = "0", to = "13",wideMax="20"),
        @Input(name = "delta", interval = false, type = java.lang.Double.class, from = "0.001", to = "0.01" ,step="0.001")
    })
    public Map getAtoms(Integer number ,Integer point,Double delta);

}
