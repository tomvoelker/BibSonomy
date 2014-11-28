/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
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
package org.bibsonomy.webapp.command.admin;

import java.util.HashMap;
import java.util.Map;

/**
 * Presents status information of spam framework, for example, how many 
 * spammers have been flagged recently
 * @author sts
 * @author bkr
 */
public class AdminStatisticsCommand {

	private final Map<Long, Integer> numAdminSpammer = new HashMap<Long, Integer>();
	
	private final Map<Long, Integer> numAdminNoSpammer = new HashMap<Long, Integer>();
	
	private final Map<Long, Integer> numClassifierSpammer = new HashMap<Long, Integer>();
	
	private final Map<Long, Integer> numClassifierSpammerUnsure = new HashMap<Long, Integer>();
	
	private final Map<Long, Integer> numClassifierNoSpammer = new HashMap<Long, Integer>();
	
	private final Map<Long, Integer> numClassifierNoSpammerUnsure = new HashMap<Long, Integer>();
	
	public Map<Long, Integer> getNumAdminSpammer() {
		return this.numAdminSpammer;
	}

	public void setNumAdminSpammer(final Long interval, final int counts) {
		this.numAdminSpammer.put(interval, counts);
	}

	public Map<Long, Integer> getNumAdminNoSpammer() {
		return this.numAdminNoSpammer;
	}

	public void setNumAdminNoSpammer(final Long interval, final int counts) {
		this.numAdminNoSpammer.put(interval, counts);
	}

	public Map<Long, Integer> getNumClassifierSpammer() {
		return this.numClassifierSpammer;
	}

	public void setNumClassifierSpammer(final Long interval, final int counts) {
		this.numClassifierSpammer.put(interval, counts);
	}

	public Map<Long, Integer> getNumClassifierSpammerUnsure() {
		return this.numClassifierSpammerUnsure;
	}

	public void setNumClassifierSpammerUnsure(final Long interval, final int counts) {
		this.numClassifierSpammerUnsure.put(interval, counts);
	}

	public Map<Long, Integer> getNumClassifierNoSpammer() {
		return this.numClassifierNoSpammer;
	}

	public void setNumClassifierNoSpammer(final Long interval, final int counts) {
		this.numClassifierNoSpammer.put(interval, counts);
	}

	public Map<Long, Integer> getNumClassifierNoSpammerUnsure() {
		return this.numClassifierNoSpammerUnsure;
	}

	public void setNumClassifierNoSpammerUnsure(final Long interval, final int counts) {
		this.numClassifierNoSpammerUnsure.put(interval, counts);
	}
}