/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.search.es.index.converter.group;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Group;
import org.bibsonomy.search.es.search.util.ElasticsearchIndexSearchUtils;
import org.bibsonomy.search.util.Converter;

/**
 * converter for {@link Group}s
 *
 * @author dzo
 */
public class GroupConverter implements Converter<Group, Map<String, Object>, Object> {

	private static final Log LOG = LogFactory.getLog(GroupConverter.class);

	@Override
	public Map<String, Object> convert(final Group group) {
		final HashMap<String, Object> document = new HashMap<>();
		final URL homepage = group.getHomepage();
		if (present(homepage)) {
			document.put(GroupFields.HOMEPAGE, homepage.toString());
		}

		final String name = group.getName();
		document.put(GroupFields.NAME, name);
		final String realname = group.getRealname();
		document.put(GroupFields.REALNAME, realname);
		document.put(GroupFields.REALNAME_PREFIX, ElasticsearchIndexSearchUtils.getPrefixForString(present(realname) ? realname : name));

		document.put(GroupFields.INTERNAL_ID, group.getInternalId());
		document.put(GroupFields.ORGANIZATION, group.isOrganization());

		final Group parentGroup = group.getParent();
		if (present(parentGroup)) {
			document.put(GroupFields.PARENT_NAME, parentGroup.getName());
		}

		document.put(GroupFields.ALLOW_JOIN, group.isAllowJoin());
		document.put(GroupFields.SHARES_DOCUMENTS, group.isSharedDocuments());

		return document;
	}

	@Override
	public Group convert(final Map<String, Object> source, final Object options) {
		final Group group = new Group();

		group.setRealname((String) source.get(GroupFields.REALNAME));
		group.setName((String) source.get(GroupFields.NAME));
		group.setInternalId((String) source.get(GroupFields.INTERNAL_ID));
		group.setOrganization((Boolean) source.get(GroupFields.ORGANIZATION));
		group.setAllowJoin((Boolean) source.get(GroupFields.ALLOW_JOIN));
		group.setSharedDocuments((Boolean) source.get(GroupFields.SHARES_DOCUMENTS));

		final Object homepage = source.get(GroupFields.HOMEPAGE);
		if (present(homepage)) {
			try {
				group.setHomepage(new URL((String) homepage));
			} catch (MalformedURLException e) {
				LOG.error("error converting group", e);
			}
		}

		if (source.containsKey(GroupFields.PARENT_NAME)) {
			final Group parentGroup = new Group();
			parentGroup.setName((String) source.get(GroupFields.PARENT_NAME));
			group.setParent(parentGroup);
		}

		return group;
	}
}
