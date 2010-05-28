package org.bibsonomy.lucene.database.results;

/**
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
	public Pair( T first, U second )
	{
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

	@Override
	public int hashCode() {
		return (getFirst() == null? 0 : getFirst().hashCode() * 31) + (second == null? 0 : second.hashCode());
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals( Object oth ) {
		if ( this == oth ) {
			return true;
		}
		if ( oth == null || !(getClass().isInstance( oth )) )
		{
			return false;
		}
		final Pair<T, U> other = (Pair<T,U>) oth ;
		return (getFirst() == null? other.getFirst() == null : getFirst().equals( other.getFirst() ))
		&& (second == null? other.second == null : second.equals( other.second ));
	}
} 