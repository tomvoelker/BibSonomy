package org.bibsonomy.community.util;

public class Triple <T, U, V> extends Pair<T,U>
{
	private V third;
	
	private transient final int hash;

	public Triple() {
		this.third= null;
		this.hash = 0;
	}
	
	public Triple( T f, U s, V t )
	{
		setFirst(f);
		setSecond(s);
		setThird(t);
		hash = (getThird() == null? 0 : getThird().hashCode() * 97) + super.hashCode();
	}

	public void setThird(V third) {
		this.third = third;
	}

	public V getThird() {
		return third;
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
		Triple<T, U, V> other = getClass().cast( oth );
		
		return (getFirst() == null? other.getFirst() == null : getFirst().equals( other.getFirst() ))
		&& (getSecond() == null? other.getSecond() == null : getSecond().equals( other.getSecond() ))
		&& (getThird() == null? other.getThird() == null : getThird().equals( other.getThird() ));		
	}

	@Override
	public String toString() {
		return "["+getFirst()+", "+getSecond()+", "+getThird()+"]";
	}

} 