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

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.Filter;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.StatisticsUnit;
import org.bibsonomy.model.Resource;


/**
 * command for statistic informations
 *
 * @author dzo
 */
@Getter
@Setter
public class StatisticsCommand extends BaseCommand {
	public enum StatisticType {
		POSTS,
		TAGS,
		DOCUMENTS,
		USERS;
	}
	
	private boolean spammers;
	private boolean all;

	private Class<? extends Resource> resourceType = Resource.class;
	
	private GroupingEntity grouping;
	private Set<Filter> filters = new HashSet<Filter>();
	
	private ConceptStatus conceptStatus;
	
	private Integer interval;
	private StatisticsUnit unit;
	private StatisticType type = StatisticType.POSTS;
	
	private String responseString;

}
