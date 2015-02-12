/**
 * BibSonomy-Rest-Server - The REST-server.
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

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.text.ParseException;
import java.util.Date;

import org.bibsonomy.common.exceptions.InvalidModelException;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.sync.SynchronizationStatus;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.strategy.AbstractUpdateStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author wla
 */
public class PutSyncStatusStrategy extends AbstractUpdateStrategy {

	private final URI serviceURI;
	private final Class<? extends Resource> resourceType;
	private final String synchronizationStatus;
	private final String date;
	
	/**
	 * 
	 * @param context
	 * @param serviceURI
	 */
	public PutSyncStatusStrategy(final Context context, final URI serviceURI) {
		super(context);
		this.serviceURI = serviceURI;
		this.resourceType = ResourceFactory.getResourceClass(context.getStringAttribute(RESTConfig.RESOURCE_TYPE_PARAM, ResourceFactory.RESOURCE_CLASS_NAME));
		this.synchronizationStatus = context.getStringAttribute(RESTConfig.SYNC_STATUS, "");
		this.date = context.getStringAttribute(RESTConfig.SYNC_DATE_PARAM, null);
	}

	@Override
	protected void render(final Writer writer, final String resourceID) {
		return;
	}

	@Override
	protected String update() {
		final LogicInterface logic = this.getLogic();
		if (!present(this.synchronizationStatus)) {
			throw new BadRequestOrResponseException("No status given.");
		}
		
		final SynchronizationStatus status = SynchronizationStatus.valueOf(this.synchronizationStatus);
		
		String info = null;
		try {
			/*
			 * FIXME: why do we directly use a reader and not the XML parser?
			 */
			info = new BufferedReader(this.doc).readLine();
		} catch (final IOException ex) {
			throw new BadRequestOrResponseException("Could not read body of request.");
		}
		
		final User authenticatedUser = logic.getAuthenticatedUser();
		final String userName = authenticatedUser.getName();
		
		final Date lastSyncDate = logic.getLastSyncData(userName, this.serviceURI, this.resourceType).getLastSyncDate();
		
		final Date newDate;
		if (present(this.date)) {
			try {
				newDate = RESTConfig.parseDate(this.date);
			} catch (final ParseException e) {
				throw new InvalidModelException("parsing date failed. Date was " + this.date);
			}
		} else {
			newDate = null;
		}
		
		logic.updateSyncData(userName, this.serviceURI, this.resourceType, lastSyncDate, status, info, newDate);
		return null;
	}


}
