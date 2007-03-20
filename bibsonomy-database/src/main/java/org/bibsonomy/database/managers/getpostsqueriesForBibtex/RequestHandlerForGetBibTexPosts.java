
package org.bibsonomy.database.managers.getpostsqueriesForBibtex;


import org.bibsonomy.database.managers.BibTexDatabaseManager;
import org.bibsonomy.database.managers.RequestHandlerForGetPosts;

/*******
* 
* @author mgr
*
**/

public abstract class RequestHandlerForGetBibTexPosts extends RequestHandlerForGetPosts{
	protected final BibTexDatabaseManager db = BibTexDatabaseManager.getInstance();
    
}