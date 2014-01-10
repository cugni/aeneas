package es.bsc.aeneas.cassandra.serializers;

import es.bsc.aeneas.model.marshalling.PointType;
import me.prettyprint.cassandra.serializers.AbstractSerializer;

import java.nio.ByteBuffer;

public class BoxSerializer extends AbstractSerializer<BoxType> {
	public final static int BYTE_SIZE=PointSerializer.BYTE_SIZE*3;
	private static PointSerializer ps=PointSerializer.get();
	private final static BoxSerializer instance=new BoxSerializer();;
	public static BoxSerializer get(){
		return instance;
	}
	@Override
	public ByteBuffer toByteBuffer(BoxType b) {
		byte[] tmp= new byte[BoxSerializer.BYTE_SIZE];
		for(int i=0;i<BoxType.NUMBER_OF_POINTS;i++){
			PointType p=b.getPoints().get(i);
			System.arraycopy(ps.toBytes(p), 0, tmp, 
					PointSerializer.BYTE_SIZE*i, PointSerializer.BYTE_SIZE);
		}
		return ByteBuffer.wrap(tmp);
		
	}

	@Override
	public BoxType fromByteBuffer(ByteBuffer byteBuffer) {
		BoxType b=new BoxType();
		for(int i=0;i<BoxType.NUMBER_OF_POINTS;i++){
			b.getPoints().add(ps.fromByteBuffer(byteBuffer));
		}
		return b;
	}

}
