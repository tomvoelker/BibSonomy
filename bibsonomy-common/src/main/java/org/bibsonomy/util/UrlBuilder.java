/**
 *
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
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

import java.net.URI;
import java.net.URISyntaxException;
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
	public UrlBuilder addPathElement(String pathElement) {
		this.pathElements.add(pathElement);
		return this;
	}
	
	/**
	 * @return the url as string
	 */
	public String asString() {
		return this.asURI().toString();
	}
	
	/**
	 * @return the url as URI
	 */
	public URI asURI() {
		final StringBuilder pathBuilder = new StringBuilder();
		for (final String pathElement : this.pathElements) {
			if (present(pathBuilder) && pathBuilder.charAt(pathBuilder.length() - 1) == '/') {
				pathBuilder.setLength(pathBuilder.length() - 1);
			}
			if ((pathElement.length() == 0) || (pathElement.charAt(0)) != '/') {
				pathBuilder.append('/');
			}
			pathBuilder.append(pathElement);
		}
		
		final StringBuilder queryBuilder = new StringBuilder();
		if (present(this.parameters)) {
			final Iterator<Entry<String, String>> entrySetIterator = this.parameters.entrySet().iterator();
			while (entrySetIterator.hasNext()) {
				final Entry<String, String> param = entrySetIterator.next();
				final String key = UrlUtils.safeURIEncode(param.getKey());
				final String value = UrlUtils.safeURIEncode(param.getValue());
				queryBuilder.append(key).append("=").append(value);
				if (entrySetIterator.hasNext()) {
					queryBuilder.append("&");
				}
			}
		}
		try {
			final URI basePathUri = new URI(this.baseUrl);
			/*
			 * to avoid ? at the end of the uri, remove it by setting
			 * the query string to null
			 */
			final String queryString;
			if (present(queryBuilder)) {
				queryString = queryBuilder.toString();
			} else {
				queryString = null;
			}
			return new URI(basePathUri.getScheme(), null, basePathUri.getHost(), -1, pathBuilder.toString(), queryString, null);
		} catch (final URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String toString() {
		return asString();
	}
}
