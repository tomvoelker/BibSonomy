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
package org.bibsonomy.rest.strategy.sync;

import java.net.URI;
import java.text.ParseException;
import java.util.Date;

import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.strategy.AbstractDeleteStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author wla
 */
public class DeleteSyncDataStrategy extends AbstractDeleteStrategy {

	private final URI serviceURI;
	private final Class<? extends Resource> resourceType;
	private final String date;

	/**
	 * 
	 * @param context
	 * @param serviceURI
	 */
	public DeleteSyncDataStrategy(final Context context, final URI serviceURI) {
		super(context);
		this.serviceURI = serviceURI;
		this.resourceType = ResourceFactory.getResourceClass(context.getStringAttribute(RESTConfig.RESOURCE_TYPE_PARAM, ResourceFactory.RESOURCE_CLASS_NAME));
		this.date = context.getStringAttribute(RESTConfig.SYNC_DATE_PARAM, null);
	}

	@Override
	protected boolean delete() {

		try {
			final LogicInterface logic = this.getLogic();
			// we allow null dates; they are used to delete ALL entries
			final Date parsedDate = (this.date == null ? null : RESTConfig.parseDate(this.date));
			logic.deleteSyncData(logic.getAuthenticatedUser().getName(), this.serviceURI, this.resourceType, parsedDate);
			return true;
		} catch (ParseException ex) {
			throw new BadRequestOrResponseException("the given date '" + this.date + "' could not be parsed.");
		}
	}
}
