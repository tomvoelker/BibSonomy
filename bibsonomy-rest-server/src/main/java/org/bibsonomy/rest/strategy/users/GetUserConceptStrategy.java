/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest.strategy.users;


import java.io.ByteArrayOutputStream;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * Handle user concept request
 * 
 * @author Stefan Stützer
 */
public class GetUserConceptStrategy extends Strategy {

	private final String conceptName;
	private final String userName;

	/**
	 * @param context -  the context
	 * @param conceptName - the name of the supertag to retrieve the subtags for
	 * @param userName - the owner of the concept
	 */
	public GetUserConceptStrategy(final Context context, final String conceptName, final String userName) {
		super(context);
		this.conceptName = conceptName;
		this.userName = userName;
	}

	@Override
	public void perform(final ByteArrayOutputStream outStream) throws InternServerException, NoSuchResourceException {
		final Tag concept = this.getLogic().getConceptDetails(this.conceptName, GroupingEntity.USER, userName);
		if (concept == null) {
			throw new NoSuchResourceException("The requested concept '" + conceptName + "' does not exist for user '" + userName + "'.");
		}
		this.getRenderer().serializeTag(writer, concept, new ViewModel());
	}
	
	@Override
	protected String getContentType() {
		return "tag";
	}
}