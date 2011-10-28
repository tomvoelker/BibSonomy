package org.bibsonomy.rest.strategy.sync;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.rest.strategy.AbstractDeleteStrategy;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.utils.RestSyncUtils;

/**
 * @author wla
 * @version $Id$
 */
public class DeleteSyncDataStrategy extends AbstractDeleteStrategy {
	
	private static final Log log = LogFactory.getLog(DeleteSyncDataStrategy.class);
	
	private final URI serviceURI;
	
	/**
	 * 
	 * @param context
	 * @param serviceURI
	 */
	public DeleteSyncDataStrategy(final Context context, final URI serviceURI) {
		super(context);
		this.serviceURI = serviceURI;
	}
	
	@Override
	protected boolean delete() {
		final Class<? extends Resource> resourceType = ResourceFactory.getResourceClass(context.getStringAttribute("resourceType", "all"));
		final LogicInterface logic = this.getLogic();		
		
		final String dateString = context.getStringAttribute("date", null);
		
		Date date = null;
		if(present(dateString)) {
			date = RestSyncUtils.parseDate(dateString);
			if (!present(date)) {
				log.error("can't parse date string");
				return false;
			}
		}
		final String userName = logic.getAuthenticatedUser().getName();
		if (BibTex.class.equals(resourceType) || Resource.class.equals(resourceType)) {
			logic.deleteSyncData(userName, serviceURI, BibTex.class, date);
		}
		if(Bookmark.class.equals(resourceType) || Resource.class.equals(resourceType)) {
			logic.deleteSyncData(userName, serviceURI, Bookmark.class, date);
		}
		
		logic.deleteSyncData(userName, serviceURI, resourceType, date);
		return true;
	}



}
