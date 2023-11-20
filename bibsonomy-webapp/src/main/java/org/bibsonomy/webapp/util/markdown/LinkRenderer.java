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
package org.bibsonomy.webapp.util.markdown;

import org.bibsonomy.search.es.help.HelpUtils;
import org.bibsonomy.services.URLGenerator;
import org.pegdown.ast.ExpLinkNode;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * replace the link variable with our replacement
 *
 * @author dzo
 */
public class LinkRenderer extends org.pegdown.LinkRenderer {
	
	private final String projectHome;
	private final URLGenerator urlGenerator;
	private final String language;
	
	/**
	 * @param projectHome
	 * @param urlGenerator
	 * @param language
	 */
	public LinkRenderer(final String projectHome, final URLGenerator urlGenerator, final String language) {
		this.projectHome = projectHome;
		this.urlGenerator = urlGenerator;
		this.language = language;
	}
	
	/* (non-Javadoc)
	 * @see org.pegdown.LinkRenderer#render(org.pegdown.ast.ExpLinkNode, java.lang.String)
	 */
	@Override
	public Rendering render(ExpLinkNode node, String text) {
		String url = node.url.replace("${" + HelpUtils.PROJECT_HOME + "}", this.projectHome);

		try {
			final URI uri = new URI(url);
			if (!uri.isAbsolute()) {
				url = this.urlGenerator.getHelpPage(url, this.language);
			}
		} catch (final URISyntaxException e) {
			// ignore
		}

		return new Rendering(url, text);
	}
}
