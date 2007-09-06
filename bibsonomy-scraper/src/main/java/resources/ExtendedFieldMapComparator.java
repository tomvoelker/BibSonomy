package resources;

import java.io.Serializable;
import java.util.Comparator;

public class ExtendedFieldMapComparator implements Comparator<ExtendedFieldMap>, Serializable {
	
	private static final long serialVersionUID = 4735362385246669491L;

	public int compare (ExtendedFieldMap o1, ExtendedFieldMap o2) {
		return o1.getOrder() - o2.getOrder();
	}	
}