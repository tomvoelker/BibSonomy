package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.bibtex.parser.PostBibTeXParser;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.webapp.command.actions.PostPublicationCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.importer.PublicationImporter;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

import bibtex.parser.ParseException;

/**
 * @author rja
 * @version $Id$
 */
public class PublicationRenderingController implements MinimalisticController<PostPublicationCommand>, ErrorAware {

	private static final Log log = LogFactory.getLog(PublicationRenderingController.class);

	private PublicationImporter publicationImporter;

	private Errors errors;

	@Override
	public PostPublicationCommand instantiateCommand() {
		final PostPublicationCommand postPublicationCommand = new PostPublicationCommand();
		postPublicationCommand.setPost(new Post<BibTex>());
		postPublicationCommand.getPost().setResource(new BibTex());
		return postPublicationCommand;
	}

	@Override
	public View workOn(PostPublicationCommand command) {

		List<Post<BibTex>> posts = null;
		
		if (present(command.getSelection()) || present(command.getFile())) {
			final String snippet;
			if (present(command.getSelection())) {
				/*
				 * The user has entered text into the snippet selection - we use that
				 */
				log.debug("user has filled selection");
				snippet = this.publicationImporter.handleSelection(command.getSelection());
			} else {
				// command.getFile() exists
				/*
				 * The user uploads a BibTeX or EndNote file
				 */
				log.debug("user uploads a file");
				// get the (never empty) content or add corresponding errors
				snippet = this.publicationImporter.handleFileUpload(command, this.errors);
			} 
			/*
			 * configure the parser
			 */
			final PostBibTeXParser parser = new PostBibTeXParser();
			parser.setDelimiter(command.getDelimiter());
			parser.setWhitespace(command.getWhitespace());
			parser.setTryParseAll(true);

			/*
			 * FIXME: why aren't commas, etc. removed?
			 */



			try {
				/*
				 * Parse the BibTeX snippet
				 */
				posts = parser.parseBibTeXPosts(snippet);
			} catch (final ParseException ex) {
				errors.reject("error.upload.failed.parse", ex.getMessage());
			} catch (final IOException ex) {
				errors.reject("error.upload.failed.parse", ex.getMessage());
			}

			if (errors.hasErrors()) return Views.ERROR;

		} else {
			posts = Collections.singletonList(command.getPost());
		}
		command.getBibtex().setList(posts);

		return Views.getViewByFormat(command.getFormat());
	}

	public PublicationImporter getPublicationImporter() {
		return this.publicationImporter;
	}

	public void setPublicationImporter(PublicationImporter publicationImporter) {
		this.publicationImporter = publicationImporter;
	}

	@Override
	public Errors getErrors() {
		return errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
	}

}
