package org.bibsonomy.rest.strategy.users;


import java.io.ByteArrayOutputStream;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * Handle user concept request
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class GetUserConceptStrategy extends Strategy {

	private final String conceptName; 
	private final String userName;	

	/**
	 * @param context -  the context
	 * @param conceptName - the name of the supertag to retrieve the subtags for
	 * @param userName - the owner of the concept
	 */
	public GetUserConceptStrategy(final Context context, final String conceptName, final String userName) {
		super(context);
		this.conceptName = conceptName;
		this.userName = userName;
	}

	@Override
	public void perform(final ByteArrayOutputStream outStream) throws InternServerException, NoSuchResourceException {
		final Tag concept = this.getLogic().getConceptDetails(this.conceptName, GroupingEntity.USER, userName);
		if (concept == null) {
			throw new NoSuchResourceException("The requested concept '" + conceptName + "' does not exist for user '" + userName + "'.");
		}		
		this.getRenderer().serializeTag(writer, concept, new ViewModel());		
	}
	
	@Override
	protected String getContentType() {
		return "tag";
	}
}