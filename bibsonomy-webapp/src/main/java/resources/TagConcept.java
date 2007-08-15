package resources;

/**
 * Stores a single tag together with some of its properties for viewing it in the tag cloud.
 * 
 *
 */
public class TagConcept implements Comparable<TagConcept> {
	
	/**
	 * The name of the tag as stored in the database.
	 */
	private String name;
	/**
	 * How often is this tag used (by the user).
	 */
	private int count;
	/**
	 * <code>true</code> if this tag is a supertag of one or more relations.
	 */
	private boolean supertag;
	/**
	 * <code>true</code> if this tag is shown (i.e., selected to be shown in the tag relations list).
	 */
	private boolean shown;
	/**
	 * To decide, how to compare two objects of this type. Either <code>0</code> (for alph)- 
	 * to compare alphabetically, or <code>1</code> (for freq) to compare by count.
	 */
	private int sortOrder;
	
	
	/** 
	 * Simple constructor which sets all attributes at once
	 * @param name name of the tag
	 * @param count how often is this tag used (by the user)
	 * @param supertag <code>true</code> if it is a supertag of one or more relations
	 * @param shown <code>true</code> if this tag is shown
	 */
	public TagConcept (String name, int count, boolean supertag, boolean shown, int sortOrder){
		this.name      = name;
		this.count     = count;
		this.supertag  = supertag;
		this.shown     = shown;
		this.sortOrder = sortOrder;
	}

	public String toString() {
		return name + "(" + count + ") supertag(" + supertag + ") shown(" + shown + ")";
	}
	
	/** Compares two TagConcepts. Depending on the private value {@link #sortOrder}
	 * they are compared by their count or alphabetically.
	 *  
	 * @param o
	 * @return
	 */
	public int compareTo (TagConcept o) {
		if (o == null) { throw new NullPointerException(); }
		/*
		 * check, how to compare these objects
		 */
		if (sortOrder == 1) {
			/*
			 * compare by count (frequency)
			 */
			if (this.count < o.count) {
				return +1;
			} else if (this.count > o.count) {
				return -1;
			}
			/*
			 * if they have the same count, order alphabetically
			 */
			return compareAlph(o);
		} else { // if ("alph".equals(sortOrder)) {
			/*
			 * compare alphabetically
			 */
			/*
			 * note: this does not take <em>locales</em> into account, see
			 * http://java.sun.com/j2se/1.5.0/docs/api/java/lang/String.html#compareTo(java.lang.String)
			 * and
			 * http://java.sun.com/j2se/1.5.0/docs/api/java/text/Collator.html#compare(java.lang.String,%20java.lang.String)
			 */
			return compareAlph(o);
		} // else {
			/*
			 * if we don't know, how to compare the objects, they're all the same
			 */
		  // return 0;	
		//}
	}
		
	/** 
	 * Compares two TagConcepts alphabetically, first ignoring case (so that B comes after a) 
	 * and not ignoring it, when two tags are the same without case (like A and a)
	 * 
	 * @param o
	 * @return
	 */
	private int compareAlph (TagConcept o) {
		int c = this.name.compareToIgnoreCase(o.name);
		if (c == 0) {
			return this.name.compareTo(o.name);
		}
		return c;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		TagConcept tagConcept = null;
		try {
			tagConcept = (TagConcept)o;
		} catch(ClassCastException e) {
			System.out.println("TagConcept.equals(Object o): " + e);
		}
		return name.equals(tagConcept.name); 
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return name.hashCode();	
	}

		
	public int getCount() {
		return count;
	}
	public String getName() {
		return name;
	}
	public boolean isShown() {
		return shown;
	}
	public boolean isSupertag() {
		return supertag;
	}

}