package resources;

public class TagRelation implements Comparable<TagRelation> {
	
	private String upper;
	private String lower;
	private boolean valid;
	
	public final static String LOWER_UPPER = "->"; // i.e. a->b means a "is a" b 
	public final static String UPPER_LOWER = "<-"; // i.e. b<-a means a "is a" b

	
	public TagRelation(String lower, String upper){
		this.valid = lower != null && upper != null && !lower.equals(upper);
		this.lower = lower;
		this.upper = upper;
	}
	
	public String getLower() {
		return lower;
	}

	public String getUpper() {
		return upper;
	}

	public boolean isValid() {
		return valid;
	}
	
	public boolean equals(Object o) {
		TagRelation tagrel = null;
		try {
			tagrel = (TagRelation)o;
		} catch(ClassCastException e) {
			System.out.println("TagRelation.equals(Object o): " + e);
		}
		return tagrel != null && 
			   tagrel.isValid() && 
			   valid && 
			   tagrel.getUpper().equals(upper) && 
			   tagrel.getLower().equals(lower);
	}
	
	public String toString(){
		return upper + UPPER_LOWER + lower;
	}

	@Override
	public int hashCode() {
		return lower.hashCode() ^ upper.hashCode();
	}

	/** 
	 * Compares two tag relations. For the upper tags, the case is ignored,
	 * for the lower tags it is NOT ignored, since other wise equals() and 
	 * compareTo wouldn't be compatible.
	 */
	public int compareTo(TagRelation o) {
		if (o == null) { throw new NullPointerException(); }
		if (this.upper.toLowerCase().equals(o.upper.toLowerCase())) {
			// the upper tags of both relations are equal --> compare lower tags
			return this.lower.compareTo(o.lower);
		} else { 
			// the upper tags of both relations differ
			return this.upper.compareToIgnoreCase(o.upper);
		}
	}
	
	
}