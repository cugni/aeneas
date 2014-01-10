package es.bsc.aeneas.cassandra.serializers;

import es.bsc.aeneas.model.marshalling.PointType;
import es.bsc.aeneas.model.marshalling.ResidueType;
import me.prettyprint.cassandra.serializers.AbstractSerializer;
import me.prettyprint.cassandra.serializers.IntegerSerializer;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map.Entry;


public class ResidueSerializer extends AbstractSerializer<ResidueType> {

	private final static IntegerSerializer is = IntegerSerializer.get();
	private final static PointSerializer ps = PointSerializer.get();
	private final static ResidueSerializer instance = new ResidueSerializer();

	public static ResidueSerializer get() {
		return instance;
	}

        @Override
	public ResidueType fromByteBuffer(ByteBuffer bb) {
		ResidueType residue = new ResidueType();
		int nAtoms = is.fromByteBuffer(bb);
		for (int i=0; i<nAtoms; i++) {
			Integer atomid = is.fromByteBuffer(bb);
			PointType point = ps.fromByteBuffer(bb);
			residue.addAtom(atomid, point);
		}
		return residue;
	}

        @Override
	public ByteBuffer toByteBuffer(ResidueType residue) {
		HashMap<Integer, PointType> atoms = residue.getAtoms();
		ByteBuffer b = ByteBuffer.allocate(((24 + 4)*atoms.size()) + 4);
		b.putInt(atoms.size());
		for (Entry<Integer, PointType> entry : atoms.entrySet()) {
			b.putInt(entry.getKey());
			b.putDouble(entry.getValue().getX());
			b.putDouble(entry.getValue().getY());
			b.putDouble(entry.getValue().getZ());
		}
                b.position(0);
		return b;
	}

}
