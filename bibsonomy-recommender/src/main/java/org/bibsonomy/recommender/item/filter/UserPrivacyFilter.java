/**
 * BibSonomy Recommendation - Tag and resource recommender.
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.recommender.item.filter;

import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.item.model.RecommendationUser;
import org.bibsonomy.recommender.item.service.ExtendedMainAccess;

import recommender.core.interfaces.filter.PrivacyFilter;

/**
 * Filters user to send only insensitive data to external services.
 * 
 * @author lukas
 *
 */
public class UserPrivacyFilter implements PrivacyFilter<RecommendationUser>{

	private ExtendedMainAccess<? extends Resource> dbAccess;
	
	/*
	 * This method maps usernames to their ids as they are set in the database.
	 * 
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.filter.PrivacyFilter#filterEntity(recommender.core.interfaces.model.RecommendationEntity)
	 */
	@Override
	public RecommendationUser filterEntity(RecommendationUser entity) {
		entity.setUserName(String.valueOf(this.dbAccess.getUserIdByName(entity.getUserName())));
		return entity;
	}

	/**
	 * @param dbAccess
	 */
	public void setDbAccess(ExtendedMainAccess<? extends Resource> dbAccess) {
		this.dbAccess = dbAccess;
	}
}
