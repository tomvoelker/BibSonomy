package org.bibsonomy.webapp.util.spring.condition;

/**
 * TODO: add documentation to this class
 * 
 * @author lutful
 */
public class CheckSharedResourceSearchEnabled implements Condition {
	private String clusterName;
	private String notExpected;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.webapp.util.spring.condition.Condition#eval()
	 */
	@Override
	public boolean eval() {
		if (clusterName.equalsIgnoreCase(notExpected))
			return false;
		return true;
	}

	/**
	 * @return the clusterName
	 */
	public String getClusterName() {
		return this.clusterName;
	}

	/**
	 * @param clusterName the clusterName to set
	 */
	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	/**
	 * @return the notExpected
	 */
	public String getNotExpected() {
		return this.notExpected;
	}

	/**
	 * @param notExpected the notExpected to set
	 */
	public void setNotExpected(String notExpected) {
		this.notExpected = notExpected;
	}

}
