/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.util;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author dzo
 */
public class UrlBuilder {
	private final String baseUrl;
	private Map<String, String> parameters;
	private final List<String> pathElements = new ArrayList<String>(4);
	
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
			this.parameters.put(key, value);
		}
		
		return this;
	}
	
	/**
	 * Removes all parameters whose name is not contained in the given collection
	 * @param allowedParams the set of allowed parameter names or null (in that case the method does nothing)
	 * @return this object
	 */
	public UrlBuilder clearParamsRetaining(Collection<String> allowedParams) {
		if (allowedParams == null) {
			return this;
		}
		for (Iterator<Entry<String, String>> it = parameters.entrySet().iterator(); it.hasNext(); ) {
			if (!allowedParams.contains(it.next().getKey())) {
				it.remove();
			}
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
	 * @param pathElement part between two '/'es
	 * @return this object
	 */
	public UrlBuilder addPathElement(final String pathElement) {
		if (present(pathElement)) {
			this.pathElements.add(pathElement);
		}
		return this;
	}
	
	/**
	 * @return the url as string
	 */
	public String asString() {
		final StringBuilder url = new StringBuilder(this.baseUrl);
		
		for (final String pathElement : this.pathElements) {
			if (present(url) && url.charAt(url.length() - 1) == '/') {
				url.setLength(url.length() - 1);
			}
			if ((pathElement.length() == 0) || (pathElement.charAt(0)) != '/') {
				url.append('/');
			}
			
			/*
			 * FIXME: replacing all + to %20; UrlUtils.safeURIEncode encodes the
			 * content for x-www-form-urlencoded which is not what we really want
			 */
			final String encodedPathElement = UrlUtils.safeURIEncode(pathElement).replaceAll("\\+", "%20");
			url.append(encodedPathElement);
		}
		
		if (present(this.parameters)) {
			url.append("?");
			for (Entry<String, String> param : this.parameters.entrySet()) {
				String key = UrlUtils.safeURIEncode(param.getKey());
				String value = UrlUtils.safeURIEncode(param.getValue());
				url.append(key).append("=").append(value);
				url.append("&");
			}
			/*
			 * remove the last &
			 */
			return url.substring(0, url.length() - 1);
		}
		
		return url.toString();
	}
	
	@Override
	public String toString() {
		return asString();
	}
}
