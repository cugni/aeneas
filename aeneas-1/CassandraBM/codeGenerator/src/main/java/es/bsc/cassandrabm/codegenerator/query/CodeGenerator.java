/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.codegenerator.query;

import com.google.common.base.CaseFormat;
import com.sun.codemodel.*;
import es.bsc.cassandrabm.codegenerator.query.annotations.Input;
import es.bsc.cassandrabm.codegenerator.query.annotations.InputVars;
import es.bsc.cassandrabm.codegenerator.query.annotations.ReturnVars;
import es.bsc.cassandrabm.codegenerator.query.annotations.Var;
import es.bsc.cassandrabm.model.gen.ModelType;
import es.bsc.cassandrabm.model.gen.NamedType;
import es.bsc.cassandrabm.model.gen.QueryType;
import es.bsc.cassandrabm.model.util.GenUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

/**
 *
 * @author cesare
 */
public class CodeGenerator {

    private final File outputDir;//new File("target/generated-sources/cbm/");
    private final File confDir;
    public final static Pattern patternQi = Pattern.compile("^(.+)(\\.qi\\.xml)$");

    public CodeGenerator() {
        outputDir = new File("../workloader/src/main/java/");//new File("target/generated-sources/cbm/");
        confDir = new File(".");
    }

    public CodeGenerator(File confDir, File outputDir) {
        this.outputDir = outputDir;
        this.confDir = confDir;
    }

    public CodeGenerator(File confDir) {
        this.outputDir = new File("../workloader/src/main/java/");
        this.confDir = confDir;
    }

    public static void main(String[] args) {

        CodeGenerator cd;
        if (args.length == 0) {
            File confFile = new File(args[0]);
            if (!confFile.exists()) {
                System.out.printf("configuration folder %s not found\n", confFile.getAbsolutePath());
                return;
            } else {
                if (args.length >= 1) {
                    File outDir = new File(args[1]);
                    cd = new CodeGenerator(confFile, outDir);
                } else {
                    cd = new CodeGenerator(confFile);
                }
            }

        } else {
            cd = new CodeGenerator();
        }
        cd.generate();
        System.out.printf("queries generated\n");
    }

    public void generate() {


        ModelType mt = GenUtils.getQueryModel();
        JCodeModel jCodeModel = new JCodeModel();
        //TODO check here to insert a configurable package
        JPackage pk = jCodeModel._package("es.bsc.cassandrabm.queryImpl");
        JDefinedClass cl;
        try {
            cl = pk._interface("QueryInterfaceImpl")._extends(QueryInterface.class);
        } catch (JClassAlreadyExistsException ex) {
            throw new IllegalArgumentException(ex);
        }
        JDocComment javadoc = cl.javadoc();

        javadoc.add(
                "Query interface of queryModel.xml created on the "
                + DateFormat.getDateInstance().format(Calendar.getInstance().getTime()));
        for (QueryType q : mt.getQueries()) {
            JMethod met = cl.method(JMod.PUBLIC,
                    java.util.Map.class,
                    q.getName());
            JDocComment metDoc = met.javadoc();
            JAnnotationUse annotate = met.annotate(ReturnVars.class);
            JAnnotationArrayMember paramArray = annotate.paramArray("value");

            //   metDoc("Returns values in the map");
            for (NamedType nt : q.getReturnType()) {
                JAnnotationUse annotate1 = paramArray.annotate(Var.class) //
                        .param("name", nt.getName()) //
                        .param("returnType", GenUtils.getClass(nt))//
                        .param("collectionType", nt.getCollectionType());//

            }

            JAnnotationArrayMember inann = met.annotate(InputVars.class).paramArray("value");

            for (QueryType.Input i : q.getInput()) {
                JAnnotationUse param = inann.annotate(Input.class) //
                        .param("name", i.getName()) //name
                        .param("interval", i.isInterval()).param("type", GenUtils.getClass(i.getStandardType()));
                if (i.getInputRange() != null) {

                    param.param("from", i.getInputRange().getFrom())//
                            .param("to", i.getInputRange().getTo());
                }

                if (i.isInterval()) {
                    String name = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, i.getName());
                    metDoc.addParam("from" + name);
                    met.param(GenUtils.getClass(i.getStandardType()), "from" + name);
                    metDoc.addParam("to" + name);
                    met.param(GenUtils.getClass(i.getStandardType()), "to" + name);
                } else {
                    metDoc.addParam(i.getName());
                    met.param(GenUtils.getClass(i.getStandardType()), i.getName());
                }

            }


        }

        if (!outputDir.exists()) {
            outputDir.mkdirs();
        } else {
            outputDir.delete();
            outputDir.mkdir();
        }
        try {
            jCodeModel.build(outputDir);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Impossible to write in the outputdir", ex);
        }
        File[] listFiles = confDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return patternQi.matcher(pathname.getName()).find();
                //      return pathname.getName().matches("^.+(\\.qi\\.xml)$");
            }
        });
        for (File qi : listFiles) {

            QIGenerator qiGenerator;
            try {
                qiGenerator = new QIGenerator(cl, qi.toURI().toURL());


                qiGenerator.generated(outputDir);
            } catch (MalformedURLException ex) {
                throw new IllegalArgumentException("Impossible to open the filer", ex);
            } catch (IOException ex) {
                throw new IllegalArgumentException("Impossible to write in the outputdir", ex);
            }
        }

    }
}
