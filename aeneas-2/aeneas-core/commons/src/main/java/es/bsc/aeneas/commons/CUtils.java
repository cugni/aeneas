package es.bsc.aeneas.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.SystemConfiguration;

/**
 * Hello world!
 *
 */
public class CUtils {

    private final static Logger proplog = Logger.getLogger("SystemProperty");
    private static final Random rand = new Random();
    private static final ThreadLocal<Random> rng = new ThreadLocal<Random>();
    public static final int FNV_offset_basis_32 = 0x811c9dc5;
    public static final int FNV_prime_32 = 16777619;
    public static final long FNV_offset_basis_64 = 0xCBF29CE484222325L;
    public static final long FNV_prime_64 = 1099511628211L;
    private static Configuration conf = new VerboseConfiguration(new SystemConfiguration());

    @Deprecated
    public static void setConfiguration(Configuration conf) {
        CUtils.conf = conf;
    }

    @Deprecated
    public static Configuration getConfiguration() {
        return CUtils.conf;
    }

    @Deprecated
    public static Integer getInt(String name, Integer defval) {
        return conf.getInt(name, defval);
    }

    @Deprecated
    public static Float getFloat(String name, Float defval) {
        return conf.getFloat(name, defval);
    }

    @Deprecated
    public static Object getProperty(String name) {
        return conf.getProperty(name);

    }

    @Deprecated
    public static Object getProperty(String name, Object defval) {
        Object ret = conf.getProperty(name);
        return ret == null ? defval : ret;

    }

    @Deprecated
    public static String getString(String name) {
        return conf.getString(name);

    }

    @Deprecated
    public static String getString(String name, String defval) {
        return conf.getString(name, defval);
    }

    @Deprecated
    public static Boolean getBoolean(String name, Boolean defval) {
        return conf.getBoolean(name, defval);
    }

    public static Random random() {
        Random ret = rng.get();
        if (ret == null) {
            ret = new Random(rand.nextLong());
            rng.set(ret);
        }
        return ret;
    }

    /**
     * Generate a random ASCII string of a given length.
     *
     * @param length number of chars in the string
     * @return A random ascii string
     */
    public static String RandomASCIIString(int length) {
        int interval = '~' - ' ' + 1;

        byte[] buf = new byte[length];
        random().nextBytes(buf);
        for (int i = 0; i < length; i++) {
            if (buf[i] < 0) {
                buf[i] = (byte) ((-buf[i] % interval) + ' ');
            } else {
                buf[i] = (byte) ((buf[i] % interval) + ' ');
            }
        }
        return new String(buf);
    }

    /**
     * Hash an integer value.
     */
    public static long hash(long val) {
        return FNVhash64(val);
    }

    /**
     * 32 bit FNV hash. Produces more "random" hashes than (say)
     * String.hashCode().
     *
     * @param val The value to hash.
     * @return The hash value
     */
    public static int FNVhash32(int val) {
        //from http://en.wikipedia.org/wiki/Fowler_Noll_Vo_hash
        int hashval = FNV_offset_basis_32;

        for (int i = 0; i < 4; i++) {
            int octet = val & 0x00ff;
            val = val >> 8;

            hashval = hashval ^ octet;
            hashval = hashval * FNV_prime_32;
            //hashval = hashval ^ octet;
        }
        return Math.abs(hashval);
    }

    /**
     * 64 bit FNV hash. Produces more "random" hashes than (say)
     * String.hashCode().
     *
     * @param val The value to hash.
     * @return The hash value
     */
    public static long FNVhash64(long val) {
        //from http://en.wikipedia.org/wiki/Fowler_Noll_Vo_hash
        long hashval = FNV_offset_basis_64;

        for (int i = 0; i < 8; i++) {
            long octet = val & 0x00ff;
            val = val >> 8;

            hashval = hashval ^ octet;
            hashval = hashval * FNV_prime_64;
            //hashval = hashval ^ octet;
        }
        return Math.abs(hashval);
    }

    public static List<Integer> getDiscreteRange(int from, int to) {
        ArrayList<Integer> l = new ArrayList<Integer>(to - from);
        for (int i = from; i < to; i++) {
            l.add(i);
        }
        return l;
    }

    public static List<Integer> getDiscreteRange(int from, int to, int maxsize) {
        ArrayList<Integer> l = new ArrayList<Integer>((to - from) < maxsize ? to - from : maxsize);
        for (int i = from; i < from + maxsize && i < to; i++) {
            l.add(i);
        }
        return l;
    }

   
    

    private CUtils() {
    }
}
