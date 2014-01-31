/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.codegenerator.query;

import es.bsc.cassandrabm.codegenerator.query.QIVars.QIVar;
import es.bsc.cassandrabm.model.CF;
import es.bsc.cassandrabm.model.gen.ForeachColumnType;
import es.bsc.cassandrabm.model.gen.GetRowType;
import es.bsc.cassandrabm.model.gen.QueryType;
import es.bsc.cassandrabm.model.gen.RangeKeyType;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * @author cesare
 */
class QiRow {

    final GetRowType row;
    final QIVar keyValue;
    final QIVar[] keysValue;
    final QIGenerator qgen;
    QueryType queryType = null;
    final QIVars qivars;
    final QiQuery qiquery;
    private static final Logger log = Logger.getLogger(QiRow.class.getSimpleName());
    public final CF cf;

    QiRow(GetRowType row, QiQuery qiquery) {
        this.row = row;
        this.qivars = qiquery.qivars;
        this.qgen = qiquery.qgen;
        this.qiquery = qiquery;
        CF found = null;
        for (CF tmp : qgen.k.cfs) {
            if (tmp.name.equals(row.getColumnFamily())) {
                found = tmp;
            }
        }
        cf = checkNotNull(found);
        log.log(Level.INFO, "Writing the  query for the Column Family {0}", row.getColumnFamily());
        //set KeyType
        if (row.getKey() != null) {
            keyValue = qiquery.qivars.getKeyType(row.getKey(), cf.keyType);
            keysValue = null;
        } else if (row.getRangeKey() != null) {
            RangeKeyType rangeKey = row.getRangeKey();
            keysValue = new QIVar[]{qivars.getKeyType(rangeKey.getFrom(), cf.keyType),
                qivars.getKeyType(rangeKey.getTo(), cf.keyType)};
            keyValue = null;

        } else {
            throw new IllegalArgumentException("Illegal value type");
        }
        for (ForeachColumnType forcol : row.getForeachColumn()) {
            new QiEachColumn(forcol, this);
        }
    }
}//end of qiRow    

