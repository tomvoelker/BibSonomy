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
package org.bibsonomy.webapp.command.ajax;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.BaseCommand;


/**
 * @author bsc
 */
@Getter
@Setter
public class UserSearchCommand extends BaseCommand {
	private String search;
	private int limit;
	private List<User> users;
	private boolean showSpammers;
	
	//for ajax callbacks
	private String callback = "";

	/**
	 * @param limit the limit to set
	 */
	public void setLimit(int limit) {
		this.limit = (limit > 0 ? limit : 10);
	}

	/**
	 * @return the showSpammers
	 */
	public boolean showSpammers() {
		return showSpammers;
	}

}
