 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.loader;

import es.bsc.aeneas.cassandra.translator.Loader;
import es.bsc.aeneas.cassandra.translator.XMLCassandraSetter;
 import es.bsc.aeneas.core.rosetta.exceptions.UnreachableClusterException;

 import java.io.File;
 import java.io.PrintStream;
 import java.text.SimpleDateFormat;
 import java.util.*;
 import java.util.logging.Level;
 import java.util.logging.Logger;

/***
 *
 * @author cesare Configurable parameters: maxbatchsize Client.java "resultdir"
 * - dir where file reports are saved "filereporting" - true/false GenUtils.java
 * "confdir" - where the framework looks for the XML schemas "referencemodel" -
 * the file name of the Reference Model "querymodel" - the file name of the
 * Query model XMLCassandraSetter.java "batched" - true/false (default true)
 * Indicates if join more request in one bigger "nothreads" -true/false (default
 * false) Indicates if use only one thread for the Source Reader and the
 * Cassandra abstraction layer "queuelength" - Indicates the queue lenght of
 * request between SR and CAL "readconsistency" - Indicates the level of
 * consistency for read requests: values possible: ANY ONE TWO THREE
 * LOCAL_QUORUM EACH_QUORUM QUORUM ALL "writeconsistency" - Indicates the level
 * of consistency for write requests "autoDiscoverHosts" - true/false (defalut
 * true) Indicates if enable or not the Hector's auto discover of the
 * Cassandra's hosts
 *
 */
public class Client {

    private final static Logger log = Logger.getLogger(Client.class.getName());
    private final static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");
    private static PrintStream c;

    public static void main(String[] args) throws InterruptedException, Exception {
        c = System.out;
        c.printf("Cassandra Benchmarking platform - core console\n");
        c.printf("Commands available are:\n"
                + "load-data modelfile.cm.xml sourcefile.txt source.Reader.Class\n"
                + "load-and-test-data modelfile.cm.xml sourcefile.txt source.Reader.Class* \n");
        // + "generate-queries (confDirPath  outputDirPath*)*\n");
        String command;
        boolean done = false;
        Scanner s = new Scanner(System.in);
        while (!done) {
            StringTokenizer tok = new StringTokenizer(s.nextLine());
            if (!tok.hasMoreTokens()) {
                continue;
            }
            command = tok.nextToken();
            if (command.equals("load-data")) {
                String modelf = tok.nextToken();
                String sourcef = tok.nextToken();
                File modelfile = new File(modelf);
                File source = new File(sourcef);
                String sourceFile;
                if (tok.hasMoreTokens()) {
                    sourceFile = tok.nextToken();
                } else {
                    sourceFile = null;
                }
                loadData(modelfile, source, sourceFile);
//            } else if (command.equals("generate-queries")) {
//                generateQueries(tok);
            } else if (command.equals("load-and-test-data")) {
                if (tok.countTokens() < 2) {
                    c.printf("modelfile.cm.xml sourcefile.txt  arguments are required\n");
                    continue;
                }
                String modelf = tok.nextToken();
                String sourcef = tok.nextToken();
                File modelfile = new File(modelf);
                File source = new File(sourcef);
                String sourceFile;
                if (tok.hasMoreTokens()) {
                    sourceFile = tok.nextToken();
                } else {
                    sourceFile = null;
                }
                loadData(modelfile, source, sourceFile);
                loadAndTestData(modelfile, source, sourceFile);
            } else if (command.matches("(quit)|(exit)")) {
                c.printf("bye bye\n");
                Loader.getInstance().bruteShutdown();
                return;
            } else {
                c.printf("comand %s not found\n", command);
            }

        }
    }

    private static void loadData(File modelfile, File source, String sourceClass) throws Exception {
        SourceReader es;
        if (sourceClass != null) {
            try {
                es = (SourceReader) Class.forName(sourceClass).newInstance();
            } catch (InstantiationException ex) {
                c.printf("impossible to load the %s class\n", sourceClass);
                return;
            } catch (IllegalAccessException ex) {
                c.printf("impossible to load the %s class\n", sourceClass);
                return;
            } catch (ClassNotFoundException ex) {
                c.printf("impossible to load the %s class\n", sourceClass);
                return;
            }
        } else {
            Iterator<SourceReader> i = ServiceLoader.load(SourceReader.class).iterator();
            if (i.hasNext()) {
                es = i.next();
                log.log(Level.INFO, "Source Reader loaded by ServiceLoader: {}", es.getClass().toString());

            } else {
                c.printf("loader class not found\n");
                return;
            }
        }
        if (!modelfile.exists()) {
            c.printf("model file %s not found\n", modelfile.getAbsolutePath());
            return;
        } else if (!source.exists()) {
            c.printf("source file %s not found\n", source.getAbsolutePath());
            return;
        }

//        if (getIntProperty.getBoolProperty("filereporting", true)) {
//
//            File resDir = new File(resultDir, "res-"
//                    + sf.format(Calendar.getInstance().getTime()) + "-" + modelfile + "-" + source);
//            if (resDir.exists()) {
//                resDir.delete();
//            }
//            checkArgument(resDir.mkdirs(), "Impossible create the result dir " + resDir.getName());
//            c.printf("Saving reports in the dir %s\n", resDir.getAbsolutePath());
////            CsvReporter.enable(
////                    resDir,
////                    1,//
////                    TimeUnit.SECONDS);//
//        }
        Loader l = Loader.getInstance();


        c.println("Starting loading at " + sf.format(new Date()));
        XMLCassandraSetter set = new XMLCassandraSetter(modelfile.toURI().toURL());

        try {
            set.open(l.clustername, l.clusterlocation);
        } catch (UnreachableClusterException ex) {
            c.printf("impossible to connecto to the cluster %s at %s\n", l.clustername, l.clusterlocation);
            return;
        }

        set.configure();
        es.open(source);
        es.setDBSetter(set);
        if (es.call()) {
            c.println("loading process completed");
        } else {
            c.println("loading process failed");
        }
        c.println("completed at" + sf.format(new Date()));
    }

    private static void loadAndTestData(File modelfile, File source, String sourceClass) throws Exception {
        SourceReader es;
        if (sourceClass != null) {
            try {
                es = (SourceReader) Class.forName(sourceClass).newInstance();
            } catch (InstantiationException ex) {
                c.printf("impossible to instantiate the %s class\n", sourceClass);
                return;
            } catch (IllegalAccessException ex) {
                c.printf("impossible to load the %s class\n", sourceClass);
                return;
            } catch (ClassNotFoundException ex) {
                c.printf("impossible to find  the %s class\n", sourceClass);
                return;
            }
        } else {
            Iterator<SourceReader> i = ServiceLoader.load(SourceReader.class).iterator();
            if (i.hasNext()) {
                es = i.next();
                log.log(Level.INFO, "Source Reader loaded by ServiceLoader: {}", es.getClass().toString());
            } else {
                c.printf("loader class not found\n");
                return;
            }
        }
        if (!modelfile.exists()) {
            c.printf("model file %s not found\n", modelfile.getAbsolutePath());
            return;
        } else if (!source.exists()) {
            c.printf("source file %s not found\n", source.getAbsolutePath());
            return;
        }
        c.println("Starting loading at " + sf.format(new Date()));
        XMLCassandraSetter set = new XMLCassandraSetter(modelfile.toURI().toURL());
        Loader l = Loader.getInstance();
        try {
            set.open(l.clustername, l.clusterlocation);
        } catch (UnreachableClusterException ex) {
            c.printf("impossible to connecto to the cluster %s at %s\n", l.clustername, l.clusterlocation);
            return;
        }
        es.open(source);
        IOTestDB test = new IOTestDB(set);
        es.setDBSetter(test);
        if (es.call()) {
            c.printf("testing process completed with %d insertion."
                    + " Tested %d, failed %d, not tested %d\n", test.totalRows.get(),
                    test.testedRows.get(), test.failedRows.get(), test.untestedRows.get());

        } else {
            c.println("testing process failed");
        }
        c.println("testing completed at" + sf.format(new Date()));
    }
}
