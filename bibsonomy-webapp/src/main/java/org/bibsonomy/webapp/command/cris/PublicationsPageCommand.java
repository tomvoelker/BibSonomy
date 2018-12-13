package org.bibsonomy.webapp.command.cris;

import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;
import org.bibsonomy.webapp.command.EntitySearchAndFilterCommand;
import org.bibsonomy.webapp.command.ListCommand;

/**
 * command of the publication overview command
 *
 * @author dzo
 */
public class PublicationsPageCommand extends EntitySearchAndFilterCommand {
	private final ListCommand<Post<GoldStandardPublication>> publications = new ListCommand<>(this);

	/**
	 * @return the publications
	 */
	public ListCommand<Post<GoldStandardPublication>> getPublications() {
		return publications;
	}
}
