package org.bibsonomy.webapp.controller.actions;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.bibtex.parser.PostBibTeXParser;
import org.bibsonomy.bibtex.parser.SimpleBibTeXParser;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ScraperMetadata;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.ValidationUtils;
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
 * @version $Id$
 * 
 * @param <COMMAND> 
 */
public abstract class AbstractEditPublicationController<COMMAND extends EditPublicationCommand> extends EditPostController<BibTex, COMMAND> {

	private static final String SESSION_ATTRIBUTE_SCRAPER_METADATA = "scraperMetaData";

	private static final Log log = LogFactory.getLog(AbstractEditPublicationController.class);

	private Scraper scraper;

	@Override
	protected View getPostView() {
		return Views.EDIT_PUBLICATION; // TODO: this could be configured using Spring!
	}


	/**
	 * If the command has set a url or selection, the scrapers are called to fill
	 * the command's post with the scraped data. 
	 * 
	 * @see org.bibsonomy.webapp.controller.actions.EditPostController#workOnCommand(org.bibsonomy.webapp.command.actions.EditPostCommand, org.bibsonomy.model.User)
	 */
	@Override
	protected void workOnCommand(final COMMAND command, final User loginUser) {
		/*
		 * Check if the controller was called by a bookmarklet which just 
		 * delivers URL + selection which should be passed to the scrapers.
		 */
		final String url = command.getUrl();
		final String selection = command.getSelection();

		if (ValidationUtils.present(url) || ValidationUtils.present(selection)) {
			handleScraper(command, loginUser, command, url, selection);
		} // if (ValidationUtils.present(url) || ValidationUtils.present(selection))
	}


	private void handleScraper(final COMMAND command, final User loginUser, final COMMAND publicationCommand, final String url, String selection) {
		/*
		 * We have a URL set which means we shall scrape!
		 * 
		 * set selected text
		 */
		if (selection == null) {
			selection = "";
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
			final ScrapingContext scrapingContext = new ScrapingContext(url == null ? null : new URL(url), selection);
			final boolean isSuccess = scraper.scrape(scrapingContext);
			final String scrapedBibtex = scrapingContext.getBibtexResult();
			if (isSuccess && ValidationUtils.present(scrapedBibtex)) {
				/*
				 * When the parser is thread-safe (it currently is not!), we can 
				 * use the same instance for each invocation.
				 */
				try {
					final SimpleBibTeXParser parser = new SimpleBibTeXParser();
					final BibTex parsedBibTex = parser.parseBibTeX(scrapedBibtex);
					log.debug(parser.getWarnings());

					/*
					 * check if a bibtex was scraped
					 */
					if (ValidationUtils.present(parsedBibTex)) {						
						/*
						 * initialize things needed for page 
						 * (groups, etc.)
						 */
						this.initPost(command, command.getPost(), loginUser);
						/*
						 * save result
						 */
						publicationCommand.getPost().setResource(parsedBibTex);
						/*
						 * if user already owns resource set diff post
						 */
						this.setDiffPost(command);
						/*
						 * store scraping context to show user meta information
						 */
						publicationCommand.setScrapingContext(scrapingContext);
						/*
						 * clean old scraper metadata
						 */
						setSessionAttribute(SESSION_ATTRIBUTE_SCRAPER_METADATA, null);
						/*
						 * store scraper metadata in session (to later store it 
						 * together with the post)
						 */
						if (ValidationUtils.present(scrapingContext.getMetaResult())) {
							final ScraperMetadata scraperMetadata = new ScraperMetadata();
							scraperMetadata.setScraperClass(scrapingContext.getScraper().getClass().getName());
							scraperMetadata.setMetaData(scrapingContext.getMetaResult());
							scraperMetadata.setUrl(scrapingContext.getUrl());
							setSessionAttribute(SESSION_ATTRIBUTE_SCRAPER_METADATA, scraperMetadata);
						}						
						/*
						 * return to view 
						 */
//						return this.getEditPostView(command, loginUser);
					} else {// if (ValidationUtils.present(parsedBibTex))
						/*
						 * the parser did not return any result ...
						 */
						this.getErrors().reject("error.scrape.nothing", new Object[]{scrapedBibtex, url}, "The BibTeX\n\n{0}\n\nwe scraped from {1} could not be parsed.");
					}
				} catch (IOException ex) {
					/*
					 * exception while parsing bibtex
					 */ 
					this.getErrors().reject("error.parse.bibtex.failed", new Object[]{scrapedBibtex, ex.getMessage()}, "Error parsing BibTeX:\n\n{0}\n\nMessage was: {1}");
				} catch (ParseException ex) {
					/*
					 * exception while parsing bibtex; inform user and show him the scraped bibtex
					 */
					this.getErrors().reject("error.parse.bibtex.failed", new Object[]{scrapedBibtex, ex.getMessage()}, "Error parsing BibTeX:\n\n{0}\n\nMessage was: {1}");
				}
			} // if (isSuccess && ValidationUtils.present(scrapedBibtex))
			else {
				/*
				 * We could not scrape the given URL, i.e., the URL is either not
				 * supported (no scraper) or the scrape did not manage to extract
				 * something.
				 * FIXME: in this case we should probably show the 
				 * boxes/import_publication_hints.jsp 
				 */
				this.getErrors().reject("error.scrape.nothing", new Object[]{url}, "The URL {0} is not supported by one of our scrapers.");
			}
		} catch (ScrapingException ex) {
			/*
			 * scraping failed no bibtex scraped
			 */
			this.getErrors().reject("error.scrape.failed", new Object[]{url, ex.getMessage()}, "Could not scrape the URL {0}.\nMessage was: {1}");
		} catch (MalformedURLException ex) {
			/*
			 * wrong url format
			 */
			this.getErrors().reject("error.scrape.failed", new Object[]{url, ex.getMessage()}, "Could not scrape the URL {0}.\nMessage was: {1}");
		}
		/*
		 * A URL or selection to scrape was given ... but we did not 
		 * return to the post form ... so something went wrong
		 */
//		return Views.ERROR;
	}

	@Override
	protected void preparePostForView(Post<BibTex> post) {
		/*
		 * replace all " and "s by a new line in author and
		 * editor field of the bibtex to separate multiple authors and editors
		 */
		BibTexUtils.prepareEditorAndAuthorFieldForView(post.getResource());
	}


	/** 
	 * This controller exchanges the resource by a parsed version of it and 
	 * additionally adds scraper metadata from the session (if available).
	 * 
	 * @see org.bibsonomy.webapp.controller.actions.EditPostController#cleanPost(org.bibsonomy.model.Post)
	 */
	@Override
	protected void cleanPost(final Post<BibTex> post) {
		super.cleanPost(post);
		/*
		 * exchange post with a parsed version
		 */
		try {
			new PostBibTeXParser().updateWithParsedBibTeX(post);
		} catch (ParseException ex) {
			/*
			 * we silently ignore parsing errors - they have been handled by the
			 * validator
			 */
		} catch (IOException ex) {
			/*
			 * we silently ignore parsing errors - they have been handled by the
			 * validator
			 */
		}
		/*
		 * store scraper metadata
		 */
		final Object scraperMetadata = getSessionAttribute(SESSION_ATTRIBUTE_SCRAPER_METADATA);
		if (ValidationUtils.present(scraperMetadata)) {
			post.getResource().setScraperMetadata((ScraperMetadata) scraperMetadata);
		}
	}

	@Override
	protected void preparePostAfterView(final Post<BibTex> post) {
		/*
		 * replace all new lines with an " and " to undo the preparePostForView action
		 */
		BibTexUtils.prepareEditorAndAuthorFieldForDatabase(post.getResource());
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
	protected void setDuplicateErrorMessage(Post<BibTex> post, Errors errors) {
		errors.reject("error.duplicate.post");
	}


	public Scraper getScraper() {
		return this.scraper;
	}

	public void setScraper(Scraper scraper) {
		this.scraper = scraper;
	}

}
