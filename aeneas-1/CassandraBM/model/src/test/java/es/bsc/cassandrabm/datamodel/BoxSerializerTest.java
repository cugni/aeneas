package es.bsc.cassandrabm.datamodel;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class BoxSerializerTest {

	@Test
	public void test() {
		BoxSerializer ps=new BoxSerializer();
		ArrayList<BoxType> boxes=new ArrayList<BoxType>();		
		BoxType b=new BoxType();
		b.getPoints().add(new PointType(0d,0d,0d));
		b.getPoints().add(new PointType(0d,0d,0d));
		b.getPoints().add(new PointType(0d,0d,0d));
		boxes.add(b);
		b=new BoxType();
		b.getPoints().add(new PointType(0d,0d,0d));
		b.getPoints().add(new PointType(1d,2d,3d));
		b.getPoints().add(new PointType(1d,2d,3d));
		boxes.add(b);
		b=new BoxType();
		b.getPoints().add(new PointType(0d,0d,0d));
		b.getPoints().add(new PointType(Double.MIN_VALUE,Double.MIN_VALUE,Double.MIN_VALUE));
		b.getPoints().add(new PointType(1d,2d,3d));
		boxes.add(b);
		b=new BoxType();
		b.getPoints().add(new PointType(0d,0d,0d));
		b.getPoints().add(new PointType(Double.MAX_VALUE,Double.MIN_VALUE,Double.MAX_VALUE));
		b.getPoints().add(new PointType(1d,2d,3d));
		boxes.add(b);
		b=new BoxType();
		b.getPoints().add(new PointType(Double.MAX_VALUE,Double.MIN_VALUE,Double.MAX_VALUE));
		b.getPoints().add(new PointType(1d,2d,3d));
		b.getPoints().add(	new PointType(3d,2d,3d));
		boxes.add(b);
		b=new BoxType();
		b.getPoints().add(new PointType(Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE));
		b.getPoints().add(new PointType(Double.MAX_VALUE,Double.MIN_VALUE,Double.MAX_VALUE));
		b.getPoints().add(new PointType(Double.MIN_VALUE,Double.MIN_VALUE,Double.MIN_VALUE));
		boxes.add(b);
		for(BoxType ba:boxes){		
			BoxType b2=ps.fromByteBuffer(ps.toByteBuffer(ba));
//			System.out.println(b2);
//			for(int i=0;i<BoxSerializer.BYTE_SIZE;i++)System.out.print(i/8+"\t");
//			System.out.println();
//			for(byte a: ps.toByteBuffer(ba).array()){
//				System.out.print(a+"\t");		
//				
//			}
			for(int i=0;i<ba.getPoints().size();i++){
				PointType p=ba.getPoints().get(i);
				PointType p2=b2.getPoints().get(i);
				assertEquals("x",p.getX(),p2.getX());
				assertEquals("y",p.getY(),p2.getY());
				assertEquals("z",p.getZ(),p2.getZ());
			}
		}
	}

}
