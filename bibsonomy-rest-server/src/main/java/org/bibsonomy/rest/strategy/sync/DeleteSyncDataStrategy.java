package org.bibsonomy.rest.strategy.sync;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;
import java.text.ParseException;

import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.strategy.AbstractDeleteStrategy;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.utils.RestSyncUtils;

/**
 * @author wla
 * @version $Id$
 */
public class DeleteSyncDataStrategy extends AbstractDeleteStrategy {

	private final URI serviceURI;
	private final Class<? extends Resource> resourceType;
	private final String date;

	/**
	 * 
	 * @param context
	 * @param serviceURI
	 */
	public DeleteSyncDataStrategy(final Context context, final URI serviceURI) {
		super(context);
		this.serviceURI = serviceURI;
		this.resourceType = ResourceFactory.getResourceClass(context.getStringAttribute(RESTConfig.RESOURCE_TYPE_PARAM, ResourceFactory.RESOURCE_CLASS_NAME));
		this.date = context.getStringAttribute(RESTConfig.SYNC_DATE_PARAM, null);
	}

	@Override
	protected boolean delete() {
		if (!present(date)) {
			throw new BadRequestOrResponseException("no date given");
		}

		try {
			final LogicInterface logic = this.getLogic();
			logic.deleteSyncData(logic.getAuthenticatedUser().getName(), this.serviceURI, this.resourceType, RestSyncUtils.parseDate(date));
			return true;
		} catch (ParseException ex) {
			throw new BadRequestOrResponseException("the given date '" + date + "' could not be parsed.");
		}
	}
}
