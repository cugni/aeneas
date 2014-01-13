/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.codegenerator.query;

import com.google.common.base.CaseFormat;
import com.sun.codemodel.*;
import es.bsc.aeneas.cassandra.mapping.Ks;
import es.bsc.aeneas.model.gen.Model;
import es.bsc.aeneas.model.gen.QueryImplType;
import es.bsc.aeneas.model.gen.Type;
import es.bsc.aeneas.core.model.util.GenUtils;
import me.prettyprint.cassandra.serializers.DynamicCompositeSerializer;
import me.prettyprint.cassandra.service.template.ColumnFamilyResult;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.*;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.SliceQuery;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import static com.google.common.base.Preconditions.checkArgument;


/**
 * Query Interface generator This class generates the Query Implementation It's
 * made by four levels of nested classes QIGenerator ->QiQuery : a complex
 * interrogation ->QiRow : a request to a column family ->QiEachRow : an
 * selection of columns from a column family
 *
 * @author cesare
 */
public class QIGenerator {

    final JClass refThriftColumnFamilyTemplate;
    final JClass refQueryResult;
    final JClass refColumnSlice;
    final JClass refHColumn;
    final JClass refRows;
    final JClass refRow;
    final JClass refQueryNotImplementedException;
     final Ks k;
   
    public enum CassandraAPI {

        TEMPLATE,
        SLICE_RANGE,
        MULTIGET_SLICE,
        RANGE_SLICE,
        INDEX_SLICE
    }

    public static JType getJType(JCodeModel jmodel, Type type) {
        return jmodel.ref(GenUtils.getClass(type));
    }
    final JClass refHFactory;
    final JClass refKeyspace;
    final JClass refSliceQuery;
    final JClass refColumnFamilyTemplate;
    final JDefinedClass qiClass;
    final JCodeModel jmodel;
  
    final Model qiModel;
    final String className;
    final JDefinedClass jclasse;
    final JClass refComposite;
    final JClass refDynamicComposite;
    final JClass refDynamicCompositeSerializer;
    final JClass refColumnFamilyResult;
    final JClass refHashMap;
    final JClass refUnsupportedEx;
    static final Logger log = Logger.getLogger(QIGenerator.class.getName());

    public QIGenerator(JDefinedClass qiClass, URL QueryImplementation) {
        try {

            this.qiClass = qiClass;
            jmodel = new JCodeModel();
            //TODO modify it, to make it configurable
            JPackage pk = jmodel._package("es.bsc.aeneas.workloader.queryImpl");
            //needed classes
            refHFactory = jmodel.ref(HFactory.class);
            refKeyspace = jmodel.ref(Keyspace.class);
            refSliceQuery = jmodel.ref(SliceQuery.class);
            refColumnFamilyTemplate = jmodel.ref(ColumnFamilyTemplate.class);
            refComposite = jmodel.ref(Composite.class);
            refColumnFamilyResult = jmodel.ref(ColumnFamilyResult.class);
            refDynamicComposite = jmodel.ref(DynamicComposite.class);
            refDynamicCompositeSerializer = jmodel.ref(DynamicCompositeSerializer.class);
            refHashMap = jmodel.ref(HashMap.class);
            refQueryResult = jmodel.ref(QueryResult.class);
            refColumnSlice = jmodel.ref(ColumnSlice.class);
            refHColumn = jmodel.ref(HColumn.class);
            refRows = jmodel.ref(Rows.class);
            refRow = jmodel.ref(Row.class);
          
            refUnsupportedEx = jmodel.ref(UnsupportedOperationException.class);
            refQueryNotImplementedException =jmodel.ref(QueryNotImplementedException.class);
            refThriftColumnFamilyTemplate = jmodel.ref(ThriftColumnFamilyTemplate.class);
            qiModel = GenUtils.getQueryImplementation(QueryImplementation);
            Matcher matcher = CodeGenerator.patternQi.matcher(QueryImplementation.toString());
            checkArgument(matcher.find());
            String expName = matcher.group(1);
            className = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL,
                    expName.replaceAll("[^A-z0-9]", "_").toUpperCase());
            log.log(Level.INFO, "Creating the class {0}", expName);
            jclasse = pk._class(className)._implements(qiClass)._extends(AbstractQueryImpl.class);

            URL cassandraModel = GenUtils.getCassandraModelFile(expName);
//            checkArgument(cassandraModel.exists(), "Cassandra model file not found {0}", expName);
             k = GenUtils.getCassandraModel(cassandraModel);
            

            jclasse.constructor(JMod.PUBLIC).body().invoke("super").arg(k.name);



            /*
             * JMethod constructor = jclasse.constructor(JMod.PUBLIC); JVar map
             * = constructor.param(PathMatchMap.class, "map"); JVar
             * keyspace=constructor.param(Keyspace.class,"keyspace"); JBlock
             * cbody = constructor.body(); cbody.
             *
             */
            JDocComment javadoc = jclasse.javadoc();

            javadoc.add("Query Implementation of " + className + " created on "
                    + DateFormat.getDateInstance().format(Calendar.getInstance().getTime()));

            for (QueryImplType q : qiModel.getQuery()) {
                new QiQuery(q, this);
            }
        } catch (JClassAlreadyExistsException ex) {
            Logger.getLogger(QIGenerator.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalArgumentException(ex);
        }
    }

    public void generated(File whereGenerate) throws IOException {
        if (!whereGenerate.exists()) {
            whereGenerate.mkdirs();
        } else {
            whereGenerate.delete();
            whereGenerate.mkdir();
        }
        log.log(Level.INFO, "Writing the file {0}", whereGenerate.getAbsolutePath() + className);
        jmodel.build(whereGenerate);
    }
}//QiImp
