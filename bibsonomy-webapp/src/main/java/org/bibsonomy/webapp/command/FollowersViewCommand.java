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

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.model.User;

/**
 * 
 * @author Christian Kramer
 */
@Setter
@Getter
public class FollowersViewCommand extends TagResourceViewCommand {
	private List<User> followersOfUser;
	private List<User> userIsFollowing;

	private RankingCommand ranking = new RankingCommand();
	
	/**
	 * defines the similarity measure by which the related users are computed  
	 * (default is folkrank)
	 * TODO: change type to UserRelation
	 */
	private String userSimilarity = UserRelation.FOLKRANK.name();
	
}
