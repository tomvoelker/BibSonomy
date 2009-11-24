package org.bibsonomy.webapp.controller.actions;

import helpers.database.DBScraperMetadataManager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.bibtex.parser.SimpleBibTeXParser;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.webapp.command.actions.EditPostCommand;
import org.bibsonomy.webapp.command.actions.EditPublicationCommand;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.PostPostValidator;
import org.bibsonomy.webapp.validation.PostPublicationValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;

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
 */
public class PostPublicationController extends PostPostController<BibTex> {

	private static final Log logger = LogFactory.getLog(PostPublicationController.class);

	private Scraper scraper;

	@Override
	protected View getPostView() {
		return Views.EDIT_PUBLICATION; // TODO: this could be configured using Spring!
	}

	@Override
	protected String getRedirectUrl(Post<BibTex> post) {
		/*
		 * FIXME: this isn't necessary the right URL - it might be the PDF!
		 * Thus, we need to store the original URL.
		 * Or maybe it's better to redirect to the /bibtex/HASH/USER page?
		 * 
		 */
		return post.getResource().getUrl();
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.webapp.controller.actions.PostPostController#workOn(org.bibsonomy.webapp.command.actions.EditPostCommand)
	 */
	@Override
	public View workOn(final EditPostCommand<BibTex> command) {
		final RequestWrapperContext context = command.getContext();
		/*
		 *  user logged in?
		 */
		if (!context.isUserLoggedIn()) {
			// TODO: refactor @see PostPostController#workOn
			return new ExtendedRedirectView("/login?notice=" + LOGIN_NOTICE + command.getPost().getResource().getClass().getSimpleName().toLowerCase() + "&referer=/postPublication?" + this.safeURIEncode(context.getQueryString())); 
		}

		final User loginUser = context.getLoginUser();

		/*
		 * Check if the controller was called by a bookmarklet which just 
		 * delivers URL + selection which should be passed to the scrapers.
		 */
		if (command instanceof EditPublicationCommand) {
			final EditPublicationCommand publicationCommand = (EditPublicationCommand) command;
			final String url = publicationCommand.getUrl();
			final String selection = publicationCommand.getSelection();

			if (ValidationUtils.present(url) || ValidationUtils.present(selection)) {
				return handleScraper(command, loginUser, publicationCommand, url, selection);
			} // if (ValidationUtils.present(url) || ValidationUtils.present(selection))
		} 

		/*
		 * handle update or create of a publication
		 */
		return super.workOn(command);
	}

	private View handleScraper(final EditPostCommand<BibTex> command, final User loginUser, final EditPublicationCommand publicationCommand, final String url, String selection) {
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
			 */
			final ScrapingContext scrapingContext = new ScrapingContext(new URL(url), selection);
			final boolean isSuccess = scraper.scrape(scrapingContext);
			final String scrapedBibtex = scrapingContext.getBibtexResult();
			if (isSuccess && ValidationUtils.present(scrapedBibtex)) {
				/*
				 * XXX: if the parser is thread-safe, we can use 
				 * the same instance for each invocation
				 */
				try {
					final BibTex parsedBibTex = new SimpleBibTeXParser().parseBibTeX(scrapedBibtex);

					/*
					 * store scraper metadata using old code
					 * 
					 * FIXME: NEVER use old code!
					 */
					if (scrapingContext.getMetaResult() != null) {
						int scraperId = new DBScraperMetadataManager().insertMetadata(scrapingContext);
						if (scraperId > 0) {
							parsedBibTex.setScraperId(scraperId);
						}
					}

					/*
					 * check if a bibtex was scraped
					 */
					if (ValidationUtils.present(parsedBibTex)) {						
						/*
						 * initialize things needed for page 
						 * (groups, etc.)
						 */
						this.initPost(command);
						/*
						 * FIXME: why is this needed? Shouldn't be necessary!
						 */
						// this.populateCommandWithPost(command, post);

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
						 * return to view 
						 */
						return this.getPostPostView(command, loginUser);
					} // if (ValidationUtils.present(parsedBibTex))
					/*
					 * the parser did not return any result ...
					 */
					this.getErrors().reject("error.scrape.nothing", new Object[]{url, scrapedBibtex}, "The BibTeX\n\n{0}\n\nwe scraped from {1} could not be parsed.");
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
		return Views.ERROR;
	}
	
	@Override
	protected void preparePostForView(Post<BibTex> post) {
		/*
		 * replace all " and "s by a new line in author and
		 * editor field of the bibtex to separate multiple authors and editors
		 */
		BibTexUtils.prepareEditorAndAuthorFieldForView(post.getResource());
	}

	@Override
	protected void preparePostForDatabase(Post<BibTex> post) {
		/*
		 * replace all new lines with an " and " to undo the preparePostForView action
		 */
		BibTexUtils.prepareEditorAndAuthorFieldForDatabase(post.getResource());
	}
	
	@Override
	protected BibTex instantiateResource() {
		final BibTex publication = new BibTex();
		return publication;
	}

	@Override
	protected PostPostValidator<BibTex> getValidator() {
		return new PostPublicationValidator();
	}

	@Override
	protected EditPostCommand<BibTex> instantiateEditPostCommand() {
		return new EditPublicationCommand();
	}

	public Scraper getScraper() {
		return this.scraper;
	}

	public void setScraper(Scraper scraper) {
		this.scraper = scraper;
	}

}
