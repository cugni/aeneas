/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.nodeagent.readers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.io.LineReader;
import es.bsc.aeneas.nodeagent.Metric;
import es.bsc.aeneas.nodeagent.MetricsReader;
import org.apache.commons.configuration.Configuration;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;

/**
 *
 *
 * It creates a wrapper to obtain statistics about a block devices by reading
 * information from <i>/proc/diskstats </i>
 * For further information read the Linux documentation about
 * {@link Documentation/iostat}
 *
 * @author ccugnasc
 */
public class PagefaultReader implements MetricsReader {

    private static final File diskstats = new File("/proc/vmstat");
    private static final Pattern dline = Pattern.compile("((pgpgin)|(pgpgout)|(pswpin)|(pswpout)) +([0-9]+)");
    private static final int arg_position=1;
    private static final int value_position=6;
    private static final Logger LOG = Logger.getLogger(PagefaultReader.class.getName());
   
    /**
     * It creates a wrapper to obtain statistics about a block devices by
     * reading information from <i>/sys/block/device_name/stat</i>
     *
     * @param deviceregexp a string with a regexp to match with the device to
     * control <i>(e.g. "sd[ab]")</i>
     *
     *
     */
    public PagefaultReader( ) {
        checkArgument(diskstats.exists(), "/proc/vmstat does not exists");
        checkArgument(diskstats.canRead(), "/proc/vmstat can not be read");
    }

  

    @Override
    public List<Metric> call() throws Exception {

        Builder<Metric> b = ImmutableList.builder();
        LineReader lr = new LineReader(new FileReader(diskstats));
        String l;
        while ((l = lr.readLine()) != null) {
            Matcher m = dline.matcher(l);
            if (m.matches()) {
                b.add(new Metric("memory",m.group(arg_position), Long.parseLong(m.group(value_position))));

            }
        }
        return b.build();

    }

    @Override
    public void configure() {
        LOG.info("called the configure method");
//        if (conf.containsKey("deviceregexp")) {
//            this.metric_regexp = conf.getString("deviceregexp");
//            checkArgument(diskstats.exists(), "/proc/diskstats does not exists");
//            checkArgument(diskstats.canRead(), "/proc/diskstats can not be read");
//        }
    }

    @Override
    public void setConf(Configuration c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Configuration getConf() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
