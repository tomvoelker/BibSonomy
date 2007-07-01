package org.bibsonomy.rest.strategy.users;

import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetPostDetailsStrategy extends Strategy {

	private final String userName;
	private final String resourceHash;

	public GetPostDetailsStrategy(final Context context, final String userName, final String resourceHash) {
		super(context);
		this.userName = userName;
		this.resourceHash = resourceHash;
	}

	@Override
	public void perform(final HttpServletRequest request, final Writer writer) throws InternServerException, NoSuchResourceException {
		// delegate to the renderer
		final Post<? extends Resource> post = this.context.getLogic().getPostDetails(this.resourceHash, this.userName);
		if (post == null) {
			throw new NoSuchResourceException("The requested post for the hash '" + this.resourceHash + "' of the requested user '" + this.userName + "' does not exist.");
		}
		this.context.getRenderer().serializePost(writer, post, new ViewModel());
	}

	@Override
	public String getContentType(final String userAgent) {
		if (this.context.apiIsUserAgent(userAgent)) return "bibsonomy/post+" + this.context.getRenderingFormat().toString();
		return RestProperties.getInstance().getContentType();
	}
}