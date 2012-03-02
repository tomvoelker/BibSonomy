/**
 *
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
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

package org.bibsonomy.util;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author dzo
 * @version $Id$
 */
public class UrlBuilder {
	private final String baseUrl;
	private Map<String, String> parameters;
	
	/**
	 * @param baseUrl
	 */
	public UrlBuilder(final String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	/**
	 * @param key
	 * @param value
	 * @return the builder
	 */
	public UrlBuilder addParameter(final String key, final String value) {
		if (present(key) && present(value)) {
			if (this.parameters == null) {
				this.parameters = new HashMap<String, String>();
			}
			this.parameters.put(key, UrlUtils.safeURIEncode(value));
		}
		
		return this;
	}
	
	/**
	 * clears the parameters for reusing the builder
	 * @return the builder
	 */
	public UrlBuilder clearParameters() {
		if (present(this.parameters)) {
			this.parameters.clear();
		}
		
		return this;
	}
	
	/**
	 * @return the url as string
	 */
	public String asString() {
		final StringBuilder url = new StringBuilder(this.baseUrl);
		
		if (present(this.parameters)) {
			url.append("?");
			for (Entry<String, String> param : this.parameters.entrySet()) {
				url.append(param.getKey()).append("=").append(param.getValue());
				url.append("&");
			}
			/*
			 * remove the last &
			 */
			url.substring(0, url.length() - 1);
		}
		
		return url.toString();
	}
}
