package org.bibsonomy.model.enums;


/**
 * 
 * all possible relations between two gold standards
 *
 * @author lka
 */
public enum GoldStandardRelation {
	/** gold standard is referenced by another gold standard */
	REFERENCE(0),
	
	/** gold standard is part of another gold standard */
	PART_OF(1);
	
	/**
	 * @param id
	 * @return the relation to the provided id
	 */
	public static GoldStandardRelation getGoldStandardRelation(final int id) {
		for (GoldStandardRelation goldStandardRelation : GoldStandardRelation.values()) {
			if (goldStandardRelation.getValue() == id) {
				return goldStandardRelation;
			}
		}
		
		throw new IllegalArgumentException("no relation with id " + id);
	}
	
	private final int value;
	
	private GoldStandardRelation(int value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return this.value;
	}
}
