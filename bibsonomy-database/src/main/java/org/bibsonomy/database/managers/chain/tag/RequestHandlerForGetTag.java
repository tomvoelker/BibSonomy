package org.bibsonomy.database.managers.chain.tag;

import java.util.List;
import org.bibsonomy.model.Tag;

/**
 * @author mgr
 */

public abstract class RequestHandlerForGetTag{

	private RequestHandlerForGetTag next;


	public void setNext(RequestHandlerForGetTag nextHandler) {
		this.next = nextHandler;
	}

	public List<Tag> perform(String authUser, String resourceHash, String currUser) {
		if (this.canHandle(authUser, resourceHash, currUser)) {
			return this.handleRequestForGetTag(authUser, resourceHash, currUser);
		} else {
			if (next != null) {
				return next.perform(authUser, resourceHash, currUser);
			}
		}
		
		return null;
	}

	protected abstract List<Tag> handleRequestForGetTag(String authUser, String resourceHash, String currUser);

	protected abstract boolean canHandle(String authUser, String resourceHash, String currUser);
}