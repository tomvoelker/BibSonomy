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
import org.bibsonomy.scraper.KDEScraperFactory;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
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
	
	private static final String IMPORT_TAG = "imported";
	private static final Log logger = LogFactory.getLog(PostPublicationController.class);
	
	@Override
	protected View getPostView() {
		return Views.POST_PUBLICATION; // TODO: this could be configured using Spring!
	}

	@Override
	protected String getRedirectUrl(Post<BibTex> post) {
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
			return new ExtendedRedirectView("/login?notice=" + "login.notice.post." + command.getPost().getResource().getClass().getSimpleName().toLowerCase() + "&referer=/postPublication?" + this.safeURIEncode(context.getQueryString())); 
		}
		
		final User loginUser = context.getLoginUser();
		
		// check if the controller was called by a bookmarklet
		if (command instanceof EditPublicationCommand) {
			final EditPublicationCommand publicationCommand = (EditPublicationCommand) command;
			final String url = publicationCommand.getUrl();
			
			if (url != null) {
				// scrape site and parse bibtex
				String selection = publicationCommand.getSelection();
				
				if (selection == null) {
					selection = "";
				}
				
				try {
					final Post<BibTex> post = publicationCommand.getPost();
					
					// scrape bibtex
					final BibTex scrapedBibTex = this.scrapeBibtex(url, selection);
					
					// check if a bibtex was scraped
					if (scrapedBibTex != null) {						
						// set recommender, ...
						this.initPost(command);
						
						// set and update tags
						post.addTag(IMPORT_TAG);
						this.populateCommandWithPost(command, post);
						
						// save result
						post.setResource(scrapedBibTex);
						
						// if user already owns resource set diff post
						this.setDiffPost(command);
					} else {
						this.getErrors().reject("error.scrape.nothing");
					}
					
					// show view
					return this.getPostPostView(command, loginUser);
					
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
					// exception while parsing bibtex 
					logger.fatal("error while parsing bibtex", ex);
					this.getErrors().rejectValue("url", "error.field.valid.url");
					
					publicationCommand.setScrapedBibTex(this.scrapedBibtex);
					
				} catch (ParseException ex) {
					// exception while parsing bibtex; inform user and show him the scraped bibtex
					logger.fatal("error while paring bibtex \"" + this.scrapedBibtex + "\"", ex);
					this.getErrors().reject("error.parse.bibtex.failed");
					
					publicationCommand.setScrapedBibTex(this.scrapedBibtex);
				}
				
				return this.getPostPostView(command, loginUser);
			}
		}
		
		// handle update or create a publication
		return super.workOn(command);
	}

	
	private String scrapedBibtex; // hack to get the scraped bibtex string when a ParseException or an IOException while parsing occurs
	
	/**
	 * scrapes a BibTeX from the specified URL and selected text; parses the result in a {@link BibTex} object
	 * 
	 * @param urlString
	 * @param selectedText
	 * @return	a BibTex object or null if scraper scraped nothing
	 * @throws ScrapingException
	 * @throws IOException
	 * @throws ParseException
	 */
	private BibTex scrapeBibtex(final String urlString, final String selectedText) throws ScrapingException, IOException, ParseException {
		logger.debug("scraping url \"" + urlString + "\" with selected text " + "\"");
		final URL url = new URL(urlString);
		final Scraper scraper = new KDEScraperFactory().getScraper();
		
		final ScrapingContext context = new ScrapingContext(url);
		context.setSelectedText(selectedText);
		
		int id = -1;
		
		// scrape
		if (scraper.scrape(context)) {
			if (context.getMetaResult() != null) {
				id = new DBScraperMetadataManager().insertMetadata(context);
			}
			
			// parse result string in a BibTex object	
			this.scrapedBibtex = context.getBibtexResult();
			
			logger.debug("parsing bibtex \"" + this.scrapedBibtex + "\"");
			final BibTex parseBibTeX = new SimpleBibTeXParser().parseBibTeX(this.scrapedBibtex);
			
			if (id > 0) {
				parseBibTeX.setScraperId(id);
			}
			
			return parseBibTeX;
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

}
