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
package org.bibsonomy.webapp.command;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * @author mwa
 * 
 * With this command we are able to receive multiple lists of posts for a resource. 
 * 
 */
@Getter
@Setter
public class MultiResourceViewCommand extends ResourceViewCommand {

	/** a list of bibtex lists **/
	private final List<ListCommand<Post<BibTex>>> listsBibTeX = new ArrayList<ListCommand<Post<BibTex>>>();

	/** a list of bookmark lists**/
	private final List<ListCommand<Post<Bookmark>>> listsBookmark = new ArrayList<ListCommand<Post<Bookmark>>>();

	/** description for a bibtex list **/
	private List<String> listsBibTeXDescription = new ArrayList<String>();

	/** description for a bookmark list **/
	private List<String> listsBookmarkDescription = new ArrayList<String>();

	
	/**
	 * @param <T> type of the entities in the list
	 * @param resourceType type of the entities in the list
	 * @return the list with entities of type resourceType
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T extends Resource> List<ListCommand<Post<T>>> getListCommand(final Class<T> resourceType) {
		if (resourceType == BibTex.class) {
			return (List) getListsBibTeX();
		} else if (resourceType == Bookmark.class) {
			return (List) getListsBookmark();
		}
		throw new UnsupportedResourceTypeException(resourceType.getName());
	}
	
	/**
	 * @param <T> type of the entities in the list
	 * @param resourceType type of the entities in the list
	 * @return the list with entities of type resourceType
	 */
	public <T extends Resource> List<String> getListsDescription(final Class<T> resourceType) {
		if (resourceType == BibTex.class) {
			return this.getListsBibTeXDescription();
		} else if (resourceType == Bookmark.class) {
			return this.getListsBookmarkDescription();
		}
		throw new UnsupportedResourceTypeException(resourceType.getName());
	}

}
