/**
 *
 *  BibSonomy-Model - Java- and JAXB-Model.
 *
 *  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.model.sync.util;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.sync.SynchronizationData;

/**
 * @author dzo
 * @version $Id$
 */
public final class SynchronizationUtils {
	private static final Log log = LogFactory.getLog(SynchronizationUtils.class);
	
	/**
	 * To allow multiple instances for one client
	 * we introduced the special client scheme.
	 */
	public static final String CLIENT_SPECIAL_SCHEME = "client";

	private SynchronizationUtils() {}

	/**
	 * @param uri the uri to check
	 * @return <code>true</code> iff uri is a client uri
	 */
	public static boolean isClientURI(final URI uri) {
		return uri != null && CLIENT_SPECIAL_SCHEME.equals(uri.getScheme());
	}

	/**
	 * 
	 * @param service the uri of the service
	 * @return	the synchronizationData
	 */
	public static SynchronizationData buildSynchronizationDataforService(final URI service) {
		final SynchronizationData data = new SynchronizationData();
		if (!isClientURI(service)) {
			log.info("create sync data for service without device id");
			data.setService(service);
			data.setDeviceId(""); // other services get an empty string as device id
			return data;
		}
		log.info("create sync data for service with device id");
		
		
		/*
		 * e.g. Android App, iOS App, Texlipse Plugin, …
		 * TODO: improve extract query params
		 */
		final String deviceId = service.getPath().substring(1);
		data.setDeviceId(deviceId);
		final String query = service.getQuery();
		final String deviceInfo;
		if (present(query)) {
			final String[] split = query.split("=");
			deviceInfo = split[1];
		} else {
			deviceInfo = "unknown";
		}
		
		data.setDeviceInfo(deviceInfo);
		
		try {
			data.setService(new URI(service.getScheme(), service.getHost(), null, null));
		} catch (final URISyntaxException ex) {
			/*
			 * scheme and host from a valid uri must be valid to
			 */
			throw new RuntimeException(ex);
		}
		
		return data;
	}	
}
