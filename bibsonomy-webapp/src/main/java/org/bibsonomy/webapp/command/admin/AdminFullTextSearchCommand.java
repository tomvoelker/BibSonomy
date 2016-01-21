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
package org.bibsonomy.webapp.command.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bibsonomy.model.Resource;
import org.bibsonomy.search.model.SearchIndexInfo;
import org.bibsonomy.webapp.command.BaseCommand;


/**
 * Command bean for admin page 
 * 
 * @author Sven Stefani
 * @author bsc
 */
public class AdminFullTextSearchCommand extends BaseCommand {
	
	/**
	 * all actions for a full text index
	 * @author dzo
	 */
	public enum AdminFullTextAction {
		/** regenerate an index = generate new and delete the specified index */
		REGENERATE_INDEX,
		/** generate index */
		GENERATE_INDEX,
		/** delete index */
		DELETE_INDEX;
	}
	
	/** specific action for admin page */
	private AdminFullTextAction action;
	
	/** the specific index id for the indexd to be updated **/
	private String id;
	
	/** the resource class to handle */
	private Class<? extends Resource> resource;
	
	private final Map<String, List<SearchIndexInfo>> searchIndexInfo = new HashMap<>();
	
	/**
	 * @return the action
	 */
	public AdminFullTextAction getAction() {
		return this.action;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(final AdminFullTextAction action) {
		this.action = action;
	}
	
	/**
	 * @return the resource
	 */
	public Class<? extends Resource> getResource() {
		return this.resource;
	}

	/**
	 * @param resource the resource to set
	 */
	public void setResource(Class<? extends Resource> resource) {
		this.resource = resource;
	}

	/**
	 * @return the searchIndexInfo
	 */
	public Map<String, List<SearchIndexInfo>> getSearchIndexInfo() {
		return this.searchIndexInfo;
	}
}