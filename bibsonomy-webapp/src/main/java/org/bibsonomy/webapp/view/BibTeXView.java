package org.bibsonomy.webapp.view;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.webapp.command.BibtexViewCommand;


/**
 * Outputs posts in BibTeX format.
 * 
 * TODO: could as well be used to return EndNote?!
 * 
 * @author rja
 */
public class BibTeXView extends AbstractPublicationView<BibtexViewCommand> {

	/**
	 * must be injected
	 */
	private Map<String, URLGenerator> urlGenerators;
	
	@Override
	protected BibtexViewCommand castCmd(Object object) {
		if (object instanceof BibtexViewCommand) {
			return (BibtexViewCommand)object;
		}
		return null;
	}
	
	protected int getFlags(final BibtexViewCommand command) {
		/*
		 * configure BibTeX export
		 * 
		 * TODO: the next two URL parameters must be added to the command 
		 * and the allowed fields (don't change their name, they are already 
		 * used)
		 */
		final int flags = BibTexUtils.getFlags(false, command.isFirstLastNames(), command.isGeneratedBibtexKeys(), command.isSkipDummyValues());
		return flags;
	}
	
	@Override
	protected void render(final BibtexViewCommand command, final OutputStreamWriter writer) throws IOException {
		final int flags = getFlags(command);
		final URLGenerator urlGenerator = getUrlGenerator(command.getUrlGenerator());
		/*
		 * write posts
		 */
		for (final Post<BibTex> post : command.getBibtex().getList()) {
			writer.append(BibTexUtils.toBibtexString(post, flags, urlGenerator) + "\n\n");
		}
	}
	
	protected URLGenerator getUrlGenerator(String urlGenerator) {
		// hier könnte man auch direkt einen urlGenerator aus einer (parametrisierten und cachenden) factory ziehen
		URLGenerator rVal = urlGenerators.get(urlGenerator);
		if (rVal == null) {
			rVal = urlGenerators.get("default");
		}
		return rVal;
	}

	/**
	 * @param urlGenerators the urlGenerators to set
	 */
	public void setUrlGenerators(Map<String, URLGenerator> urlGenerators) {
		this.urlGenerators = urlGenerators;
	}
}
