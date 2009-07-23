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
	
	public Integer getPeriodStart() {
		return this.periodStart;
	}
	public void setPeriodStart(Integer periodStart) {
		this.periodStart = periodStart;
	}
	public Integer getPeriodEnd() {
		return this.periodEnd;
	}
	public void setPeriodEnd(Integer periodEnd) {
		this.periodEnd = periodEnd;
	}
	public Integer getPeriod() {
		return this.period;
	}
	public void setPeriod(Integer period) {
		this.period = period;
	}
	public String getMethod() {
		return this.method.name().toLowerCase();
	}
	
	public RankingMethod getMethodObj() {
		return this.method;
	}
	
	public void setMethod(String method) {
		if (method != null) {
			RankingMethod newMethod = EnumUtils.searchEnumByName(RankingMethod.values(), method);
			if (newMethod != null) {
				this.method = newMethod;
			}
		}
	}
	public boolean getNormalize() {
		return this.normalize;
	}
	public void setNormalize(boolean normalize) {
		this.normalize = normalize;
	}
	
	public Integer getNextPeriod() {
		if (this.period == null) {
			return 1;
		}
		return this.period + 1;
	}
	
	public Integer getPrevPeriod() {
		if (this.period == null || this.period == 0) {
			return 0;
		}
		return this.period - 1;
	}

}
