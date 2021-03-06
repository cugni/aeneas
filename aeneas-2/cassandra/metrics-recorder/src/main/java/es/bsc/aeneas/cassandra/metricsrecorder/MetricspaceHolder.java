/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.cassandra.metricsrecorder;
 

import es.bsc.aeneas.core.model.gen.CrudType;
import es.bsc.aeneas.core.rosetta.Rosetta;
 import java.util.logging.Logger;
import javax.inject.Inject;

/**
 * TODO This class has to be completely redesigned 
 * @author ccugnasc
 */

public class MetricspaceHolder {

    private static final Logger log = Logger.getLogger(MetricspaceHolder.class.getName());
    private final Rosetta db;

    @Inject
    public MetricspaceHolder(Rosetta db) {

        this.db = db;
    }

    MetricspaceHolder() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public MetricContext getMetricContext(String context, String testname, String testingnode) {
        return new MetricContext(context, testname, testingnode);

    }

    public class MetricContext {

        private final String context;
        private final String testname;
        private final String location;

        private MetricContext(String context, String testname, String location) {
            this.context = context;
            this.testname = testname;
            this.location = location;
        }

        public MetricHolder storeMetric(long time) {
            return new MetricHolder(time);
        }

        public void stop() {
                 db.queryAll(CrudType.CREATE_OR_UPDATE,
                         System.currentTimeMillis(),context,testname,location,"stop");
        
        }

        public void start() {
                   db.queryAll(CrudType.CREATE_OR_UPDATE,
                           System.currentTimeMillis(),context,testname,location,"start");
        }

        public class MetricHolder {

            private final long time;

            private MetricHolder(long time) {
                this.time = time;
            }

            public MetricHolder addMetric(String propertyname, Object value) {
                db.queryAll(CrudType.CREATE_OR_UPDATE,
                                          value,context,testname,location,System.currentTimeMillis(),propertyname);
                return this;
            }
 

            public MetricGroup addGroup(String group) {
                return new MetricGroup(group);
            }

            public class MetricGroup {

                private final String group;

                MetricGroup(String group) {
                    this.group = group;
                }              

                public MetricGroup addMetric(String propertyname, Object value) {
                      db.queryAll(CrudType.CREATE_OR_UPDATE,
                              context,testname,location,System.currentTimeMillis(),group+"."+propertyname);                   
                   return this;
                }
            }
        }
    }
}
