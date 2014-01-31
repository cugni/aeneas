package es.bsc.cassandrabm.bsc01workloaderimpl;

import es.bsc.cassandrabm.codegenerator.query.QueryInterface;
import es.bsc.cassandrabm.codegenerator.query.annotations.Input;
import es.bsc.cassandrabm.codegenerator.query.annotations.InputVars;
import es.bsc.cassandrabm.codegenerator.query.annotations.ReturnVars;
import es.bsc.cassandrabm.codegenerator.query.annotations.TestInterface;
import es.bsc.cassandrabm.codegenerator.query.annotations.Var;
import es.bsc.cassandrabm.model.gen.CollectionType;
import java.util.Map;

/**
 * Query interface of queryModel.xml created on the 17-giu-2012
 *
 */
@TestInterface
public interface QueryInterfaceImpl
        extends QueryInterface {

    /**
     *
     *
     * @param toPoint
     * @param toFrame
     */
    @ReturnVars({
        @Var(name = "points", 
            returnType = es.bsc.cassandrabm.model.marshalling.PointType.class,
            collectionType = CollectionType.TABLE_TYPE)
    })
    @InputVars({
        @Input(name = "frame", type = java.lang.Integer.class, from = "0", to = "999")
    })
    public Map getFrameSlice(Integer frame);
    
   
    
    /**
     *
     *
     * @param toPoint
     * @param toFrame
     */
    @ReturnVars({
        @Var(name = "points", returnType = es.bsc.cassandrabm.model.marshalling.PointType.class,
            collectionType = CollectionType.TABLE_TYPE)
    })
    @InputVars({
        @Input(name = "point", type = java.lang.Integer.class, from = "0", to = "8379")
    })
    public Map getAtomSlice(Integer point);
    
    /**
     *
     * @param point
     * @param atomstoget
     * @return
     */
      
}
