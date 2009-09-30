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
 * @author rja
 * @author dzo
 * @version $Id$
 */
public class PostPublicationController extends PostPostController<BibTex> {

	private static final Log logger = LogFactory.getLog(PostPublicationController.class);

	private Scraper scraper;

	@Override
	protected View getPostView() {
		return Views.POST_PUBLICATION; // TODO: this could be configured using Spring!
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

			if (ValidationUtils.present(url)) {
				/*
				 * scrape the website and parse bibtex
				 */
				String selection = publicationCommand.getSelection();

				if (selection == null) {
					selection = "";
				}

				try {
					final Post<BibTex> post = publicationCommand.getPost();

					/*
					 * scrape bibtex
					 */
					final ScrapingContext scrapingContext = this.scrapeBibtex(url, selection);
					if (ValidationUtils.present(scrapingContext)) {
						/*
						 * store scraping context to show user meta information
						 */
						publicationCommand.setScrapingContext(scrapingContext);
						/*
						 * parse bibtex
						 */
						final String scrapedBibtex = scrapingContext.getBibtexResult();
						if (ValidationUtils.present(scrapedBibtex)) {
							logger.debug("parsing bibtex \"" + scrapedBibtex + "\"");
							/*
							 * FIXME: if the parser is thread-safe, we can use 
							 * the same for each infocation
							 */
							try {
								final BibTex parsedBibTex = new SimpleBibTeXParser().parseBibTeX(scrapedBibtex);

								/*
								 * store scraper metadata
								 */
								if (scrapingContext.getMetaResult() != null) {
									/*
									 * FIXME: NEVER use old code!
									 */
									int scraperId = new DBScraperMetadataManager().insertMetadata(scrapingContext);
									if (scraperId > 0) {
										parsedBibTex.setScraperId(scraperId);
									}
								}


								// check if a bibtex was scraped
								if (ValidationUtils.present(parsedBibTex)) {						
									// set recommender, ...
									this.initPost(command);

									/*
									 * FIXME: why is this needed? Shouldn't be necessary!
									 */
									// this.populateCommandWithPost(command, post);

									// save result
									post.setResource(parsedBibTex);

									// if user already owns resource set diff post
									this.setDiffPost(command);
									
									/*
									 * FIXME: put scraper into command to show 
									 * user the metadata 
									 */
								} else {
									this.getErrors().reject("error.scrape.nothing");
								}

								// show view
								return this.getPostPostView(command, loginUser);
							} catch (IOException ex) {
								// exception while parsing bibtex 
								logger.fatal("error while paring bibtex \"" + scrapedBibtex + "\"", ex);
								this.getErrors().reject("error.parse.bibtex.failed", new Object[]{scrapedBibtex}, "Error parsing BibTeX: {0}");
							} catch (ParseException ex) {
								// exception while parsing bibtex; inform user and show him the scraped bibtex
								logger.fatal("error while paring bibtex \"" + scrapedBibtex + "\"", ex);
								this.getErrors().reject("error.parse.bibtex.failed", new Object[]{scrapedBibtex}, "Error parsing BibTeX: {0}");
							}
						}
					}
					/*
					 * FIXME: adjust logging / error handling
					 */

				} catch (ScrapingException ex) {
					// scraping failed no bibtex scraped
					logger.fatal("scraping failed", ex);
					this.getErrors().reject("error.scrape.failed");

					return Views.ERROR;
				} catch (MalformedURLException ex) {
					// wrong url format
					logger.warn("can't parse url \"" + url + "\"", ex);
					this.getErrors().rejectValue("url", "error.field.valid.url");

					return Views.ERROR;
				} catch (IOException ex) {
					// scraping failed no bibtex scraped
					logger.fatal("scraping failed", ex);
					this.getErrors().reject("error.scrape.failed");

					return Views.ERROR;
				}

				return this.getPostPostView(command, loginUser);
			}
		}

		// handle update or create a publication
		return super.workOn(command);
	}

	/**
	 * Scrapes the given URL/selection and on success returns the scraping 
	 * context containing the result (plus additional metadata).
	 * 
	 * @param url
	 * @param selectedText
	 * @return
	 * @throws ScrapingException
	 * @throws IOException
	 */
	private ScrapingContext scrapeBibtex(final String url, final String selectedText) throws ScrapingException, IOException {
		logger.debug("scraping url \"" + url + "\" with selected text " + "\"" + selectedText + "\"");
		final ScrapingContext context = new ScrapingContext(new URL(url));
		context.setSelectedText(selectedText);
		/*
		 * scrape
		 */
		if (scraper.scrape(context)) {
			return context;
		}
		return null;
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
