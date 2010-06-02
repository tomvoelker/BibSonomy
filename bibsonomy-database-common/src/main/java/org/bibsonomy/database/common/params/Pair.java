package org.bibsonomy.database.common.params;

/**
 * TODO: replace the 3 code copies in lucene, recommender and community
 * 
 * @author ?
 * @version $Id$
 * 
 * @param <T>
 * @param <U>
 */
public class Pair <T, U> {
	private T first;
	private U second;

	/**
	 * default constructor
	 */
	public Pair() {
	}
	
	/**
	 * build constructor
	 * 
	 * @param first
	 * @param second
	 */
	public Pair( T first, U second ) {
		this.first = first;
		this.second = second;
	}

	/**
	 * @return the first
	 */
	public T getFirst() {
		return first;
	}

	/**
	 * @param first the first to set
	 */
	public void setFirst(T first) {
		this.first = first;
	}

	/**
	 * @return the second
	 */
	public U getSecond() {
		return second;
	}

	/**
	 * @param second the second to set
	 */
	public void setSecond(U second) {
		this.second = second;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Pair<?,?>)) {
			return false;
		}
		Pair<?,?> other = (Pair<?,?>) obj;
		if (first == null) {
			if (other.first != null) {
				return false;
			}
		} else if (!first.equals(other.first)) {
			return false;
		}
		if (second == null) {
			if (other.second != null) {
				return false;
			}
		} else if (!second.equals(other.second)) {
			return false;
		}
		return true;
	}

	
} 