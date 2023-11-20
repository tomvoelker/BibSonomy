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
package org.bibsonomy.webapp.util.spring.factorybeans;

import java.util.Arrays;

import org.bibsonomy.services.filesystem.extension.ExtensionChecker;
import org.bibsonomy.services.filesystem.extension.ListExtensionChecker;
import org.bibsonomy.services.filesystem.extension.WildcardExtensionChecker;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author dzo
 */
public class ExtensionCheckerFactoryBean implements FactoryBean<ExtensionChecker>{
	
	private String allowedExtensions;
	
	@Override
	public ExtensionChecker getObject() throws Exception {
		this.allowedExtensions = this.allowedExtensions.trim();
		
		if (WildcardExtensionChecker.WILDCARD.equals(this.allowedExtensions)) {
			return new WildcardExtensionChecker();
		}
		
		return new ListExtensionChecker(Arrays.asList(this.allowedExtensions.split(", ")));
	}

	@Override
	public Class<?> getObjectType() {
		return ExtensionChecker.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	/**
	 * @param allowedExtensions the allowedExtensions to set
	 */
	public void setAllowedExtensions(String allowedExtensions) {
		this.allowedExtensions = allowedExtensions;
	}

}
