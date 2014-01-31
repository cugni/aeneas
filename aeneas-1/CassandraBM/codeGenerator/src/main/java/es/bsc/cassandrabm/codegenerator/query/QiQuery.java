/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.codegenerator.query;

import com.sun.codemodel.*;
import es.bsc.cassandrabm.codegenerator.query.QIVars.QIVar;
import es.bsc.cassandrabm.model.gen.GetRowType;
import es.bsc.cassandrabm.model.gen.QueryImplType;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
 

/**
 *
 * @author cesare
 */
class QiQuery {

   

    final JBlock body;
    final QIGenerator qgen;
    private static final Logger log=Logger.getLogger(QiQuery.class.getName());
    final QIVars qivars;
    public final JVar map;
    public final String name;
    QiQuery(QueryImplType q, QIGenerator qgen) {
        this.qgen = qgen;
        name=q.getName();
        JDefinedClass jclasse = qgen.jclasse;
        JMethod met = jclasse.method(JMod.PUBLIC,
                java.util.Map.class,
                q.getName());
        qivars=new QIVars(qgen.jmodel,met,q.getVar());
        log.log(Level.INFO,"Writing the query {0}",q.getName());
        JDocComment metDoc = met.javadoc();
        met.annotate(Override.class);
        //   metDoc("Returns values in the map");
        body = met.body();
        if (q.getNotImplemented()!= null) {
            body._throw(JExpr._new(qgen.refQueryNotImplementedException).arg("Unsupported query"));            
            map=null;
            return;
        }
        map = body.decl(qgen.refHashMap, "map").init(JExpr._new(qgen.refHashMap));
        for (GetRowType row : q.getGetRow()) {
            new QiRow(row, this);
        }
        for(QIVar returns:qivars.getOUTVars()){
            body.add(map.invoke("put").arg(returns.getName())
                    .arg(returns.getJvar()));
        }
        body._return(map);
    }
    private HashMap<String, Integer> names = new HashMap(10);

    String uniqueName(String template) {
        Integer count = names.get(template);
        if (count == null) {
            names.put(template, 1);
            return template + 1;
        } else {
            count++;
            names.put(template, count);
            return template + count;

        }
    }
}//QiQuery    

