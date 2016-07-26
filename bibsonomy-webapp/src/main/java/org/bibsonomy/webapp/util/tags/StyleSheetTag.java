/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
 * if it can't find it, it replaces it the LESS file 
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
