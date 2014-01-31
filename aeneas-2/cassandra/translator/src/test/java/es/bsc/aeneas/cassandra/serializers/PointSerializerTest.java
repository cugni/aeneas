package es.bsc.aeneas.cassandra.serializers;

 
import es.bsc.aeneas.core.model.PointType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PointSerializerTest {

	@Test
	public void testConversion() {
		PointSerializer ps=new PointSerializer();
		PointType[] points=new PointType[]{new PointType(0.0,0.0,0.0),
								new PointType(1d,2d,3d),
								new PointType(0.0,2.0,0.0),
								new PointType(1.0,0.0e-23,0.0),
								new PointType(3d,2d,3.3e+34),
								new PointType(Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE),
								new PointType(Double.MAX_VALUE,Double.MIN_VALUE,Double.MAX_VALUE),								
								new PointType(Double.MIN_VALUE,Double.MIN_VALUE,Double.MIN_VALUE)};
		for(PointType p : points){
			PointType p2=ps.fromByteBuffer(ps.toByteBuffer(p));
			assertEquals("x",p.getX(),p2.getX());
			assertEquals("y",p.getY(),p2.getY());
			assertEquals("z",p.getZ(),p2.getZ());
			p2=ps.fromBytes(ps.toBytes(p));
			assertEquals("x",p.getX(),p2.getX());
			assertEquals("y",p.getY(),p2.getY());
			assertEquals("z",p.getZ(),p2.getZ());
		}
	}
        
	@Test
	public void testGrouping() {
            PointType p=new PointType();
           
            
        }
        

}
