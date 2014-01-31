package es.bsc.aeneas.cassandra.serializers;

import es.bsc.aeneas.core.model.MapPointType;
 import me.prettyprint.cassandra.serializers.AbstractSerializer;
import me.prettyprint.cassandra.serializers.DoubleSerializer;
import me.prettyprint.hector.api.ddl.ComparatorType;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class MapPointSerializer extends AbstractSerializer<MapPointType> {

    public final static int BYTE_SIZE = Double.SIZE / 8 * 2;
    private final static DoubleSerializer ls = new DoubleSerializer();
    private final static MapPointSerializer instance = new MapPointSerializer();

    public static MapPointSerializer get() {
        return instance;
    }

    @Override
    public ByteBuffer toByteBuffer(MapPointType p) {
        ByteBuffer b = ByteBuffer.allocate(16);
        b.putDouble(0, p.getLat());
        b.putDouble(8, p.getLon());
        return b;
    }

    @Override
    public MapPointType fromByteBuffer(ByteBuffer byteBuffer) {
        try {
            MapPointType p = new MapPointType();
            if (byteBuffer.capacity() < MapPointSerializer.BYTE_SIZE) {
                throw new RuntimeException(String.format("Wrong buffer dimension for a PointType: required %d, found %d", MapPointSerializer.BYTE_SIZE, byteBuffer.capacity()));
            }
            p.setLat(ls.fromByteBuffer(byteBuffer));
            p.setLon(ls.fromByteBuffer(byteBuffer));
           
            return p;
        } catch (BufferUnderflowException buf) {
            throw buf;
        }

    }
    private final static ComparatorType comparator = 
            ComparatorType.getByClassName("org.bsc.aeneas.cassandra.model.NMapPointType");

    @Override
    public ComparatorType getComparatorType() {
        return comparator;
    }
}
