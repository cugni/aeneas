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
public class IOstatReader implements MetricsReader {

    private static final File diskstats = new File("/proc/diskstats");
    private static final Pattern dline = Pattern.compile(" *\\d+ + *\\d+ +([A-Za-z0-9/-]+) +(\\d+) +\\d+ +\\d+ +(\\d+) +(\\d+) +\\d+ +\\d+ +(\\d+) +\\d+ +\\d+ +\\d+");
    private static final Logger LOG = Logger.getLogger(IOstatReader.class.getName());
    private String device_regexp = "sda";
    @Inject
    private Configuration conf;

    /**
     * It creates a wrapper to obtain statistics about a block devices by
     * reading information from <i>/sys/block/device_name/stat</i>
     *
     * @param deviceregexp a string with a regexp to match with the device to
     * control <i>(e.g. "sd[ab]")</i>
     *
     *
     */
    public IOstatReader(String deviceregexp) {
        this.device_regexp = deviceregexp;
        LOG.log(Level.INFO, "Recording stats on the device(s) {0}", deviceregexp);

        checkArgument(diskstats.exists(), "/proc/diskstats does not exists");
        checkArgument(diskstats.canRead(), "/proc/diskstats can not be read");



    }

    public IOstatReader() {
//        checkArgument(diskstats.exists(), "/proc/diskstats does not exists");
//        checkArgument(diskstats.canRead(), "/proc/diskstats can not be read");
        LOG.log(Level.INFO, "Recording stats on the device(s) {0}", this.device_regexp);
    }

    @Override
    public List<Metric> call() throws Exception {

        Builder<Metric> b = ImmutableList.builder();
        LineReader lr = new LineReader(new FileReader(diskstats));
        String l;
        while ((l = lr.readLine()) != null) {
            Matcher m = dline.matcher(l);
            if (!m.matches()) {
                LOG.log(Level.WARNING, "This line does not match: {0}", l);
                continue;
            }
            String device = m.group(1);
            if (device.matches(device_regexp)) {
                b.addAll(Metric.Metric(device)
                        .addMetric("n_reads", Long.parseLong(m.group(2)))
                        .addMetric("mills_reading", Long.parseLong(m.group(3)))
                        .addMetric("n_writes", Long.parseLong(m.group(4)))
                        .addMetric("mills_reading", Long.parseLong(m.group(5))).getList());

            }
        }
        return b.build();

    }

    @Override
    public void configure() {
        if (conf.containsKey("iostat-reader.deviceregexp")) {
            this.device_regexp = conf.getString("iostat-reader.deviceregexp");
            LOG.log(Level.INFO, "Recording stats on the device(s) {0}", this.device_regexp);
            checkArgument(diskstats.exists(), "/proc/diskstats does not exists");
            checkArgument(diskstats.canRead(), "/proc/diskstats can not be read");
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
