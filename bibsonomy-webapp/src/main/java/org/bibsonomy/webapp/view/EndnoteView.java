package org.bibsonomy.webapp.view;

import java.io.IOException;
import java.io.OutputStreamWriter;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.util.EndnoteUtils;
import org.bibsonomy.webapp.command.PublicationViewCommand;


/**
 * Outputs posts in Endnote format.
 * 
 * @author rja
 */
public class EndnoteView extends AbstractPublicationView<PublicationViewCommand> {

	@Override
	protected PublicationViewCommand castCmd(Object object) {
		if (object instanceof PublicationViewCommand) {
			return (PublicationViewCommand) object;
		}
		return null;
	}
	
	@Override
	protected void render(PublicationViewCommand command, OutputStreamWriter writer) throws IOException {
		boolean first = true;
		for (final Post<BibTex> post : command.getBibtex().getList()) {
			if (first) {
				first = false;
			} else {
				writer.append('\n');
			}
			EndnoteUtils.append(writer, post, command.isSkipDummyValues());
		}
	}
}
