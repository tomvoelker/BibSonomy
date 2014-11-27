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
package servlets.utils;

import java.util.Properties;

import javax.servlet.ServletContext;

import org.springframework.web.context.ServletContextAware;

/**
 * TODO: remove after all sites are ported to the spring mvc
 * 
 * @author dzo
 */
@Deprecated
public class ProperiesAttributeSetter implements ServletContextAware {
	
	private ServletContext context;
	private Properties properties;

	@Override
	public void setServletContext(final ServletContext servletContext) {
		this.context = servletContext;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(final Properties properties) {
		this.properties = properties;
	}
	
	/**
	 * sets the properties as context attribute
	 */
	public void init() {
		this.context.setAttribute("properties", this.properties);
	}
}
