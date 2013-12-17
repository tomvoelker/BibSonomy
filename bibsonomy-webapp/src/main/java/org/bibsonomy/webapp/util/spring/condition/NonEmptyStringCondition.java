package org.bibsonomy.webapp.util.spring.condition;

import org.bibsonomy.util.ValidationUtils;

/**
 * {@link Condition} whether some String value is non-empty.
 * 
 * @author jensi
 */
public class NonEmptyStringCondition implements Condition {
	private String value;
	
	
	@Override
	public boolean eval() {
		return ValidationUtils.present(value);
	}

	/**
	 * @return the value to be checked
	 */
	public String getValue() {
		return this.value;
	}


	/**
	 * @param value the value to be checked
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
