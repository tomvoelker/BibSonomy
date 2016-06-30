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
package tags;

import javax.servlet.jsp.JspTagException;

import org.springframework.beans.NotReadablePropertyException;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.tags.RequestContextAwareTag;
import org.springframework.web.util.ExpressionEvaluationUtils;

/**
 * TODO: move to org.bibsonomy.webapp.util.tags package
 * 
 * The tag checks, if the given (command) path exists and only
 * then executes the content of its body.
 *  
 * @author rja
 */
public class Exists extends RequestContextAwareTag {
	private static final long serialVersionUID = 8378817318583491829L;
	
	
	private String path;
	
	@SuppressWarnings("unused")
	@Override
	protected int doStartTagInternal() throws Exception {
		final String resolvedPath = ExpressionEvaluationUtils.evaluateString("path", this.path, pageContext);

		try {
			new BindStatus(getRequestContext(), resolvedPath, false);
		} catch (final IllegalStateException ex) {
			throw new JspTagException(ex.getMessage());
		} catch (final NotReadablePropertyException ex) {
			/*
			 * property not found, skip body of tag
			 */
			return SKIP_BODY;
		}
		
		return EVAL_BODY_INCLUDE;
	}
	
	/**
	 * Set the path that this tag should apply. Can be a bean (e.g. "person"),
	 * or a bean property (e.g. "person.name"). The tag checks 
	 * 
	 * @param path 
	 * 
	 */
	public void setPath(final String path) {
		this.path = path;
	}
}
