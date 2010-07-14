package org.bibsonomy.recommender.tags.database.params;

/**
 * 
 * @author fei
 * @version $Id$
 *
 * @param <T>
 * @param <U>
 */
@Deprecated // TODO: remove copy
public class Pair <T extends Comparable<T>, U extends Comparable<U>> implements Comparable<Pair<T,U>>
{
	private T first;
	private U second;
	private transient final int hash;

	public Pair() {
		this.first = null;
		this.second = null;
		this.hash = 0;
	}
	
	public Pair( T f, U s )
	{
		this.first = f;
		this.second = s;
		hash = (getFirst() == null? 0 : getFirst().hashCode() * 31)
		+(second == null? 0 : second.hashCode());
	}

	public T getFirst()
	{
		return first;
	}
	public U getSecond()
	{
		return second;
	}
	public void setFirst(T first)
	{
		this.first = first;
	}
	public void setSecond(U second)
	{
		this.second = second;
	}


	@Override
	public int hashCode()
	{
		return hash;
	}

	@Override
	public boolean equals( Object oth )
	{
		if ( this == oth )
		{
			return true;
		}
		if ( oth == null || !(getClass().isInstance( oth )) )
		{
			return false;
		}
		Pair<?, ?> other = getClass().cast( oth );
		return (getFirst() == null? other.getFirst() == null : getFirst().equals( other.getFirst() ))
		&& (second == null? other.second == null : second.equals( other.second ));
	}

	/**
	 * implements lexicographic ordering
	 */
	@Override
	public int compareTo(Pair<T, U> o) {
		int left = getFirst().compareTo(o.getFirst());
		int right= getSecond().compareTo(o.getSecond());
		if( left!=0 )
			return left;
		
		return right;
	}

} 