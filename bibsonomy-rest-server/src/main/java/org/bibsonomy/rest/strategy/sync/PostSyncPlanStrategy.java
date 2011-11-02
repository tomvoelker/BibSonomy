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
import org.bibsonomy.rest.strategy.AbstractCreateStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author wla
 * @version $Id$
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
		this.resourceType = ResourceFactory.getResourceClass(context.getStringAttribute("resourceType", "all"));
		this.strategy = ConflictResolutionStrategy.getConflictResolutionStrategyByString(context.getStringAttribute("strategy", "lw"));
		this.direction = SynchronizationDirection.getSynchronizationDirectionByString(context.getStringAttribute("direction", "both"));
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
