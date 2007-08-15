package org.bibsonomy.rest.strategy;

import java.io.Writer;

import org.bibsonomy.common.exceptions.InternServerException;

/**
 * @author Dominik Benz
 * @version $Id$
 */
public abstract class AbstractDeleteStrategy extends Strategy {
	
	public AbstractDeleteStrategy(final Context context) {
		super(context);
	}

	@Override
	public final void perform(final Writer writer) throws InternServerException {
		final boolean deleted = delete();
		if (deleted == true)
			this.getRenderer().serializeOK(writer);
		else 
			this.getRenderer().serializeFail(writer);
	}

	protected abstract boolean delete();
}