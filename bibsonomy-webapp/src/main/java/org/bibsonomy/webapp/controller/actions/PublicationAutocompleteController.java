/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.bibtex.parser.PostBibTeXParser;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.SearchType;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.id.kde.isbn.ISBNScraper;
import org.bibsonomy.search.InvalidSearchRequestException;
import org.bibsonomy.util.id.DOIUtils;
import org.bibsonomy.util.id.ISBNUtils;
import org.bibsonomy.webapp.command.actions.PublicationAutocompleteCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import bibtex.parser.ParseException;

/**
 * publication autocomplete controller
 *
 * @author dzo
 */
public class PublicationAutocompleteController implements MinimalisticController<PublicationAutocompleteCommand> {
	private static final Log log = LogFactory.getLog(PublicationAutocompleteController.class);
	
	private LogicInterface logic;
	private Scraper scrapers;
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.MinimalisticController#instantiateCommand()
	 */
	@Override
	public PublicationAutocompleteCommand instantiateCommand() {
		return new PublicationAutocompleteCommand();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.MinimalisticController#workOn(org.bibsonomy.webapp.command.ContextCommand)
	 */
	@Override
	public View workOn(final PublicationAutocompleteCommand command) {
		final String rawSearch = command.getSearch();
		final List<Post<BibTex>> allPosts = new LinkedList<>();
		final String isbn = ISBNUtils.extractISBN(rawSearch);
		final String doi = DOIUtils.extractDOI(rawSearch);
		if (present(isbn)) {
			final Post<BibTex> post = callScraper(new ISBNScraper(), isbn);
			if (present(post)) {
				allPosts.add(post);
			}
		} if (present(doi)) {
			final Post<BibTex> post = callScraper(this.scrapers, doi);
			if (present(post)) {
				allPosts.add(post);
			}
		} else if (present(rawSearch)) {
			String search = null;
			List<String> tags = new LinkedList<>();
			// if search is a number search for isbn or doi
			if (rawSearch.matches(".*\\d+.*")) {
				search = "isbn:" + rawSearch;
				search += " OR doi:" + rawSearch;
			} else {
				// build title system tags for searching publication by title
				final List<String> titleParts = Arrays.asList(rawSearch.split(" "));
				final Iterator<String> titlePartsIterator = titleParts.iterator();
				while (titlePartsIterator.hasNext()) {
					final String titlePart = titlePartsIterator.next();
					String tag = "sys:title:" + titlePart;
					if (!titlePartsIterator.hasNext()) {
						tag += "*"; // TODO: * is elasticsearch specific; should be a constant
					}
					tags.add(tag);
				}
			}
			try {
				final List<Post<BibTex>> postsBySearch = this.logic.getPosts(BibTex.class, GroupingEntity.ALL, null, tags, null, search, SearchType.LOCAL, null, Order.RANK, null, null, 0, 10);
				allPosts.addAll(postsBySearch);
			} catch (final InvalidSearchRequestException e) {
				// ignore
			}

		}
		
		BibTexUtils.removeDuplicates(allPosts);
		command.getBibtex().setList(allPosts);
		
		return Views.getViewByFormat(command.getFormat());
	}

	/**
	 * @param scraper
	 * @param text
	 * @return 
	 */
	private static Post<BibTex> callScraper(final Scraper scraper, final String text) {
		try {
			final ScrapingContext context = new ScrapingContext(null, text);
			final boolean scrape = scraper.scrape(context);
			if (scrape) {
				final String result = context.getBibtexResult();
				final PostBibTeXParser postBibTeXParser = new PostBibTeXParser();
				return postBibTeXParser.parseBibTeXPost(result);
			}
		} catch (final IOException | ScrapingException | ParseException e) {
			log.info("exception while scraping", e);
		}
		
		return null;
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @param scrapers the scrapers to set
	 */
	public void setScrapers(Scraper scrapers) {
		this.scrapers = scrapers;
	}

}
