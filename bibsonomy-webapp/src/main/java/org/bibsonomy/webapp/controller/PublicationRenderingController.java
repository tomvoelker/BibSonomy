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
package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.bibtex.parser.PostBibTeXParser;
import org.bibsonomy.common.exceptions.RestException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.ImportResource;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.util.BibTexReader;
import org.bibsonomy.model.util.data.Data;
import org.bibsonomy.model.util.data.DualDataWrapper;
import org.bibsonomy.rest.fileupload.FileUploadData;
import org.bibsonomy.webapp.command.actions.PublicationRendererCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.importer.PublicationImporter;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

import bibtex.parser.ParseException;

/**
 * @author rja
 */
public class PublicationRenderingController implements MinimalisticController<PublicationRendererCommand>, ErrorAware {

	private static final Log log = LogFactory.getLog(PublicationRenderingController.class);

	private PublicationImporter publicationImporter;

	private Errors errors;

	private Map<String,BibTexReader> bibtexReaders = Collections.emptyMap();

	@Override
	public PublicationRendererCommand instantiateCommand() {
		final PublicationRendererCommand postPublicationCommand = new PublicationRendererCommand();
		postPublicationCommand.setPost(new Post<BibTex>());
		postPublicationCommand.getPost().setResource(new BibTex());
		return postPublicationCommand;
	}

	@Override
	public View workOn(PublicationRendererCommand command) {

		List<Post<BibTex>> posts = null;
		
		if (present(command.getSelection()) || (present(command.getFile()) && (!bibtexReaders.containsKey(command.getFile().getContentType()))))  {
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
		} else if (present(command.getFile())) {
			final Data data = new FileUploadData(command.getFile());
			posts = importData(data);
		} else if (present(command.getMarc()) && present(command.getPica())) {
			final Data data = new DualDataWrapper(new FileUploadData(command.getMarc()), new FileUploadData(command.getPica()));
			posts = importData(data);
		} else {
			posts = Collections.singletonList(command.getPost());
		}
		
		if (errors.hasErrors()) return Views.ERROR;
		
		command.getBibtex().setList(posts);

		return Views.getViewByFormat(command.getFormat());
	}

	public List<Post<BibTex>> importData(final Data data) {
		BibTexReader reader = bibtexReaders.get(data.getMimeType());
		if (reader == null) {
			throw new RestException(0, "", "");
		}
		List<Post<BibTex>> posts;
		Collection<? extends BibTex> bibTexs;
		bibTexs = reader.read(new ImportResource(data));
		posts = new ArrayList<Post<BibTex>>(bibTexs.size());
		for (BibTex b : bibTexs) {
			Post<BibTex> p = new Post<BibTex>();
			p.setTags(Collections.<Tag>emptySet());
			p.setResource(b);
			posts.add(p);
		}
		return posts;
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

	/**
	 * @return the mimeTypeReaders
	 */
	public Map<String, BibTexReader> getBibtexReaders() {
		return this.bibtexReaders;
	}

	/**
	 * @param mimeTypeReaders the mimeTypeReaders to set
	 */
	public void setBibtexReaders(Map<String, BibTexReader> mimeTypeReaders) {
		this.bibtexReaders = mimeTypeReaders;
	}

}
