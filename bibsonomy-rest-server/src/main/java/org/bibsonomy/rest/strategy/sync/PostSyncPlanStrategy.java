/**
 * BibSonomy-Rest-Server - The REST-server.
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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest.strategy.sync;

import java.io.ByteArrayOutputStream;
import java.io.Writer;
import java.net.URI;
import java.util.List;

import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.sync.ConflictResolutionStrategy;
import org.bibsonomy.model.sync.SynchronizationDirection;
import org.bibsonomy.model.sync.SynchronizationPost;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.strategy.AbstractCreateStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author wla
 */
public class PostSyncPlanStrategy extends AbstractCreateStrategy {

	private final URI serviceURI;
	private final Class<? extends Resource> resourceType;
	private final ConflictResolutionStrategy strategy;
	private final SynchronizationDirection direction;
	private List<SynchronizationPost> syncPlan;
	
	/**
	 * @param context
	 * @param serviceURI 
	 */
	public PostSyncPlanStrategy(final Context context, final URI serviceURI) {
		super(context);
		this.serviceURI = serviceURI;
		this.resourceType = ResourceFactory.getResourceClass(context.getStringAttribute(RESTConfig.RESOURCE_TYPE_PARAM, ResourceFactory.RESOURCE_CLASS_NAME));
		this.strategy = ConflictResolutionStrategy.getConflictResolutionStrategyByString(context.getStringAttribute(RESTConfig.SYNC_STRATEGY_PARAM, ConflictResolutionStrategy.LAST_WINS.getConflictResolutionStrategy()));
		this.direction = SynchronizationDirection.getSynchronizationDirectionByString(context.getStringAttribute(RESTConfig.SYNC_DIRECTION_PARAM, SynchronizationDirection.BOTH.getSynchronizationDirection()));
	}

	
	/**
	 * Since {@link AbstractCreateStrategy} does not allow to overwrite {@link #perform(ByteArrayOutputStream)}, 
	 * we store the syncplan in this strategy and render it when {@link #render(Writer, String)} 
	 * is called. 
	 * 
	 * @see org.bibsonomy.rest.strategy.AbstractCreateStrategy#create()
	 */
	@Override
	protected String create() {
		final LogicInterface logic = this.getLogic();
		/*
		 * we first parse the incoming posts ...
		 */
		final List<SynchronizationPost> clientPosts = this.getRenderer().parseSynchronizationPostList(this.doc);
		/*
		 * ... and then calculate the syncplan (which we store to later render it)
		 */
		this.syncPlan = logic.getSyncPlan(logic.getAuthenticatedUser().getName(), this.serviceURI, this.resourceType, clientPosts, this.strategy, this.direction);
		/*
		 * ... which is then rendered in render().
		 * The returned result is a dummy which is not used by render. 
		 */
		return "";
	}

	@Override
	protected void render(Writer writer, String resourceID) {
		this.getRenderer().serializeSynchronizationPosts(this.writer, this.syncPlan);
	}


}
