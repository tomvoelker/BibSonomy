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
package org.bibsonomy.webapp.command.admin;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.BaseCommand;


/**
 * Command bean for admin page 
 * 
 * @author Sven Stefani
 * @author bsc
 */
public class AdminFullTextSearchCommand extends BaseCommand {
	/** specific action for admin page */
	private String action;
	
	/** the specific index id for the indexd to be updated **/
	private int id;
	
	/** the resource class to handle */
	private Class<? extends Resource> resource;
	
	/** the string response for the admin */
	private String adminResponse = "";
	
	
	
	
	private final List<LuceneResourceIndicesInfoContainer> indicesInfos = new LinkedList<LuceneResourceIndicesInfoContainer>();
	
	// TODO: more generic (map<Resource.class, ContainerInfo>)
	private final List<LuceneResourceIndicesInfoContainer> esIndicesInfosBibtex = new LinkedList<LuceneResourceIndicesInfoContainer>();
	private final List<LuceneResourceIndicesInfoContainer> esIndicesInfosBookmark = new LinkedList<LuceneResourceIndicesInfoContainer>();
	private final List<LuceneResourceIndicesInfoContainer> esIndicesInfosGoldStandard = new LinkedList<LuceneResourceIndicesInfoContainer>();

	/**
	 * @return the action
	 */
	public String getAction() {
		return this.action;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(final String action) {
		this.action = action;
	}

	/**
	 * @return the list of indices
	 */
	public List<LuceneResourceIndicesInfoContainer> getIndicesInfos() {
		return indicesInfos;
	}

	/**
	 * @param adminResponse
	 */
	public void setAdminResponse(final String adminResponse) {
		this.adminResponse = adminResponse;
	}

	/**
	 * @return the admin response
	 */
	public String getAdminResponse() {
		return adminResponse;
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
	 * @return the esIndicesInfosBibtex
	 */
	public List<LuceneResourceIndicesInfoContainer> getEsIndicesInfosBibtex() {
		return this.esIndicesInfosBibtex;
	}

	/**
	 * @return the esIndicesInfosBookmark
	 */
	public List<LuceneResourceIndicesInfoContainer> getEsIndicesInfosBookmark() {
		return this.esIndicesInfosBookmark;
	}

	/**
	 * @return the esIndicesInfosGoldStandard
	 */
	public List<LuceneResourceIndicesInfoContainer> getEsIndicesInfosGoldStandard() {
		return this.esIndicesInfosGoldStandard;
	}
}