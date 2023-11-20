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
package org.bibsonomy.webapp.view;

import org.bibsonomy.webapp.util.View;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author rja
 */
public class ExtendedRedirectView extends RedirectView implements View {

	/** Constructs a new redirect view.
	 * 
	 * @param redirectURI
	 */
	public ExtendedRedirectView(final String redirectURI) {
		this(redirectURI, false);
	}
	
	/**
	 * @param redirectURI 
	 * @param permanent 
	 */
	public ExtendedRedirectView(final String redirectURI, final boolean permanent) {
		super(redirectURI);
		this.setExposeModelAttributes(false);
		if (permanent) {
			this.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
		}

		// by default we do not support expanding of uri template vars
		this.setExpandUriTemplateVariables(false);
	}
	
	@Override
	public String getName() {
		return getUrl();
	}

}
