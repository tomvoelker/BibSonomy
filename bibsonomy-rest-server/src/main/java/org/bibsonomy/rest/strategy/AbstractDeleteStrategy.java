package org.bibsonomy.rest.strategy;


import java.io.ByteArrayOutputStream;

import org.bibsonomy.common.exceptions.InternServerException;

/**
 * @author Dominik Benz
 * @version $Id$
 */
public abstract class AbstractDeleteStrategy extends Strategy {
	
	/**
	 * @param context
	 */
	public AbstractDeleteStrategy(final Context context) {
		super(context);
	}

	@Override
	public final void perform(final ByteArrayOutputStream outStream) throws InternServerException {
		final boolean deleted = delete();
		if (deleted) {
			this.getRenderer().serializeOK(writer);
		} else {
			this.getRenderer().serializeFail(writer);
		}
	}

	protected abstract boolean delete();
}