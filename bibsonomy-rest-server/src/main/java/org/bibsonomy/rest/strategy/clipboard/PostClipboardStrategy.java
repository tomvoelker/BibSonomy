package org.bibsonomy.rest.strategy.clipboard;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author wla
 * @version $Id$
 */
public class PostClipboardStrategy extends Strategy {

	protected final String resourceHash;
	protected final String userName;

	/**
	 * 
	 * @param context
	 * @param userName of post owner
	 * @param resourceHash of post
	 */
	public PostClipboardStrategy(Context context, String userName, String resourceHash) {
		super(context);
		if (present(resourceHash) && resourceHash.length() == 33) {
			this.resourceHash = resourceHash.substring(1);
		} else {
			this.resourceHash = resourceHash;
		}
		this.userName = userName;
	}

	@Override
	public void perform(ByteArrayOutputStream outStream) throws InternServerException, NoSuchResourceException, ResourceMovedException, ResourceNotFoundException {
		this.getLogic().createBasketItems(createPost(resourceHash, userName));
		this.getRenderer().serializeOK(this.writer);
	}

	/**
	 * 
	 * Creates a new Collections.singletonList with (empty) post with the given username and resourcehash.
	 * 
	 * @param resourceHash
	 * @param userName
	 * @return
	 */
	protected List<Post<? extends Resource>> createPost(final String resourceHash, final String userName) {
		final Post<BibTex> post = new Post<BibTex>();
		final BibTex publication = new BibTex();

		publication.setIntraHash(resourceHash);
		post.setResource(publication);
		post.setUser(new User(userName));
		return Collections.<Post<? extends Resource>> singletonList(post);
	}

}
