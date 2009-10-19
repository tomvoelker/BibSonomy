package org.bibsonomy.lucene.database.results;

public class Pair <T, U>
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
		Pair<T, U> other = getClass().cast( oth );
		return (getFirst() == null? other.getFirst() == null : getFirst().equals( other.getFirst() ))
		&& (second == null? other.second == null : second.equals( other.second ));
	}

} 