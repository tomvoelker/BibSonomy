package org.bibsonomy.rest.strategy.concepts;

import java.io.ByteArrayOutputStream;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * Handles a global concept details request
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class GetConceptDetailsStrategy extends Strategy {

	private final String conceptName;
	
	/**
	 * @param context
	 * @param conceptName
	 */
	public GetConceptDetailsStrategy(final Context context, final String conceptName) {
		super(context);
		this.conceptName = conceptName;
	}

	@Override
	public void perform(final ByteArrayOutputStream outStream) throws InternServerException, NoSuchResourceException {
		final Tag concept = this.getLogic().getConceptDetails(conceptName, GroupingEntity.ALL, null);
		if (concept == null) {
			throw new NoSuchResourceException("The requested concept '" + conceptName + "' does not exist.");
		}
		this.getRenderer().serializeTag(writer, concept, new ViewModel());
	}
	
	@Override
	protected String getContentType() {
		return "tag";
	}
}