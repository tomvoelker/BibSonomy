/**
 * BibSonomy-Database - Database for BibSonomy.
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
package org.bibsonomy.database.managers.chain.resource;

import java.util.List;

import org.bibsonomy.database.managers.PostDatabaseManager;
import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.params.ResourceParam;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.services.searcher.ResourceSearch;

/**
 * 
 * @param <R> the resource of the chain element
 * @param <P> the param of the chain element
 * 
 * @author dzo
 */
public abstract class ResourceChainElement<R extends Resource, P extends ResourceParam<R>> extends ChainElement<List<Post<R>>, P> {
	
	protected PostDatabaseManager<R, P> databaseManager;
	/** instance of the lucene searcher */
	protected ResourceSearch<R> resourceSearch;	
	/**
	 * @param databaseManager the databaseManager to set
	 */
	public void setDatabaseManager(final PostDatabaseManager<R, P> databaseManager) {
		this.databaseManager = databaseManager;
	}

	/**
	 * @param resourceSearch the resourceSearch to set
	 */
	public void setResourceSearch(ResourceSearch<R> resourceSearch) {
		this.resourceSearch = resourceSearch;
	}
}
