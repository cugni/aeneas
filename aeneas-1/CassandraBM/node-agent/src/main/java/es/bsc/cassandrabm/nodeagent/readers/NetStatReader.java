/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.nodeagent.readers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.io.LineReader;
import es.bsc.cassandrabm.nodeagent.Metric;
import es.bsc.cassandrabm.nodeagent.MetricsReader;
import org.apache.commons.configuration.Configuration;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import javax.inject.Inject;

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
public class NetStatReader implements MetricsReader {

    private static File netstat = new File("/proc/net/dev");
    private static final Pattern dline =
            //rx                                                   //tx
            //                  device_name        -bytes  packets errs  drops fifo   frame  com   mult   -bytes  packets errs  drops fifo   frame  com   mult
            Pattern.compile("^ *([A-Za-z]+[0-9]*):\\D*(\\d+)\\D+(\\d+)\\D+(\\d+)\\D+(\\d+)\\D+\\d+\\D+\\d+\\D+\\d+\\D+\\d+\\D+(\\d+)\\D+(\\d+)\\D+(\\d+)\\D+(\\d+)\\D+\\d+\\D+\\d+\\D+\\d+\\D+\\d+.*");
    private static final Logger LOG = Logger.getLogger(NetStatReader.class.getName());
    @Inject
    private Configuration conf;
    private String device_regexp = "eth0";

    /**
     * It creates a wrapper to obtain statistics about a network interface by
     * reading information from <i>/proc/net/dev</i>
     *
     * @param interfaceRegexp a string with a regexp to match with the device to
     * control <i>(e.g. "sd[ab]")</i>
     *
     *
     */
    public NetStatReader(String interfaceRegexp) {
        LOG.log(Level.INFO, "Sampling interfaces {0}", interfaceRegexp);
        this.device_regexp = interfaceRegexp;

        checkArgument(netstat.exists(), "/proc/diskstats does not exists");
        checkArgument(netstat.canRead(), "/proc/diskstats can not be read");



    }

    /**
     * *
     *
     * @param interfaceRegexp
     * @param processPid to bind the statistics to only one process
     * @deprecated it seems to give the same results when looking for a process
     * instead of the whole system
     */
    @Deprecated
    public NetStatReader(String interfaceRegexp, Integer processPid) {
        LOG.log(Level.INFO, "Sampling interfaces {0} for process {1}",
                new Object[]{interfaceRegexp, processPid});
        this.device_regexp = interfaceRegexp;
        netstat = new File("proc/" + processPid + "/net/dev");
        checkArgument(netstat.exists(), "/proc/diskstats does not exists");
        checkArgument(netstat.canRead(), "/proc/diskstats can not be read");



    }

    public NetStatReader() {
//        checkArgument(diskstats.exists(), "/proc/diskstats does not exists");
//        checkArgument(diskstats.canRead(), "/proc/diskstats can not be read");
    }

    @Override
    public List<Metric> call() throws Exception {

        Builder<Metric> b = ImmutableList.builder();
        LineReader lr = new LineReader(new FileReader(netstat));
        String l;
        while ((l = lr.readLine()) != null) {
            Matcher m = dline.matcher(l);
            if (!m.matches()) {
                continue;
            }
            String iface = m.group(1);
            if (iface.matches(device_regexp)) {
                b.addAll(Metric.Metric(iface)
                        .addMetric("rx.bytes", Long.parseLong(m.group(2)))
                        .addMetric("rx.packets", Long.parseLong(m.group(3)))
                        .addMetric("rx.errs", Long.parseLong(m.group(4)))
                        .addMetric("rx.drop", Long.parseLong(m.group(5)))
                        .addMetric("tx.bytes", Long.parseLong(m.group(6)))
                        .addMetric("tx.packets", Long.parseLong(m.group(7)))
                        .addMetric("tx.errs", Long.parseLong(m.group(8)))
                        .addMetric("tx.drop", Long.parseLong(m.group(9))).getList());

            }
        }
        return b.build();

    }

    @Override
    public void configure() {
        if (conf.containsKey("netstat-reader.interfaceregexp")) {
            this.device_regexp = conf.getString("netstat-reader.interfaceregexp");
            checkArgument(netstat.exists(), "/proc/diskstats does not exists");
            checkArgument(netstat.canRead(), "/proc/diskstats can not be read");
        }
    }
     @Override
    public void setConf(Configuration c) {
        conf = c;
    }

    @Override
    public Configuration getConf() {
        return conf;
    }
}
