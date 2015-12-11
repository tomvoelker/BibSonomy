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
package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.bibtex.parser.PostBibTeXParser;
import org.bibsonomy.bibtex.parser.SimpleBibTeXParser;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ScraperMetadata;
import org.bibsonomy.model.User;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.webapp.command.actions.EditPostCommand;
import org.bibsonomy.webapp.command.actions.EditPublicationCommand;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.PostValidator;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

import bibtex.parser.ParseException;

/**
 * Posting/editing one (!) publication posts.
 * 
 * TODO:
 * <ul>
 * <li>{@link SimpleBibTeXParser} stores warnings in a list - maybe we should
 * show those to the user?</li>
 * </ul>
 * 
 * @author rja
 * @author dzo
 * 
 * @param <COMMAND>
 */
public abstract class AbstractEditPublicationController<COMMAND extends EditPublicationCommand> extends EditPostController<BibTex, COMMAND> {

	private static final String SESSION_ATTRIBUTE_SCRAPER_METADATA = "scraperMetaData";

	protected Scraper scraper;

	@Override
	protected View getPostView() {
		return Views.EDIT_PUBLICATION; // TODO: this could be configured using
										// Spring!
	}

	/**
	 * If the command has set a url or selection, the scrapers are called to
	 * fill
	 * the command's post with the scraped data.
	 * 
	 * @see org.bibsonomy.webapp.controller.actions.EditPostController#workOnCommand(org.bibsonomy.webapp.command.actions.EditPostCommand,
	 *      org.bibsonomy.model.User)
	 */
	@Override
	protected void workOnCommand(final COMMAND command, final User loginUser) {
		/*
		 * Check if the controller was called by a bookmarklet which just
		 * delivers URL + selection which should be passed to the scrapers.
		 */
		final String url = command.getUrl();
		final String selection = command.getSelection();

		if ((present(url) || present(selection))) {
			this.handleScraper(command, url, selection);
		}
	}

	@Override
	protected void preparePost(final COMMAND command, final Post<BibTex> post) {
		super.preparePost(command, post);

		/*
		 * link the temp documents with the post
		 */
		final List<String> fileNames = command.getFileName();
		if (!present(fileNames)) {
			return;
		}
		final BibTex publication = post.getResource();
		if (publication.getDocuments() == null) {
			publication.setDocuments(new LinkedList<Document>());
		}
		for (final String compoundFileName : fileNames) {
			final String fileHash = compoundFileName.substring(0, 32);
			final String fileName = compoundFileName.substring(32);
			/*
			 * copy temporary file to documents directory
			 */
			final Document document = new Document();
			document.setTemp(true);
			document.setFileName(fileName);
			document.setFileHash(fileHash);
			/*
			 * add document to the resource the logic will
			 * move the file to the correct position
			 */
			publication.getDocuments().add(document);
		}
	}

	protected void handleScraper(final COMMAND command, final String url, String selection) {
		/*
		 * We have a URL set which means we shall scrape!
		 * 
		 * set selected text
		 */
		if (selection == null) {
			selection = "";
		}

		/*
		 * Create context for scraping
		 */
		ScrapingContext scrapingContext;
		try {
			scrapingContext = new ScrapingContext(url == null ? null : new URL(url), selection);
		} catch (final MalformedURLException ex) {
			/*
			 * wrong url format
			 */
			this.getErrors().reject("error.scrape.failed", new Object[] { url, ex.getMessage() }, "Could not scrape the URL {0}.\nMessage was: {1}");
			return;
		}

		/*
		 * --> scrape the website and parse bibtex
		 */
		try {
			/*
			 * scrape bibtex
			 * NOTE: if the given URL is null, we probably have to scrape a
			 * selection (e.g., ISBN) - therefore, we insert a null URL
			 */
			final boolean isSuccess = this.scraper.scrape(scrapingContext);
			final String scrapedBibtex = scrapingContext.getBibtexResult();
			if (isSuccess && present(scrapedBibtex)) {
				/*
				 * When the parser is thread-safe (it currently is not!), we can
				 * use the same instance for each invocation.
				 * TODO: why don't we use the PostBibTeXParser? (There must be
				 * reasons for not using it! E.g., probably because otherwise
				 * tags are taken from the post and then no edit form appears.)
				 */
				try {
					final SimpleBibTeXParser parser = new SimpleBibTeXParser();
					final BibTex parsedBibTex = parser.parseBibTeX(scrapedBibtex);

					/*
					 * check if a bibtex was scraped
					 */
					if (present(parsedBibTex)) {
						/*
						 * save result
						 */
						command.getPost().setResource(parsedBibTex);
						/*
						 * store scraping context and scraping metadata
						 */
						this.handleScraperMetadata(command, scrapingContext);
					} else {
						/*
						 * the parser did not return any result ...
						 */
						this.getErrors().reject("error.scrape.nothing", new Object[] { scrapedBibtex, scrapingContext.getUrl() }, "The BibTeX\n\n{0}\n\nwe scraped from {1} could not be parsed.");
					}
				} catch (final IOException ex) {
					/*
					 * exception while parsing bibtex
					 */
					this.getErrors().reject("error.parse.bibtex.failed", new Object[] { scrapedBibtex, ex.getMessage() }, "Error parsing BibTeX:\n\n{0}\n\nMessage was: {1}");
				} catch (final ParseException ex) {
					/*
					 * exception while parsing bibtex; inform user and show him
					 * the scraped bibtex
					 */
					this.getErrors().reject("error.parse.bibtex.failed", new Object[] { scrapedBibtex, ex.getMessage() }, "Error parsing BibTeX:\n\n{0}\n\nMessage was: {1}");
				}
			} // if (isSuccess && present(scrapedBibtex))
			else {
				/*
				 * We could not scrape the given URL, i.e., the URL is either
				 * not
				 * supported (no scraper) or the scrape did not manage to
				 * extract
				 * something.
				 * FIXME: in this case we should probably show the
				 * boxes/import_publication_hints.jsp
				 */
				this.getErrors().reject("error.scrape.nothing", new Object[] { scrapingContext.getUrl() }, "The URL {0} is not supported by one of our scrapers.");
			}
		} catch (final ScrapingException ex) {
			/*
			 * scraping failed no bibtex scraped
			 */
			this.getErrors().reject("error.scrape.failed", new Object[] { scrapingContext.getUrl(), ex.getMessage() }, "Could not scrape the URL {0}.\nMessage was: {1}");
		}
	}

	private void handleScraperMetadata(final COMMAND command, final ScrapingContext scrapingContext) {
		/*
		 * store scraping context to show user meta information
		 */
		command.setScrapingContext(scrapingContext);
		/*
		 * clean old scraper metadata
		 */
		this.setSessionAttribute(SESSION_ATTRIBUTE_SCRAPER_METADATA, null);
		/*
		 * store scraper metadata in session (to later store it
		 * together with the post)
		 */
		if (present(scrapingContext.getMetaResult())) {
			final ScraperMetadata scraperMetadata = new ScraperMetadata();
			scraperMetadata.setScraperClass(scrapingContext.getScraper().getClass().getName());
			scraperMetadata.setMetaData(scrapingContext.getMetaResult());
			scraperMetadata.setUrl(scrapingContext.getUrl());
			this.setSessionAttribute(SESSION_ATTRIBUTE_SCRAPER_METADATA, scraperMetadata);
		}
	}

	/**
	 * This controller exchanges the resource by a parsed version of it and
	 * additionally adds scraper metadata from the session (if available).
	 * 
	 * @see org.bibsonomy.webapp.controller.actions.EditPostController#cleanPost(org.bibsonomy.model.Post)
	 */
	@Override
	protected void cleanPost(final Post<BibTex> post) {
		/*
		 * exchange post with a parsed version
		 */
		try {
			new PostBibTeXParser(this.instantiateResource().getClass()).updateWithParsedBibTeX(post);
		} catch (final ParseException ex) {
			/*
			 * we silently ignore parsing errors - they have been handled by the
			 * validator
			 */
		} catch (final IOException ex) {
			/*
			 * we silently ignore parsing errors - they have been handled by the
			 * validator
			 */
		}
		/*
		 * store scraper metadata
		 */
		final Object scraperMetadata = this.getSessionAttribute(SESSION_ATTRIBUTE_SCRAPER_METADATA);
		if (present(scraperMetadata)) {
			post.getResource().setScraperMetadata((ScraperMetadata) scraperMetadata);
		}
		super.cleanPost(post);
	}

	@Override
	protected void replaceResourceSpecificPostFields(final BibTex bibResource, final String key, final BibTex newResource) {
		/*
		 * TODO: Move functionality to BibTeXUtils or some other fitting model
		 * class
		 */
		// BeanUtils.copyProperty(bean, name, value);
		switch (key) {
		case "entrytype":
			bibResource.setEntrytype(newResource.getEntrytype());
			break;
		case "title":
			bibResource.setTitle(newResource.getTitle());
			break;
		case "author":
			bibResource.setAuthor(newResource.getAuthor());
			break;
		case "editor":
			bibResource.setEditor(newResource.getEditor());
			break;
		case "year":
			bibResource.setYear(newResource.getYear());
			break;
		case "booktitle":
			bibResource.setBooktitle(newResource.getBooktitle());
			break;
		case "journal":
			bibResource.setJournal(newResource.getJournal());
			break;
		case "volume":
			bibResource.setVolume(newResource.getVolume());
			break;
		case "number":
			bibResource.setNumber(newResource.getNumber());
			break;
		case "pages":
			bibResource.setPages(newResource.getPages());
			break;
		case "month":
			bibResource.setMonth(newResource.getMonth());
			break;
		case "day":
			bibResource.setDay(newResource.getDay());
			break;
		case "publisher":
			bibResource.setPublisher(newResource.getPublisher());
			break;
		case "address":
			bibResource.setAddress(newResource.getAddress());
			break;
		case "edition":
			bibResource.setEdition(newResource.getEdition());
			break;
		case "chapter":
			bibResource.setChapter(newResource.getChapter());
			break;
		case "url":
			bibResource.setUrl(newResource.getUrl());
			break;
		case "key":
			bibResource.setKey(newResource.getKey());
			break;
		case "howpublished":
			bibResource.setHowpublished(newResource.getHowpublished());
			break;
		case "institution":
			bibResource.setInstitution(newResource.getInstitution());
			break;
		case "organization":
			bibResource.setOrganization(newResource.getOrganization());
			break;
		case "school":
			bibResource.setSchool(newResource.getSchool());
			break;
		case "series":
			bibResource.setSeries(newResource.getSeries());
			break;
		case "crossref":
			bibResource.setCrossref(newResource.getCrossref());
			break;
		case "misc":
			bibResource.setMisc(newResource.getMisc());
			break;
		case "bibtexAbstract":
			bibResource.setAbstract(newResource.getAbstract());
			break;
		case "privnote":
			bibResource.setPrivnote(newResource.getPrivnote());
			break;
		case "annote":
			bibResource.setAnnote(newResource.getAnnote());
			break;
		case "note":
			bibResource.setNote(newResource.getNote());
			break;
		default:
			throw new ValidationException("Couldn't find " + key + " among BibTex fields!");
		}
	}

	@Override
	protected BibTex instantiateResource() {
		return new BibTex();
	}

	@Override
	protected PostValidator<BibTex> getValidator() {
		return new PostValidator<BibTex>();
	}

	@Override
	protected void setDuplicateErrorMessage(final Post<BibTex> post, final Errors errors) {
		errors.rejectValue("post.resource.title", "error.field.valid.alreadyStoredPublication", "You already have this publication in your collection.");
	}

	/**
	 * @param scraper the scraper to set
	 */
	public void setScraper(final Scraper scraper) {
		this.scraper = scraper;
	}
}