package org.bibsonomy.webapp.command;

import org.bibsonomy.util.EnumUtils;
import org.bibsonomy.webapp.util.RankingUtil.RankingMethod;

/**
 * Command to hold information about ranking
 * 
 * @author dbenz
 * @version $Id$
 */
public class RankingCommand {
	
	/** 
	 * the ranking period; starting from 1:
	 *   - 1: the most recent 1000 posts
	 *   - 2: most recent 1001 to 2000
	 *   - 3: most recent 2001 to 3000... 
	*/	
	private Integer period = 0;
	/**
     * Start-/End values for ranking periods
    */ 	
	private Integer periodStart;
	private Integer periodEnd;
	/**
	 * the ranking method used
	 */
	private RankingMethod method = RankingMethod.TFIDF;
	/**
	 * whether to normalize the ranking or not
	 */
	private boolean normalize = false;
	
	/**
	 * @return the period
	 */
	public Integer getPeriod() {
		return this.period;
	}

	/**
	 * @param period the period to set
	 */
	public void setPeriod(Integer period) {
		this.period = period;
	}

	/**
	 * @return the periodStart
	 */
	public Integer getPeriodStart() {
		return this.periodStart;
	}

	/**
	 * @param periodStart the periodStart to set
	 */
	public void setPeriodStart(Integer periodStart) {
		this.periodStart = periodStart;
	}

	/**
	 * @return the periodEnd
	 */
	public Integer getPeriodEnd() {
		return this.periodEnd;
	}

	/**
	 * @param periodEnd the periodEnd to set
	 */
	public void setPeriodEnd(Integer periodEnd) {
		this.periodEnd = periodEnd;
	}

	/**
	 * @return the name of the {@link #method} (lower case)
	 */
	public String getMethod() {
		return this.method.name().toLowerCase();
	}
	
	/**
	 * @return the {@link #method}
	 */
	public RankingMethod getMethodObj() {
		return this.method;
	}
	
	/**
	 * @param method the name of the method to set
	 */
	public void setMethod(String method) {
		if (method != null) {
			RankingMethod newMethod = EnumUtils.searchEnumByName(RankingMethod.values(), method);
			if (newMethod != null) {
				this.method = newMethod;
			}
		}
	}
	
	/**
	 * @return the normalize
	 */
	public boolean getNormalize() {
		return this.normalize;
	}
	
	/**
	 * @param normalize the normalize to set
	 */
	public void setNormalize(boolean normalize) {
		this.normalize = normalize;
	}

	/**
	 * @return the next period
	 */
	public Integer getNextPeriod() {
		if (this.period == null) {
			return 1;
		}
		return this.period + 1;
	}
	
	/**
	 * @return the previous period
	 */
	public Integer getPrevPeriod() {
		if (this.period == null || this.period == 0) {
			return 0;
		}
		return this.period - 1;
	}
}
