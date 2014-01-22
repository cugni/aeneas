/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.cassandra.mapping;

import es.bsc.aeneas.core.model.gen.CassandraMatchType;
import es.bsc.aeneas.core.model.gen.ColumnFamilyType;
import es.bsc.aeneas.core.model.gen.KeyspaceType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ccugnasc
 */
class Reorder {
    KeyspaceType keyspace;
    ColumnFamilyType columnFamily;
    List<CassandraMatchType> matches = new ArrayList();
}
