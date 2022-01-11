/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.command;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.util.EnumUtils;
import org.bibsonomy.webapp.util.RankingUtil.RankingMethod;

/**
 * Command to hold information about ranking
 * 
 * @author dbenz
 */
@Getter
@Setter
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
