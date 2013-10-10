package org.bibsonomy.model;


/**
 * TODO: move to model module?
 * 
 * @author dzo
 */
public class Suggestion implements Comparable<Suggestion> {
	private String name;
	private int rating;
	
	/**
	 * default constructor
	 */
	public Suggestion() {
		// noop
	}
	
	/**
	 * @param name
	 * @param rating
	 */
	public Suggestion(String name, int rating) {
		super();
		this.name = name;
		this.rating = rating;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the rating
	 */
	public int getRating() {
		return rating;
	}
	
	/**
	 * @param rating the rating to set
	 */
	public void setRating(int rating) {
		this.rating = rating;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + rating;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Suggestion other = (Suggestion) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (rating != other.rating)
			return false;
		return true;
	}

	@Override
	public int compareTo(Suggestion other) {
		int ratingDiff = this.rating - other.getRating();
		if (ratingDiff != 0) {
			return ratingDiff < 0 ? - 1 : 1;
		}
		
		return this.name.compareTo(other.getName());
	}
}
