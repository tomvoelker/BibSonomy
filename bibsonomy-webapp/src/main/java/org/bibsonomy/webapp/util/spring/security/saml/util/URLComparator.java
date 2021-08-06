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
package org.bibsonomy.webapp.util.spring.security.saml.util;

import org.opensaml.common.binding.decoding.BasicURLComparator;

/**
 * This removes the query params of the url before comparing it
 *
 * code copy of org.springframework.security.saml.util.DefaultURLComparator
 * please remove as soon as we updated the spring security saml dependency
 *
 * @author dzo
 */
@Deprecated // code copy, update saml dependency
public class URLComparator extends BasicURLComparator {

	@Override
	public boolean compare(String uri1, String uri2) {
		if (uri2 == null){
			return uri1 == null;
		}
		int queryStringIndex = uri2.indexOf('?');
		if (queryStringIndex >= 0){
			uri2 = uri2.substring(0, queryStringIndex);
		}

		return super.compare(uri1, uri2);
	}
}
