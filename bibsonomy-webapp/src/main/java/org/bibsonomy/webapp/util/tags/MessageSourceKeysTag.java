/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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

import java.util.Collection;
import java.util.Locale;

import javax.servlet.jsp.PageContext;

import org.bibsonomy.webapp.util.spring.i18n.ExposedResourceMessageBundleSource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

/**
 * TODO: add documentation to this class
 *
 * @author dzo
 */
public class MessageSourceKeysTag extends RequestContextAwareTag {
	private static final long serialVersionUID = 8814519023061404076L;
	
	private Locale locale;
	private String var;

	private int scope = PageContext.PAGE_SCOPE;
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.tags.RequestContextAwareTag#doStartTagInternal()
	 */
	@Override
	protected int doStartTagInternal() throws Exception {
		final RequestContext requestContext = this.getRequestContext();
		final WebApplicationContext context = requestContext.getWebApplicationContext();
		final ExposedResourceMessageBundleSource messageSource = context.getBean(ExposedResourceMessageBundleSource.class);
		final Collection<Object> keys = messageSource.getAllMessageKeys(this.locale);
		this.pageContext.setAttribute(this.var, keys, this.scope);
		return 0;
	}

	/**
	 * @param locale the locale to set
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/**
	 * @param var the var to set
	 */
	public void setVar(String var) {
		this.var = var;
	}

	/**
	 * @param scope the scope to set
	 */
	public void setScope(int scope) {
		this.scope = scope;
	}
}
