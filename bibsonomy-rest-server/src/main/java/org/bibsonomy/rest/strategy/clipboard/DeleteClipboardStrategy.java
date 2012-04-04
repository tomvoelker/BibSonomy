package org.bibsonomy.rest.strategy.clipboard;

import java.io.ByteArrayOutputStream;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author wla
 * @version $Id$
 */
public class DeleteClipboardStrategy extends PostClipboardStrategy {

	private final boolean clearClipboard;

	/**
	 * 
	 * @param context
	 * @param userName
	 * @param resourceHash
	 * @param clearClipboard 
	 */
	public DeleteClipboardStrategy(Context context, String userName, String resourceHash, boolean clearClipboard) {
		super(context, userName, resourceHash);
		this.clearClipboard = clearClipboard;
	}

	@Override
	public void perform(ByteArrayOutputStream outStream) throws InternServerException, NoSuchResourceException, ResourceMovedException, ResourceNotFoundException {
		this.getLogic().deleteBasketItems(createPost(resourceHash, userName), clearClipboard);
		this.getRenderer().serializeOK(this.writer);
	}

}
