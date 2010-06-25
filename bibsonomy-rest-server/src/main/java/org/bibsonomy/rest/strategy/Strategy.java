package org.bibsonomy.rest.strategy;

import java.io.ByteArrayOutputStream;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.renderer.Renderer;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public abstract class Strategy {
	private final LogicInterface logic;
	private final Context context;
	private final Renderer renderer;

	/**
	 * @param context
	 */
	public Strategy(final Context context) {
		this.context = context;
		this.logic = context.getLogic();
		this.renderer = context.getRenderer();
	}

	/**
	 * @see Context#canAccess()
	 */
	public void canAccess() {
		// noop
	}

	/**
	 * @param outStream
	 * @throws InternServerException
	 * @throws NoSuchResourceException
	 * @throws ResourceNotFoundException 
	 * @throws ResourceMovedException 
	 */
	public abstract void perform(final ByteArrayOutputStream outStream) throws InternServerException, NoSuchResourceException, ResourceMovedException, ResourceNotFoundException;

	/**
	 * Get Content type to be set for response, depending on the specified user agent.
	 * 
	 * @param userAgent - 
	 * @return the contentType of the answer document
	 */
	public final String getContentType(final String userAgent) {
		if (getContentType() == null) {
			return this.context.getRenderingFormat().toMimeType();
		}
		// Use special content type if request comes from BibSonomy REST client
		// (like bibsonomy/post+XML )
		if (this.context.apiIsUserAgent(userAgent)) 
			return RestProperties.getInstance().getSystemName().toLowerCase() + "/" + getContentType() + "+" + this.context.getRenderingFormat().toString();
		return this.context.getRenderingFormat().toMimeType();
	}

	protected abstract String getContentType();

	/**
	 * Chooses a GroupingEntity based on the parameterMap in the {@link Context}.
	 * 
	 * @return The GroupingEntity; it defaults to ALL.
	 */
	protected GroupingEntity chooseGroupingEntity() {
		String value = this.context.getStringAttribute("user", null);
		if (value != null) return GroupingEntity.USER;

		value = this.context.getStringAttribute("group", null);
		if (value != null) return GroupingEntity.GROUP;

		value = this.context.getStringAttribute("viewable", null);
		if (value != null) return GroupingEntity.VIEWABLE;

		value = this.context.getStringAttribute("friend", null);
		if (value != null) return GroupingEntity.FRIEND;

		return GroupingEntity.ALL;
	}

	protected LogicInterface getLogic() {
		return this.logic;
	}

	protected Renderer getRenderer() {
		return this.renderer;
	}
}