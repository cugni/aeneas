package es.bsc.aeneas.loader;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.configuration.Configuration;

/**
 *       TODO         finish this class integrating it with the common arguments repository
 *
 * User: ccugnasc
 * Date: 7/15/13
 * Time: 3:47 PM

 */
public class CliClient {


    @Inject
    public  Configuration conf;

    @Parameter
    public List<String> parameters=  new ArrayList();

    @Parameter(names={"-src","-source-reader"},description = "The full class name of the source reader implementation. It must extends SourceReader ")
    public String srClassName;

    @Parameter(names={"-db","-dbSetter-class"},description = "The full class name of the database setter implementation. It must extends DBSetter ")
    public String   dbsClassName;


    public static void main(String[] args){
       new CliClient().doMain(args);

    }

    private void doMain(String[] args) {
        JCommander parser = new JCommander(this);


    }
}