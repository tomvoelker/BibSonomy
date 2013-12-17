package org.bibsonomy.rest.strategy;

import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.io.Writer;

import org.bibsonomy.common.exceptions.InternServerException;

/**
 * @author Dominik Benz
  */
public abstract class AbstractUpdateStrategy extends Strategy {
	protected final Reader doc;
	
	/**
	 * @param context
	 */
	public AbstractUpdateStrategy(final Context context) {
		super(context);
		this.doc = context.getDocument();
	}

	@Override
	public final void perform(final ByteArrayOutputStream outStream) throws InternServerException {
		final String resourceID = update();		
		render(writer, resourceID);
	}

	/**
	 * @param writer
	 * @param resourceID
	 */
	protected abstract void render(Writer writer, String resourceID);

	/**
	 * @return the resourceID of the updated resource
	 */
	protected abstract String update();
}