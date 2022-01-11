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
package org.bibsonomy.webapp.command.admin;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URL;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.webapp.command.BaseCommand;

import recommender.core.database.params.RecAdminOverview;
import recommender.core.interfaces.model.RecommendationResult;

/**
 * Command bean for admin page 
 * 
 * @author bsc
 */
@Getter
@Setter
public class AdminRecommenderViewCommand extends BaseCommand {
	private Map<Class<? extends RecommendationResult>, List<RecAdminOverview>> recommenderOverviewMap;
	/** number of values which will be fetched from the database to calculate average recommender-latencies */
	private Long queriesPerLatency;

	/** the action which will be executed by the controller and set to null again */
	private String action;
	private Class<? extends RecommendationResult> recommendationResultClass;
	private Long recommenderId;

	/** url of new recommender to be added */
	private URL newrecurl;
	private boolean trusted = false;

	/** response-message to the last action executed (e.g. failure, success etc.) set by the controller */
	private String adminResponse;
	
	/**
	 * default constructor
	 */
	public AdminRecommenderViewCommand(){
		this.queriesPerLatency = Long.valueOf(1000);
		this.action = null;
	}
	
	/**
	 * @param queriesPerLatency number of values which will be fetched from the database to calculate average recommender-latencies
	 */
	public void setQueriesPerLatency(final Long queriesPerLatency){
		// only accept positive values
		if (present(queriesPerLatency) && queriesPerLatency.longValue() > 0) {
			this.queriesPerLatency = queriesPerLatency;
		}
	}

}