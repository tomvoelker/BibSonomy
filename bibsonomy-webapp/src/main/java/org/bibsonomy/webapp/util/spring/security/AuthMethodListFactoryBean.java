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
package org.bibsonomy.webapp.util.spring.security;

import java.util.List;

import org.bibsonomy.common.enums.AuthMethod;
import org.springframework.beans.factory.FactoryBean;

/**
 * TODO: better way to get a list of {@link AuthMethod}s
 * 
 * @author dzo
 */
public class AuthMethodListFactoryBean implements FactoryBean<List<AuthMethod>> {

	private List<AuthMethod> authConfig;

	@Override
	public List<AuthMethod> getObject() throws Exception {
		return this.authConfig;
	}

	@Override
	public Class<?> getObjectType() {
		return List.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	/**
	 * @param authConfig the authConfig to set
	 */
	public void setAuthConfig(final List<AuthMethod> authConfig) {
		this.authConfig = authConfig;
	}
}
