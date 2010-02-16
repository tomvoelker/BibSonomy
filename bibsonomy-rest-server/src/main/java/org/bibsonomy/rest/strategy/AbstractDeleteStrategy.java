package org.bibsonomy.rest.strategy;

import java.io.ByteArrayOutputStream;
import java.io.Writer;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.rest.renderer.xml.tools.EscapingPrintWriter;

/**
 * @author Dominik Benz
 * @version $Id$
 */
public abstract class AbstractDeleteStrategy extends Strategy {
	protected Writer writer;
	
	/**
	 * @param context
	 */
	public AbstractDeleteStrategy(final Context context) {
		super(context);
	}

	@Override
	public final void perform(final ByteArrayOutputStream outStream) throws InternServerException {
		writer = new EscapingPrintWriter(outStream);
		final boolean deleted = delete();
		if (deleted == true)
			this.getRenderer().serializeOK(writer);
		else 
			this.getRenderer().serializeFail(writer);
	}

	protected abstract boolean delete();
}