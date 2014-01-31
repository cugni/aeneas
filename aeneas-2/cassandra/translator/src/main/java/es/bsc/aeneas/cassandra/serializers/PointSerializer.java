package es.bsc.aeneas.cassandra.serializers;

import es.bsc.aeneas.core.model.PointType;
 import me.prettyprint.cassandra.serializers.AbstractSerializer;
import me.prettyprint.cassandra.serializers.DoubleSerializer;
import me.prettyprint.hector.api.ddl.ComparatorType;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class PointSerializer extends AbstractSerializer<PointType> {

    public final static int BYTE_SIZE = Double.SIZE / 8 * 3;
    private final static DoubleSerializer ls = new DoubleSerializer();
    private final static PointSerializer instance = new PointSerializer();

    public static PointSerializer get() {
        return instance;
    }

    @Override
    public ByteBuffer toByteBuffer(PointType p) {
        ByteBuffer b = ByteBuffer.allocate(24);
        b.putDouble(0, p.getX());
        b.putDouble(8, p.getY());
        b.putDouble(16, p.getZ());
        return b;
    }

    @Override
    public PointType fromByteBuffer(ByteBuffer byteBuffer) {
        try {
            PointType p = new PointType();
            if (byteBuffer.capacity() < PointSerializer.BYTE_SIZE) {
                throw new RuntimeException(String.format("Wrong buffer dimension for a PointType: required %d, found %d", PointSerializer.BYTE_SIZE, byteBuffer.capacity()));
            }
            p.setX(ls.fromByteBuffer(byteBuffer));
            p.setY(ls.fromByteBuffer(byteBuffer));
            p.setZ(ls.fromByteBuffer(byteBuffer));
            return p;
        } catch (BufferUnderflowException buf) {
            throw buf;
        }

    }
    private final static ComparatorType comparator = ComparatorType.getByClassName("es.bsc.aeneas.cassandra.model.CPointType");

    @Override
    public ComparatorType getComparatorType() {
        return comparator;
    }
}
