/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
package org.bibsonomy.webapp.util.spring.security.saml.metadata;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.saml.metadata.MetadataGenerator;
import org.springframework.security.saml.metadata.MetadataGeneratorFilter;

/**
 * @author jensi
 */
public class HttpsMetadataGeneratorFilter extends MetadataGeneratorFilter {

	private boolean useHttps;
	private boolean removeTrailingBaseUrlSlash;

	/**
	 * delegates to parent class
	 * @param generator
	 */
	public HttpsMetadataGeneratorFilter(MetadataGenerator generator) {
		super(generator);
	}
	
	@Override
	public void afterPropertiesSet() throws ServletException {
		String s = generator.getEntityBaseURL();
		if (removeTrailingBaseUrlSlash) {
			if (s.endsWith("/")) {
				s = s.substring(0, s.length() - 1);
			}
		}
		s = convertToHttpsIfRequired(s);
		generator.setEntityBaseURL(s);
		super.afterPropertiesSet();
	}
	
	@Override
	protected String getDefaultBaseURL(HttpServletRequest request) {
		String s = super.getDefaultBaseURL(request);
		return convertToHttpsIfRequired(s);
	}

	protected String convertToHttpsIfRequired(String s) {
		if (useHttps == false) {
			return s;
		}
		s = s.replace("http://", "https://");
		if (s.endsWith(":80")) {
			s = s.replace(":80", "");
		} else if (s.endsWith(":8080")) {
			s = s.replace(":8080", ":8443");
		}
		return s;
	}

	/**
	 * @return the useHttps
	 */
	public boolean isUseHttps() {
		return this.useHttps;
	}

	/**
	 * @param useHttps the useHttps to set
	 */
	public void setUseHttps(boolean useHttps) {
		this.useHttps = useHttps;
	}

	/**
	 * @param removeTrailingBaseUrlSlash the removeTrailingBaseUrlSlash to set
	 */
	public void setRemoveTrailingBaseUrlSlash(boolean removeTrailingBaseUrlSlash) {
		this.removeTrailingBaseUrlSlash = removeTrailingBaseUrlSlash;
	}

}
