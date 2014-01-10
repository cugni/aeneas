/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.cassandra.model;

import java.nio.ByteBuffer;
import org.apache.cassandra.cql.jdbc.JdbcBytes;
import org.apache.cassandra.db.marshal.AbstractType;
import org.apache.cassandra.db.marshal.DoubleType;
import org.apache.cassandra.db.marshal.MarshalException;

/**
 *
 * @author cesare
 */
public class NMapPointType extends AbstractType<ByteBuffer> {

    public static final NMapPointType instance = new NMapPointType();

    NMapPointType() {
    }
    DoubleType d = DoubleType.instance;

    @Override
    public int compare(ByteBuffer o1, ByteBuffer o2) {
        if (o1.remaining() == 0) {
            return o2.remaining() == 0 ? 0 : -1;
        }
        if (o2.remaining() == 0) {
            return 1;
        }
        ByteBuffer c1 = o1.duplicate();
        ByteBuffer c2 = o2.duplicate();
        double x1 = d.compose(c1);
        double x2 = d.compose(c2);
        c1.position(8);
        c2.position(8);
        double y1=d.compose(c1);
        double y2=d.compose(c2);
        c1.position(16);
        c2.position(16);
        double z1=d.compose(c1);
        double z2=d.compose(c2);
        double n1=Math.sqrt(x1*x1+y1*y1+z1*z1);
        double n2=Math.sqrt(x2*x2+y2*y2+z2*z2);
        return Double.compare(n1, n2);
    }

    @Override
    public String getString(ByteBuffer bytes) {
        validate(bytes);
        StringBuilder s = new StringBuilder(20);
        s.append("{")//
                .append(bytes.getDouble(0)).append(",")//
                .append(bytes.getDouble(8)).append(",")//
                .append(bytes.getDouble(16)).append("}");
        return s.toString();
    }

    @Override
    public boolean isCompatibleWith(AbstractType<?> previous) {
        return previous instanceof NMapPointType;
    }

    @Override
    public void validate(ByteBuffer bytes) throws MarshalException {
        if (bytes.remaining() != 24 && bytes.remaining() != 0) {
            throw new MarshalException(String.format("Expected 24 or 0 byte value for a PointType (%d)", bytes.remaining()));
        }

    }

    @Override
    public ByteBuffer compose(ByteBuffer bytes) {
        return JdbcBytes.instance.compose(bytes);
    }

    @Override
    public ByteBuffer decompose(ByteBuffer value) {
        return JdbcBytes.instance.decompose(value);
    }

    @Override
    public ByteBuffer fromString(String source) throws MarshalException {
        try {
            String[] split = source.replaceAll("[\\{\\}]", "").split(",");
            ByteBuffer b = ByteBuffer.allocate(24);
            b.putDouble(0, Double.parseDouble(split[0]));
            b.putDouble(8, Double.parseDouble(split[1]));
            b.putDouble(16, Double.parseDouble(split[2]));
            return b;
        } catch (Exception e) {
            throw new IllegalArgumentException("The format of the string " + source + " is not valid.");
        }
    }
}
