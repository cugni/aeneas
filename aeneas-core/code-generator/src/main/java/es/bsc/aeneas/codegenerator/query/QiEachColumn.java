/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.codegenerator.query;

import com.sun.codemodel.*;
import es.bsc.aeneas.codegenerator.query.QIVars.QIVar;
import es.bsc.aeneas.model.gen.*;
import me.prettyprint.cassandra.serializers.BytesArraySerializer;
import me.prettyprint.cassandra.serializers.CompositeSerializer;
import me.prettyprint.cassandra.serializers.DynamicCompositeSerializer;
import me.prettyprint.hector.api.beans.OrderedRows;

import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * @author cesare
 */
public class QiEachColumn {

    private final QiQuery qiquery;
    private final QIGenerator qgen;
    private static final Logger log = Logger.getLogger(QiEachColumn.class.getSimpleName());
    private final JBlock body;
    private final QIVars qivars;
    private final QiRow qirow;
    private final ForeachColumnType forcol;
    private final QIVar returnValueVar;
    private final QIVar keyValue;

    QiEachColumn(ForeachColumnType forcol, QiRow qirow) {
        this.forcol = forcol;
        this.qirow = qirow;
        this.qiquery = qirow.qiquery;
        this.qgen = qirow.qiquery.qgen;
        this.keyValue = qirow.keyValue;
        body = qiquery.body;
        qivars = qiquery.qivars;
        if (forcol.getSetResultColumnValue() != null) {
            returnValueVar = qivars.getVar(forcol.getSetResultColumnValue());
        } else {
            returnValueVar = null;
        }
        if (forcol.getColumnName() != null) {
            ComposekeyType columnName = forcol.getColumnName();
            QIVar cvar = qivars.getSingleColumn(columnName,qirow.cf.columnSort); 
            if (cvar.getCollectionType().equals(CollectionType.SIMPLE_TYPE)) {
                simpleInputQuery(cvar);
            } else if (cvar.getCollectionType().equals(CollectionType.LIST_TYPE)) {
                //retrive all the columns of a given type (column names should be DynamicComposite)
                throw new UnsupportedOperationException("Not yet implemented: list of columns names query");
                //  slideQuery(qivar);
            }


        } else {

            QIVar cfrom;
            QIVar cto;
            if (forcol.getSelectAll() != null) {
                /*
                 * I need a empty byte array if the columns are single type
                 * otherwise a a composite value
                 */
                Type st = forcol.getSelectAll();
                if (st.getStandardType() != null) {
                    cto = qivars.getAllRange(qirow.cf.columnSort, st.getStandardType(), true);
                    cfrom = qivars.getAllRange(qirow.cf.columnSort, st.getStandardType(), false);
                } else {
                    if(st.getCustomType()!=null){
                        cto=qivars.getAllRange(qirow.cf, st);
                        cfrom=cto;
                    }else{
                    cto = qivars.getAllRange(qirow.cf.columnSort, StandardType.BYTES_TYPE, true);
                    cfrom = qivars.getAllRange(qirow.cf.columnSort, StandardType.BYTES_TYPE, false);
                }}
            } else {
                RangeKeyType nameRange = checkNotNull(forcol.getColumnNameRange());
                cfrom = qivars.getSingleColumn(nameRange.getFrom(),qirow.cf.columnSort);
                cto = qivars.getSingleColumn(nameRange.getTo(),qirow.cf.columnSort);
            }
            if (qirow.keysValue != null) {
                //range query
                rangeSlidesQuery(cfrom, cto);
            } else {
                //single row query
                switch (qirow.keyValue.getCollectionType()) {
                    case SIMPLE_TYPE:
                        slideQuery(cfrom, cto);
                        break;
                    case LIST_TYPE:
                        multiGetSlideQuery(cfrom, cto);
                        break;
                    case TABLE_TYPE:
                    case MAP_TYPE:
                        throw new UnsupportedOperationException("Table or Map key type non yet implemented");
                }
            }
        }
    }

    private void slideQuery(QIVar from, QIVar to) {
        body.directStatement("//Slice query statement");
        JClass columnSer;
        JClass columnValue;
        JExpression fromexp;
        JExpression toexp;
        QIVar returnVar = qivars.getVar(forcol.getSetResultColumnValue());
        fromexp = from.getJExp();
        toexp = to.getJExp();
        columnValue = from.getJclass();
        columnSer = from.getSerializer();
        JClass valueType;
        JClass valueTypeSer;
        if (forcol.getSetResultColumnValue() == null) {
            //Value less column.. 
            //TODO implements it
            throw new UnsupportedOperationException("Not already implemented: Valueless column slice query");

        } else {
            QIVar ret = qivars.getVar(forcol.getSetResultColumnValue());
            valueType = ret.getJclass();
            valueTypeSer = ret.getSerializer();
        }

        JClass narrow = qgen.refColumnSlice.narrow(columnValue, valueType);
        JVar sliceResult = qiquery.body.decl(
                narrow, qiquery.uniqueName("queryResult")).init(
                qgen.refHFactory.staticInvoke("createSliceQuery").arg(JExpr.ref("keyspace")) //
                .arg(JExpr._new(qirow.keyValue.getSerializer())) //
                .arg(JExpr._new(columnSer)) //
                .arg(JExpr._new(valueTypeSer)) //
                .invoke("setColumnFamily") //
                .arg(qirow.cf.name).invoke("setKey") //set the row key
                .arg(qirow.keyValue.getJExp()) //row key value
                .invoke("setRange") //set the range
                .arg(fromexp).arg(toexp).arg(JExpr.FALSE) //reverse order
                .arg(JExpr.ref("1000")) //number of results
                .invoke("execute").invoke("get"));
        JClass hcolumn = qgen.refHColumn.narrow(columnValue, valueType);

        JForEach forEach = qiquery.body.forEach(hcolumn, "row", sliceResult.invoke("getColumns"));
        forEach.body().invoke(returnVar.getJExp(), "add").arg(forEach.var().invoke("getValue"));


    }

    private void rangeSlidesQuery(QIVar from, QIVar to) {
        body.directStatement("//range Slice statement");
        QIVar keyFrom = qirow.keysValue[0];
        QIVar keyTo = qirow.keysValue[1];
        JClass columnValue = from.getJclass();
        JExpression fromexp = from.getJExp();
        JExpression toexp= to.getJExp();
        JClass valueType;
        JClass valueTypeSer;
        if (forcol.getSetResultColumnValue() == null) {
            //Value less column.. 
            //TODO implements it
            valueType = qgen.jmodel.BYTE.array();
            valueTypeSer = qgen.jmodel.ref(BytesArraySerializer.class);
            //throw new UnsupportedOperationException("Not already implemented: Valueless column slice query");

        } else {
            QIVar ret = qivars.getVar(forcol.getSetResultColumnValue());
            valueType = ret.getJclass();
            valueTypeSer = ret.getSerializer();
        }

        JClass narrow = qgen.jmodel.ref(OrderedRows.class).narrow(keyFrom.getJclass(),
                columnValue, valueType);
        JVar sliceResult = qiquery.body.decl(
                narrow, qiquery.uniqueName("queryResult")).init(
                qgen.refHFactory.staticInvoke("createRangeSlicesQuery")
                .arg(JExpr.ref("keyspace")) //ref to the field keyspace (super.keyspace)
                .arg(JExpr._new(checkNotNull(keyFrom.getSerializer(), "null key serializer"))) //key serializer...?
                .arg(JExpr._new(checkNotNull(from.serializer, "null column serializer"))) //
                .arg(JExpr._new(checkNotNull(valueTypeSer, "null value serializer")))//
                .invoke("setColumnFamily")//
                .arg(qirow.cf.name) //column family name
                .invoke("setKeys") //set the row key
                .arg(keyFrom.getJExp()).arg(keyTo.getJExp()) //row key value
                .invoke("setRange") //set the range
                .arg(fromexp).arg(toexp).arg(JExpr.FALSE) //reverse order
                .arg(JExpr.direct("1000")) //number of results
                .invoke("execute").invoke("get"));

        JClass hcolumn = qgen.refHColumn.narrow(columnValue, valueType);
        JClass row = qgen.refRow.narrow(keyFrom.getJclass(), columnValue, valueType);

        JForEach for1 = qiquery.body.forEach(row, "rows",
                sliceResult);
        JExpression rowkey = for1.body().decl(keyFrom.getJclass(), "key",
                for1.var().invoke("getKey"));
        if (forcol.getSetResultKey() != null) {
            QIVar var = qivars.getVar(forcol.getSetResultKey());
            checkArgument(var.getCollectionType().equals(CollectionType.LIST_TYPE), "Only List type are supported for result keys");
            for1.body().invoke(var.getJExp(), "add").arg(rowkey);
        }

        JForEach forEach = for1.body().forEach(hcolumn, "row", for1.var().invoke("getColumnSlice").invoke("getColumns"));
        twoLoop(forcol, rowkey, forEach);

    }

    private void simpleInputQuery(QIVar cvar) {
        body.directStatement("//mtempalte statement");
        checkNotNull(returnValueVar, "template queries require a return value var");
        JInvocation columnNameSerializer;
        JClass cnameType;
        JExpression columnValue = cvar.getJExp();

        switch (qirow.cf.columnSort) {
            case DYNAMIC:
                columnValue = JExpr._new(qgen.refDynamicComposite).arg(columnValue);
                columnNameSerializer = JExpr._new(qgen.jmodel._ref(DynamicCompositeSerializer.class));
                cnameType = qgen.refDynamicComposite;
                break;
            case COMPOSITE:
                columnValue = JExpr._new(qgen.refComposite).arg(columnValue);
                columnNameSerializer = JExpr._new(qgen.jmodel._ref(CompositeSerializer.class));
                cnameType = qgen.refComposite;
                break;
            case SINGLE:
            default:



                columnNameSerializer = JExpr._new(
                        qgen.jmodel.ref(qirow.cf.columnNameS.getClass()));
                cnameType = qgen.jmodel.ref(qirow.cf.columnNameType);
        }




        JClass typezed = qgen.refColumnFamilyTemplate.narrow(keyValue.getJclass(), cnameType);
        JClass thritct = qgen.refThriftColumnFamilyTemplate.narrow(keyValue.getJclass(), cnameType);
        JInvocation keySerializer = JExpr._new(keyValue.getSerializer());


        JVar templ = qiquery.body.decl(typezed, qiquery.uniqueName("template"))//
                .init(JExpr._new(thritct) //new ThriftColumnFamilyTemplate
                .arg(JExpr.direct("keyspace")) //ref to the super class keyspace field
                .arg(qirow.cf.name) //column family name
                .arg(keySerializer).arg(columnNameSerializer));
        //differents situation if the keyType is fixed or not

        JVar ret = body.decl(qgen.refColumnFamilyResult.narrow(keyValue.getJclass(), cnameType),//
                qiquery.uniqueName("result")).init(templ.invoke("queryColumns").arg(qirow.keyValue.getJExp()));//
        JInvocation valueSerializer = JExpr._new(
                returnValueVar.getSerializer());
        body.assign(
                returnValueVar.getJvar(), valueSerializer.invoke("fromBytes").arg(
                ret.invoke("getByteArray").arg(columnValue)));
    }

    private void multiGetSlideQuery(QIVar from, QIVar to) {

        body.directStatement("//multi Get Slice statement");
        JClass columnSer=from.getSerializer();
        JClass columnValue=from.getJclass();
        JExpression fromexp=from.getJExp();
        JExpression toexp=to.getJExp();
        columnValue = from.getJclass();
        JClass valueType;
        JClass valueTypeSer;
        if (forcol.getSetResultColumnValue() == null) {
            //Value less column.. 
            //TODO implements it
            throw new UnsupportedOperationException("Not already implemented: Valueless column slice query");

        } else {
            QIVar ret = qivars.getVar(forcol.getSetResultColumnValue());
            valueType = ret.getJclass();
            valueTypeSer = ret.getSerializer();
        }

        JClass narrow = qgen.refRows.narrow(keyValue.getJclass(), from.getJclass(), valueType);
        JVar rows = qiquery.body.decl(
                narrow, qiquery.uniqueName("queryResult")).init(
                qgen.refHFactory.staticInvoke("createMultigetSliceQuery").arg(JExpr.ref("keyspace")) //
                .arg(JExpr._new(keyValue.getSerializer())) //
                .arg(JExpr._new(columnSer)) //
                .arg(JExpr._new(valueTypeSer)) //
                .invoke("setColumnFamily") //
                .arg(qirow.cf.name).invoke("setKeys") //set the row key
                .arg(qirow.keyValue.getJExp()) //row key value
                .invoke("setRange") //set the range
                .arg(fromexp).arg(toexp).arg(JExpr.FALSE) //reverse order
                .arg(JExpr.ref("1000")) //number of results
                .invoke("execute").invoke("get"));
        JClass hcolumn = qgen.refHColumn.narrow(columnValue, valueType);
        JClass row = qgen.refRow.narrow(keyValue.getJclass(), columnValue, valueType);

        JForEach for1 = qiquery.body.forEach(row, "rows",
                rows);
        JExpression rowkey = for1.body().decl(keyValue.getJclass(), "key",
                for1.var().invoke("getKey"));
        if (forcol.getSetResultKey() != null) {
            QIVar var = qivars.getVar(forcol.getSetResultKey());
            checkArgument(var.getCollectionType().equals(CollectionType.LIST_TYPE), "Only List type are supported for result keys");
            for1.body().invoke(var.getJExp(), "add").arg(rowkey);
        }
        JForEach forEach = for1.body().forEach(hcolumn, "row", for1.var().invoke("getColumnSlice").invoke("getColumns"));
        twoLoop(forcol, rowkey, forEach);

    }
    /*
     * This method is thougt for save the result of two dimensional queres (many
     * row for many columns names)
     */

    private void twoLoop(ForeachColumnType forcol, JExpression rowkey, JForEach fore) {
        if (forcol.getSetResultColumnValue() != null) {

            QIVar returnVar = qivars.getVar(forcol.getSetResultColumnValue());
            switch (returnVar.getCollectionType()) {
                case TABLE_TYPE:
                    fore.body().invoke(returnVar.getJExp(), "put").arg(rowkey) //row index
                            .arg(fore.var().invoke("getName"))//point index
                            .arg(fore.var().invoke("getValue")); //value
                    break;
                case LIST_TYPE:
                    fore.body().invoke(returnVar.getJExp(), "add").arg(fore.var().invoke("getValue")); //value
                    break;
                default:
                    throw new IllegalArgumentException("Not valid return value type");
            }
        }
        if (forcol.getSetResultColumnName() != null) {

            QIVar returnVar = qivars.getVar(forcol.getSetResultColumnName());
            switch (returnVar.getCollectionType()) {

                case MAP_TYPE:
                    fore.body().invoke(returnVar.getJExp(), "put").arg(rowkey) //row index
                            .arg(fore.var().invoke("getName"));//point index
                    break;
                case LIST_TYPE:
                    fore.body().invoke(returnVar.getJExp(), "add") //row index
                            //    .arg(fore.var().invoke("getName"))//point index
                            .arg(fore.var().invoke("getName")); //value
                    break;
                case TABLE_TYPE:
                    if (qirow.keysValue[0].getJclass().equals(qgen.refComposite) //FIXME This coud be made better
                            || qirow.keysValue[0].getJclass().equals(qgen.refDynamicComposite)) {
                        fore.body().invoke(returnVar.getJExp(), "put").arg(rowkey.invoke("get").arg(JExpr.ref("0"))) //row index
                                .arg(rowkey.invoke("get").arg(JExpr.ref("1")))//point index
                                .arg(fore.var().invoke("getName")); //value


                        break;
                    }//else throws exception

                default:
                    throw new IllegalArgumentException("Not valid return value type");
            }
        }
        if (forcol.getSetResultKey() != null) {
            throw new UnsupportedOperationException("SetResultKey not yet implemented");
        }




    }
}