package org.bibsonomy.database.managers.getpostqueries;

import org.bibsonomy.database.managers.DatabaseManager;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

public abstract class RequestHandlerForGetPost {

	private RequestHandlerForGetPost next;
	protected final DatabaseManager db = new DatabaseManager();
	
	public void setNext( RequestHandlerForGetPost nextHandler )
	{
		this.next = nextHandler;
	}
	
	public Post<? extends Resource> perform(String authUser, String resourceHash, String currUser)
	{
		if( this.canHandle(authUser, resourceHash, currUser))
		{
			return this.handleRequestForGetPost(authUser, resourceHash, currUser);
		}
		else
		{
			if( next != null )
			{
				return next.perform(authUser, resourceHash, currUser);
			}
		}
		return null;
	}
	
	protected abstract Post<? extends Resource> handleRequestForGetPost(String authUser, String resourceHash, String currUser);

	protected abstract boolean canHandle(String authUser, String resourceHash, String currUser);

}
