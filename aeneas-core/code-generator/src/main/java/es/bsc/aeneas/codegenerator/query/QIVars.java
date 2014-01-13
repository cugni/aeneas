/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.codegenerator.query;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.sun.codemodel.*;
import es.bsc.aeneas.cassandra.mapping.CF;
import es.bsc.aeneas.cassandra.mapping.ColumnSort;
import es.bsc.aeneas.cassandra.mapping.KeyType;
import es.bsc.aeneas.model.gen.*;
import es.bsc.aeneas.core.model.util.GenUtils;
import es.bsc.aeneas.cassandra.serializers.Serializers;
import me.prettyprint.cassandra.serializers.BytesArraySerializer;
import me.prettyprint.cassandra.serializers.CompositeSerializer;
import me.prettyprint.cassandra.serializers.DynamicCompositeSerializer;
import me.prettyprint.hector.api.beans.AbstractComposite;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.beans.DynamicComposite;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

 

/**
 *
 * @author cesare
 */
public class QIVars {
    private final static Logger log=Logger.getLogger(QIVars.class.getName());

    final HashMap<String, QIVar> vars = new HashMap<String, QIVar>(5);
    final HashMap<String, QIVar> varsIN = new HashMap<String, QIVar>(5);
    final HashMap<String, QIVar> varsOUT = new HashMap<String, QIVar>(5);
    final HashMap<String, QIVar> varsTMP = new HashMap<String, QIVar>(5);
    final HashMap<String, VarType> types = new HashMap<String, VarType>(5);
    final QIVars me = this;
    private final JCodeModel jmodel;

    public QIVars(JCodeModel jmodel, JMethod met, List<VarType> varts) {

        this.jmodel = jmodel;
        for (VarType nt : varts) {
            JClass ref = jmodel.ref(GenUtils.getClass(nt));
            JVar decl;
            if (nt.getScope().equals(ScopeType.INPUT_VAR)) {

                decl = met.param(ref, nt.getName());
            } else {
                switch (nt.getCollectionType()) {
                    case SIMPLE_TYPE:
                        decl = met.body().decl(ref, nt.getName());
                        break;
                    case LIST_TYPE:
                        if (nt.getScope().equals(ScopeType.TMP_VAR)) {
                            decl = met.body().decl(jmodel.ref(List.class).narrow(ref),
                                    nt.getName()).init(JExpr._new(jmodel.ref(MetricsList.class)).arg(met.name()).arg(nt.getName()));

                        } else {
                            decl = met.body().decl(jmodel.ref(List.class).narrow(ref), nt.getName()).init(JExpr._new(jmodel.ref(ArrayList.class)));
                        }
                        break;
                    case MAP_TYPE:
                        decl = met.body().decl(jmodel.ref(Map.class).narrow(jmodel.ref(String.class)).narrow(ref), nt.getName()).init(JExpr._new(jmodel.ref(HashMap.class)));
                        break;
                    default:

                        //case TABLE_TYPE:

                        decl = met.body().decl(jmodel.ref(Table.class).narrow(jmodel.ref(Object.class), jmodel.ref(Object.class), ref), nt.getName()).init(jmodel.ref(HashBasedTable.class).staticInvoke("create"));
                }
            }

            JClass serializer = jmodel.ref(Serializers.getSerializer(nt).getClass());
            JClass jcla = jmodel.ref(GenUtils.getClass(nt));
            QIVar var = new QIVar(decl, nt, serializer, jcla);
            types.put(nt.getName(), nt);
            switch (nt.getScope()) {
                case INPUT_VAR:
                    varsIN.put(nt.getName(), var);
                    break;
                case OUTPUT_VAR:
                    varsOUT.put(nt.getName(), var);
                    break;
                case TMP_VAR:
                    varsTMP.put(nt.getName(), var);
                    break;

            }

        }
        vars.putAll(varsIN);
        vars.putAll(varsOUT);
        vars.putAll(varsTMP);
    }

    public JClass getSerializer(String name) {
        return checkNotNull(vars.get(name)).serializer;
    }

    public QIVar getINVar(String name) {
        return checkNotNull(varsIN.get(name));
    }

    public QIVar getTMPVar(String name) {
        return checkNotNull(varsTMP.get(name.trim()));
    }

    public QIVar getOUTVar(String name) {
        return checkNotNull(varsOUT.get(name.trim()));
    }

    public QIVar getVar(String name) {
        return checkNotNull(vars.get(name.trim()), "Not found {0}", name);
    }

    public Collection<QIVar> getOUTVars() {
        return varsOUT.values();
    }

    public VarType getType(String name) {
        return checkNotNull(types.get(name.trim()));
    }

    /*
     * This is for a getAll when the column ordered is not composite
     */
    QIVar getAllRange(CF cf, Type all) {



        checkArgument(!cf.columnSort.equals(ColumnSort.DYNAMIC),
                "This method is only for not composite columns");

        JClass jclass = jmodel.ref(GenUtils.getClass(all));
        JClass serializer = jmodel.ref(Serializers.getSerializer(all).getClass());
        return new QIVar(serializer, JExpr._null(),
                null, ScopeType.TMP_VAR,
                jclass, checkNotNull(all).getCollectionType(),
                "range" + cf.comparator.getTypeName());
    }

    /*
     * Dynamic range queries available on for standard type @arg from indicates
     * if the QIVar generated is a from or a to (a MIN_VAL or a MAX_VAL)
     */
    QIVar getAllRange(ColumnSort columnSort, StandardType st, boolean from) {
        Class<? extends AbstractComposite> cla;
        switch (columnSort) {
            case DYNAMIC:
                cla = DynamicComposite.class;
                break;
            case COMPOSITE:
                cla = Composite.class;
                break;
            default:
                //simple type
                cla = null;

        }
        return new QIVar(checkNotNull(st), from, cla);
    }
    //QIVars

    private QIVar createQIVar(JVar jvar, VarType type,
            JClass serializer, JClass jclass) {

        return new QIVar(serializer, jvar, jvar, type.getScope(), jclass,
                type.getCollectionType(), type.getName());
    }

    QIVar getEmptyByteBuffer() {
        return new EmptyByteBuffer();
    }

    QIVar getKeyType(ComposekeyType k, KeyType keyType) {
        return new QIKeyVar(k, keyType);
    }

    QIVar getSingleColumn(ComposekeyType columnName,ColumnSort sorter) {
       return new QIColumnVar(columnName,sorter);
    }

    private class EmptyByteBuffer extends QIVar {

        EmptyByteBuffer() {
            super(JExpr._new(jmodel.BYTE.array()).arg(JExpr.direct("0")), null, ScopeType.TMP_VAR);
            serializer = jmodel.ref(BytesArraySerializer.class);
        }
    }
  public class QIColumnVar extends QIKeyVar {

        private QIColumnVar(ComposekeyType k, ColumnSort sorter) {
             scope = null;
            jvar = null;
            if (k.getSimple() != null) {
                QkeyType simple = k.getSimple();
                name = simple.getVarRef();
                //jclass=getVar(simple.getVarRef()).jclass; //TODO correct here
                jexp = getSingle(simple);
            } else {
                //compose reference
                compose = true;
                name = "composite";
                switch (sorter) {
                    case COMPOSITE:
                        jclass = me.jmodel.ref(Composite.class);
                        serializer = jmodel.ref(CompositeSerializer.class);

                        break;
                    case DYNAMIC:
                        jclass = me.jmodel.ref(DynamicComposite.class);
                        serializer = jmodel.ref(DynamicCompositeSerializer.class);
                        break;
                    default:
                        throw new IllegalArgumentException("inconsistent configuration");
                }
                jclass = me.jmodel.ref(DynamicComposite.class);
                serializer = jmodel.ref(DynamicCompositeSerializer.class);
                jexp = JExpr._new(jclass);
                for (QkeyType com : k.getComposed()) {
                    jexp.invoke("add").arg(getSingles(com));
                }

            }

        }
      
  }
    public class QIKeyVar extends QIVar {
        protected  QIKeyVar(){
            
        }
        public QIKeyVar(ComposekeyType k, KeyType keyType) {
            scope = ScopeType.INPUT_VAR;
            jvar = null;
            if (k.getSimple() != null) {
                QkeyType simple = k.getSimple();
                name = simple.getVarRef();
                //jclass=getVar(simple.getVarRef()).jclass; //TODO correct here
                jexp = getSingle(simple);
            } else {
                //compose reference
                compose = true;
                name = "composite";
                switch (keyType) {
                    case COMPOSITE_KEY:
                        jclass = jmodel.ref(Composite.class);
                        serializer = jmodel.ref(CompositeSerializer.class);

                        break;
                    case DYNAMIC_KEY:
                        jclass = jmodel.ref(DynamicComposite.class);
                        serializer = jmodel.ref(DynamicCompositeSerializer.class);
                        break;
                    default:
                        throw new IllegalArgumentException("inconsistent configuration");
                }
                jexp = JExpr._new(jclass);
                for (QkeyType com : k.getComposed()) {
                    jexp.invoke("add").arg(getSingles(com));
                }

            }

        }
        /*
         * Returns the declaration of a single elements
         */

        protected JExpression getSingle(QkeyType simple) {
            JExpression exp;
            if (simple.getFixed() != null) {
                compose = false;
                serializer = jmodel.ref(Serializers.getSerializer(simple.getFixed().getType()).getClass());
                jclass = jmodel.ref(GenUtils.getClass(simple.getFixed().getType()));
                FixedType fixed = simple.getFixed();
                switch (fixed.getType()) {
                    case UTF_8_TYPE:
                    case ASCII_TYPE:
                        exp = JExpr.direct("\"" + fixed.getValue() + "\"");
                        break;
                    default:
                        exp = JExpr.direct(fixed.getValue());
                }

                collectionType = CollectionType.SIMPLE_TYPE;
            } else {


                QIVar qivar = getVar(simple.getVarRef());
                jclass = qivar.jclass;
                serializer = qivar.serializer;
                exp = qivar.getJvar();
                VarType type = me.getType(simple.getVarRef());
                collectionType = type.getCollectionType();

            }
            return exp;
        }
        /*
         * Returns the declaration of a composite tye
         */

        protected JExpression getSingles(QkeyType simple) {
            JExpression exp;
            if (simple.getFixed() != null) {
                FixedType fixed = simple.getFixed();
                switch (fixed.getType()) {
                    case UTF_8_TYPE:

                    case ASCII_TYPE:
                        exp = JExpr.direct("\"" + fixed.getValue() + "\"");
                        break;
                    default:
                        exp = JExpr.direct(fixed.getValue());
                }
            } else {
                QIVar qivar = getVar(simple.getVarRef());
                exp = qivar.getJvar();
                VarType type = me.getType(simple.getVarRef());

                //checkArgument(type.getCollectionType().equals(CollectionType.SIMPLE_TYPE), "A composite value is creable only with SIMPLE_TYPEs"); //trying to better it

            }
            return exp;
        }
    }

    public class QIVar {

        protected JClass serializer;
        protected JExpression jexp;
        protected JVar jvar;
        protected ScopeType scope;
        protected JClass jclass;
        protected CollectionType collectionType;
        protected boolean compose = false;
        protected String name = "";
        protected boolean getAll = false;

        protected QIVar() {
        }

        protected QIVar(JExpression jexp, JVar jvar, ScopeType scope) {
            this.jexp = jexp;
            this.jvar = jvar;
            this.scope = scope;
        }

        public QIVar(JClass serializer, JExpression jexp, JVar jvar,
                ScopeType scope, JClass jclass, CollectionType collectionType,
                String name) {
            this.serializer = checkNotNull(serializer);
            this.jexp = checkNotNull(jexp);
            this.jvar = jvar;
            this.scope = scope;
            this.jclass = checkNotNull(jclass);
            this.collectionType = collectionType;
            this.name = name;
        }

        public QIVar(JClass serializer, JExpression jexp, JVar jvar,
                ScopeType scope, JClass jclass, CollectionType collectionType) {
            this.serializer = checkNotNull(serializer);
            this.jexp = checkNotNull(jexp);
            this.jvar = jvar;
            this.scope = scope;
            this.jclass = checkNotNull(jclass);
            this.collectionType = collectionType;


        }

        private QIVar(JVar jvar, VarType type, JClass serializer, JClass jclass) {
            this.collectionType = type.getCollectionType();
            jexp = jvar;
            this.serializer = serializer;
            this.jvar = jvar;
            scope = type.getScope();
            this.jclass = jclass;
            compose = false;
            name = type.getName();

        }

        /*
         * Only for the get dynamic column range
         *
         */
        private QIVar(StandardType st, boolean from, Class<? extends AbstractComposite> cla) {
            name = st.name();
            JExpression tmp;
            JClass inner;
            switch (st) {
                case INT_32_TYPE:
                    inner = jmodel.ref(Integer.class);
                    //TODO fix it
                    if (from) {
                        tmp = jmodel.ref(Integer.class).staticRef("MIN_VALUE");

                    } else {
                        tmp = jmodel.ref(Integer.class).staticRef("MAX_VALUE");
                    }
                    break;
                case LONG_TYPE:
                    inner = jmodel.ref(Long.class);

                    if (from) {
                        tmp = inner.staticRef("MIN_VALUE");

                    } else {
                        tmp = inner.staticRef("MAX_VALUE");
                    }
                    break;
                case DOUBLE_TYPE:
                    inner = jmodel.ref(Double.class);
                    if (from) {
                        tmp = inner.staticRef("MIN_VALUE");

                    } else {
                        tmp = inner.staticRef("MAX_VALUE");
                    }
                    break;
                case UTF_8_TYPE:
                    inner = jmodel.ref(String.class);
                    //TODO study here
                    tmp = JExpr.direct("\\not yet implemented.");
                    break;
                case BYTES_TYPE:
                    inner = jmodel.ref(byte[].class);
                    //TODO study here
                    tmp = JExpr.direct("null");
                    break;
                default:
                    throw new UnsupportedOperationException("Type non supported for range queries");



            }
            scope = ScopeType.TMP_VAR;
            jvar = null;
            if (cla != null) {
                jexp = JExpr._new(jmodel.ref(cla)).arg(tmp);

                jclass = jmodel.ref(cla);

                serializer = jmodel.ref(cla);
            } else {
                jexp = tmp;
                jclass = inner;
                try {
                    serializer = jmodel.ref(Serializers.getSerializer(Class.forName(inner.fullName())).getClass());
                } catch (ClassNotFoundException ex) {
                  log.log(Level.SEVERE, "Class not found.. ", ex);
                  throw new IllegalArgumentException(ex);
                }
            }

        }

        public CollectionType getCollectionType() {
            return collectionType;
        }

        public boolean isCompose() {
            return compose;
        }

        public JClass getJclass() {
            return jclass;
        }

        public JVar getJvar() {
            return checkNotNull(jvar, "Invalid Request, unreferenziable QIVar");
        }

        public JExpression getJExp() {
            return jexp;
        }

        public ScopeType getScope() {
            return scope;
        }

        public JClass getSerializer() {
            return serializer;
        }

        public String getName() {
            return name;
        }

        public boolean isGetAll() {
            return getAll;
        }

        public void setGetAll(boolean getAll) {
            this.getAll = getAll;
        }

        public JExpression getRange() {
            if (getAll) {
                return JExpr._null();
            } else {
                return jexp;
            }
        }
    }
}
