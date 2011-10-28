package org.bibsonomy.rest.strategy.sync;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.List;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.sync.ConflictResolutionStrategy;
import org.bibsonomy.model.sync.SynchronizationDirection;
import org.bibsonomy.model.sync.SynchronizationPost;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author wla
 * @version $Id$
 */
public class PostSyncPlanStrategy extends Strategy{

	final URI serviceURI;
	
	/**
	 * @param context
	 * @param serviceURI 
	 */
	public PostSyncPlanStrategy(Context context, URI serviceURI) {
		super(context);
		this.serviceURI = serviceURI;
	}

	@Override
	public void perform(ByteArrayOutputStream outStream) throws InternServerException, NoSuchResourceException, ResourceMovedException, ResourceNotFoundException {
		Class<? extends Resource> resourceType = ResourceFactory.getResourceClass(context.getStringAttribute("resourceType", "all"));
		ConflictResolutionStrategy strategy = ConflictResolutionStrategy.getConflictResolutionStrategyByString(context.getStringAttribute("strategy", "lw"));
		SynchronizationDirection direction = SynchronizationDirection.getSynchronizationDirectionByString(context.getStringAttribute("direction", "both"));
		LogicInterface logic = this.getLogic();
		
		List<SynchronizationPost> clientPosts = this.getRenderer().parseSynchronizationPostList(context.getDocument());
		
		this.getRenderer().serializeSynchronizationPosts(writer, logic.getSyncPlan(logic.getAuthenticatedUser().getName(), serviceURI, resourceType, clientPosts, strategy, direction));
	}


}
