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
package org.bibsonomy.webapp.command.resource;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.model.DiscussionItem;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.TagResourceViewCommand;

/**
 * Command for a page that handles a single resource
 *
 * @author dzo
 *
 * @param <R> the resource
 */
@Setter
@Getter
public class ResourcePageCommand<R extends Resource> extends TagResourceViewCommand {
	private String requestedHash;

	private Map<String, List<String>> copyUsersMap;

	/** sets the post of the loggedin user */
	private Post<R> postOfLoggedInUser;

	/** the discussion items of the reosurce */
	private List<DiscussionItem> discussionItems;

	private String postOwner;

	@Deprecated // FIXME: currently unused
	private String intraHash;

	private Class<R> resourceClass;
	
	private String requestedTitle;

}
