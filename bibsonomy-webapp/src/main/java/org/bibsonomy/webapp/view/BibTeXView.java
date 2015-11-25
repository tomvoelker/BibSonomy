/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.view;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.webapp.command.BibtexViewCommand;


/**
 * Outputs posts in BibTeX format.
 * 
 * @author rja
 */
public class BibTeXView extends AbstractPublicationView<BibtexViewCommand> {
	
	/** a map of url generators */
	private Map<String, URLGenerator> urlGenerators;
	
	@Override
	protected BibtexViewCommand castCmd(Object object) {
		if (object instanceof BibtexViewCommand) {
			return (BibtexViewCommand)object;
		}
		return null;
	}
	
	private static int getFlags(final BibtexViewCommand command) {
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
		final List<Post<BibTex>> publicationPosts = command.getBibtex().getList();
		if (present(publicationPosts)) {
			for (final Post<BibTex> post : publicationPosts) {
				writer.append(BibTexUtils.toBibtexString(post, flags, urlGenerator) + "\n\n");
			}
		}
	}
	
	private URLGenerator getUrlGenerator(String urlGenerator) {
		final URLGenerator rVal = urlGenerators.get(urlGenerator);
		if (rVal == null) {
			return urlGenerators.get("default");
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
