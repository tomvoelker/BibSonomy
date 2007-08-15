package org.bibsonomy.rest.strategy;

import java.io.Reader;
import java.io.Writer;

import org.bibsonomy.common.exceptions.InternServerException;

/**
 * @author Dominik Benz
 * @version $Id$
 */
public abstract class AbstractUpdateStrategy extends Strategy {

	protected final Reader doc;
	
	public AbstractUpdateStrategy(final Context context) {
		super(context);
		this.doc = context.getDocument();
	}

	@Override
	public final void perform(final Writer writer) throws InternServerException {
		final String resourceID = update();		
		render(writer, resourceID);
	}

	protected abstract void render(Writer writer, String resourceID);

	protected abstract String update();
}