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
package org.bibsonomy.webapp.util.tags;

import java.net.URL;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.servlet.tags.RequestContextAwareTag;


/**
 * This tag looks for the specified CSS file {@link #path}
 * if it can't find it, it replaces it with the LESS file
 * with the same name (for development usage)
 * 
 * @author dzo
 */
public class StyleSheetTag extends RequestContextAwareTag {
	private static final long serialVersionUID = 3240785013748446953L;
	
	private String path;
	
	@Override
	protected int doStartTagInternal() throws Exception {
		final String styleSheetPath;
		
		final URL resource = this.pageContext.getServletContext().getResource(this.path);
		/*
		 * if css file does not exists use the less file
		 */
		if (resource != null) {
			styleSheetPath = this.path;
		} else {
			final String fullPath = this.path.substring(0, this.path.lastIndexOf("/") + 1);
			final String fileNameWithoutExtension = FilenameUtils.getBaseName(this.path);
			styleSheetPath = fullPath + fileNameWithoutExtension + ".less";
		}
		
		this.pageContext.getOut().print("<link rel=\"stylesheet\" type=\"text/" + FilenameUtils.getExtension(styleSheetPath) + "\" href=\"" + styleSheetPath + "\" />");
		return SKIP_BODY;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}
}
