package org.bibsonomy.webapp.util.spring.condition;

/**
 * {@link Condition} that checks whether a checkProperty String contains an expected value in a comma-separated list.
 * 
 * @author jensi
 */
public class StringListContainsCondition implements Condition {
	private String stringList;
	private String expected;
	
	
	@Override
	public boolean eval() {
		for (String s : stringList.split(",")) {
			if (s.trim().equalsIgnoreCase(expected)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the checkProperty
	 */
	public String getStringList() {
		return this.stringList;
	}

	/**
	 * @param checkProperty the checkProperty to set
	 */
	public void setStringList(String checkProperty) {
		this.stringList = checkProperty;
	}

	/**
	 * @return the expected
	 */
	public String getExpected() {
		return this.expected;
	}

	/**
	 * @param expected the expected to set
	 */
	public void setExpected(String expected) {
		this.expected = expected;
	}
}
