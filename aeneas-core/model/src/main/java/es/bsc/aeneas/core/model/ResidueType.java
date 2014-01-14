package es.bsc.aeneas.core.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;

//import es.bsc.aeneas.model.marshalling.PointType;


public class ResidueType implements Serializable{

	private HashMap<Integer, PointType> atoms;
	
	public ResidueType() {
		atoms = new HashMap<Integer, PointType>();
	}
	
	
	
	public void addAtom(int atomId, PointType point) {
		atoms.put(atomId, point);
	}
	
	public HashMap<Integer, PointType> getAtoms() {
		return atoms;
	}
	
        @Override
	public String toString() {
		String result = "[";
		for (Entry<Integer, PointType> entry : atoms.entrySet())
			result += entry.getKey() + ":" + entry.getValue() + ", ";
		result += "]";
		
		return result;
	}

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.atoms != null ? this.atoms.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ResidueType other = (ResidueType) obj;
        if (this.atoms != other.atoms && (this.atoms == null || !this.atoms.equals(other.atoms))) {
            return false;
        }
          return true;
    }
 

}
