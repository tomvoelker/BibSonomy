package org.bibsonomy.rest.strategy.users;

import java.io.ByteArrayOutputStream;
import java.io.Writer;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;
import org.bibsonomy.rest.util.EscapingPrintWriter;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetPostDetailsStrategy extends Strategy {

	private final String userName;
	private final String resourceHash;
	private Writer writer;

	/**
	 * @param context
	 * @param userName
	 * @param resourceHash
	 */
	public GetPostDetailsStrategy(final Context context, final String userName, final String resourceHash) {
		super(context);
		this.userName = userName;
		this.resourceHash = resourceHash;
	}

	@Override
	public void perform(final ByteArrayOutputStream outStream) throws InternServerException, NoSuchResourceException, ResourceMovedException, ResourceNotFoundException {
		writer = new EscapingPrintWriter(outStream);
		// delegate to the renderer
		final Post<? extends Resource> post = this.getLogic().getPostDetails(this.resourceHash, this.userName);
		if (post == null) {
			throw new NoSuchResourceException("The requested post for the hash '" + this.resourceHash + "' of the requested user '" + this.userName + "' does not exist.");
		}
		this.getRenderer().serializePost(writer, post, new ViewModel());
	}

	@Override
	public String getContentType() {
		return "post";
	}
}