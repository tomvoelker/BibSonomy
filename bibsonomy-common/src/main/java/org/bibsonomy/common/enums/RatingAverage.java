package org.bibsonomy.common.enums;

/**
 * @author dzo
 * @version $Id$
 */
public enum RatingAverage {
	/**
	 * 1/n * ∑{i = 1}{n}(a_i)
	 */
	ARITHMETIC_MEAN;

	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 * 
	 * for iBatis statement mapping
	 */
	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
}
