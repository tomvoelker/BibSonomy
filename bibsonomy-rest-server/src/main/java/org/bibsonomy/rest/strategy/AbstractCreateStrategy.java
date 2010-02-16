package org.bibsonomy.rest.strategy;

import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.io.Writer;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.rest.renderer.xml.tools.EscapingPrintWriter;

/**
 * @author Dominik Benz
 * @version $Id$
 */
public abstract class AbstractCreateStrategy extends Strategy {

	protected final Reader doc;
	protected Writer writer;
	
	/**
	 * @param context
	 */
	public AbstractCreateStrategy(final Context context) {
		super(context);
		this.doc = context.getDocument();		
	}

	@Override
	public final void perform(final ByteArrayOutputStream outStream) throws InternServerException {
		writer = new EscapingPrintWriter(outStream);
		final String resourceID = this.create();	
		render(writer, resourceID);
	}

	protected abstract void render(Writer writer, String resourceID);

	protected abstract String create();
}