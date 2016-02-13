package org.bibsonomy.webapp.util.spring.condition;

/**
 * a condition that checks for value is equal
 *
 * @author dzo
 * @param <T> 
 */
public class EqualCondition<T> implements Condition {
	
	private T value;
	private T expected;
	
	/**
	 * @param value
	 * @param expected
	 */
	public EqualCondition(T value, T expected) {
		super();
		this.value = value;
		this.expected = expected;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.spring.condition.Condition#eval()
	 */
	@Override
	public boolean eval() {
		return value != null && value.equals(expected);
	}
}
