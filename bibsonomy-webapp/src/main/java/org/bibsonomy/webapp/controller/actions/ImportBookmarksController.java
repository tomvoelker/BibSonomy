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

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.errors.DuplicatePostErrorMessage;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.exceptions.DatabaseException;
import org.bibsonomy.common.exceptions.UnsupportedFileTypeException;
import org.bibsonomy.importer.bookmark.file.BrowserImporter;
import org.bibsonomy.importer.bookmark.service.DeliciousImporterFactory;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.filesystem.FileLogic;
import org.bibsonomy.services.filesystem.extension.ListExtensionChecker;
import org.bibsonomy.services.importer.FileBookmarkImporter;
import org.bibsonomy.services.importer.RelationImporter;
import org.bibsonomy.services.importer.RemoteServiceBookmarkImporter;
import org.bibsonomy.util.file.ServerUploadedFile;
import org.bibsonomy.webapp.command.actions.ImportCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.ImportValidator;
import org.bibsonomy.webapp.view.Views;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.Errors;

/**
 * TODO: use validator to check for errors (validator never invoked)
 * where do we want to display the errors? on the settings page?
 * 
 * @author mwa
 */
public class ImportBookmarksController implements ErrorAware, ValidationAwareController<ImportCommand> {
	private static final Log log = LogFactory.getLog(ImportBookmarksController.class);

	private static final ListExtensionChecker EXTENSION_CHECKER = new ListExtensionChecker(FileLogic.BROWSER_IMPORT_EXTENSIONS);

	/**
	 * logic interface for the database connectivity
	 */
	private LogicInterface logic;

	/**
	 * The factory used to get a Delicious Importer. 
	 */
	private DeliciousImporterFactory importerFactory;

	/**
	 * the file logic to use
	 */
	private FileLogic fileLogic;

	private Errors errors = null;

	@Override
	public View workOn(final ImportCommand command) {
		final RequestWrapperContext context = command.getContext();

		/*
		 * only users which are logged in might post -> send them to
		 * login page
		 */
		if (!context.isUserLoggedIn()) {
			throw new AccessDeniedException("please log in");
		}

		final User loginUser = context.getLoginUser();
		
		final String importType = command.getImportType();
		final boolean isDelicous = importType.equals("delicous");
		final boolean isBrowser = importType.equals("browser");

		/*
		 * check credentials to fight CSRF attacks 
		 * 
		 */
		if (!context.isValidCkey() && (isDelicous || (isBrowser && command.getTotalCount() > 0))) {
			errors.reject("error.field.valid.ckey");
			/*
			 * FIXME: correct URL?
			 * FIXME: don't do this on first call of form!
			 */
			return Views.IMPORT;
		}

		if (errors.hasErrors()) {
			return Views.IMPORT;
		}

		List<Post<Bookmark>> posts = new LinkedList<Post<Bookmark>>();
		List<Tag> relations = new LinkedList<Tag>();
		
		try {
			if (isDelicous) {
				/*
				 * TODO: we want to have checkboxes, not radio buttons!
				 */
				final String importData = command.getImportData();
				/*
				 * import posts/bundles from Delicious
				 */
				if ("posts".equals(importData)) {
					final RemoteServiceBookmarkImporter importer = importerFactory.getBookmarkImporter();
					importer.setCredentials(command.getImportUsername(), command.getImportPassword());
					posts = importer.getPosts();
				} 
				if ("bundles".equals(importData)) {
					final RelationImporter relationImporter = importerFactory.getRelationImporter();
					relationImporter.setCredentials(command.getImportUsername(), command.getImportPassword());
					relations = relationImporter.getRelations();
				} 

			} else if (isBrowser) {
				/*
				 * import posts/relations from Firefox, Safari, Opera, Chrome
				 */
				final File file = this.fileLogic.writeTempFile(new ServerUploadedFile(command.getFile()), EXTENSION_CHECKER);
				final FileBookmarkImporter fileImporter = new BrowserImporter();
				fileImporter.initialize(file, loginUser, command.getGroup());
				posts = fileImporter.getPosts();
				/*
				 * clear temporary file
				 */
				this.fileLogic.deleteTempFile(file.getName());
			} else {
				log.info("unknown import type '" + importType + "'");
			}
		/*
		 * FIXME: too general error keys!
		 */
		} catch (final UnsupportedFileTypeException ex) {
			errors.reject("error.furtherInformations", new Object[]{ex.getMessage()}, "The following error occurred: {0}");
		} catch (final Exception ex) {
			errors.reject("error.furtherInformations", new Object[]{ex.getMessage()}, "The following error occurred: {0}");
			log.warn("Delicious/Browser-Import failed: " + ex.getMessage());
		}

		/* store the posts */
		if (present(posts)) {
			this.storePosts(command, posts);

			/* how many posts were found? */
			command.setTotalCount(posts.size());
		}

		/* if available store relations */
		if (present(relations)) {
			this.storeRelations(relations, command);

			/* how many bundles were found? */
			command.setTotalCount(relations.size());
		}

		return Views.IMPORT;
	}

	/**
	 * Store the received bundles into the database
	 * 
	 * @param relations
	 * @param command
	 */
	private void storeRelations(final List<Tag> relations, final ImportCommand command) {
		command.setStoredConcepts(new LinkedList<String>());
		for (final Tag tag : relations) {
			final String conceptName = this.logic.createConcept(tag, GroupingEntity.USER, command.getContext().getLoginUser().getName());
			command.getStoredConcepts().add(conceptName);
		}
	}

	/**
	 * Method stores a list of posts into the database
	 * 
	 * @param command
	 * @param posts
	 */
	private void storePosts(final ImportCommand command, final List<Post<Bookmark>> posts) {
		// stores all newly added bookmarks
		final Map<String, String> newBookmarkEntries = new HashMap<String, String>();

		// stores all the updated bookmarks
		final Map<String, String> updatedBookmarkEntries = new HashMap<String, String>();

		// stores all the non imported bookmarks
		final Map<String, String> nonCreatedBookmarkEntries = new HashMap<String, String>();
		
		// store the posts one by one
		for (final Post<Bookmark> post : posts) {
			
			if (post.getUser() == null) {
				post.setUser(command.getContext().getLoginUser());
			}
			
			final List<Post<?>> singletonList = Collections.<Post<?>>singletonList(post);
			final String title = post.getResource().getTitle();
			try {
				// throws an exception if the bookmark already exists in the
				// system
				final List<String> createdPostHash = logic.createPosts(singletonList);
				newBookmarkEntries.put(createdPostHash.get(0), title);
			} catch (final DatabaseException de) {
				// an error occured: handle duplicates throw all other 
				for (final String hash: de.getErrorMessages().keySet()) {
					for (final ErrorMessage errorMessage: de.getErrorMessages(hash)) {
						if (errorMessage instanceof DuplicatePostErrorMessage) {
							// duplicate post detected => handle this
							// check whether the update bookmarks checkbox is checked
							if (command.isOverwrite()) {
								final List<String> createdPostHash = logic.updatePosts(singletonList, PostUpdateOperation.UPDATE_ALL);
								updatedBookmarkEntries.put(createdPostHash.get(0), title);
							} else {
								nonCreatedBookmarkEntries.put(hash, title);
							}
						} else {
							// something else went wrong => don't handle this
							throw de;
						}
					}
					
				}
			}
		}

		// stores the result to the command object, that the data can be
		// accessed by the jsp side
		if (newBookmarkEntries.size() > 0) {
			command.setNewBookmarks(newBookmarkEntries);
		}
		// stores the result to the command object, that the data can be
		// accessed by the jsp side
		if (updatedBookmarkEntries.size() > 0) {
			command.setUpdatedBookmarks(updatedBookmarkEntries);
		}
		// stores the result to the command object, that the data can be
		// accessed by the jsp side
		if (nonCreatedBookmarkEntries.size() > 0) {
			command.setNonCreatedBookmarks(nonCreatedBookmarkEntries);
		}

	}

	/**
	 * Return a new instance of an ImportCommand
	 */
	@Override
	public ImportCommand instantiateCommand() {
		final ImportCommand command = new ImportCommand();
		command.setImportData("posts");
		return command;
	}

	/**
	 * @param logic
	 *            logic interface
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}

	@Override
	public Errors getErrors() {
		return errors;
	}

	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}

	/**
	 * This factory returns pre-configured Delicious-Importers.
	 * 
	 * @param importerFactory
	 */
	@Required
	public void setImporterFactory(final DeliciousImporterFactory importerFactory) {
		this.importerFactory = importerFactory;
	}

	@Override
	public Validator<ImportCommand> getValidator() {
		return new ImportValidator();
	}

	@Override
	public boolean isValidationRequired(final ImportCommand command) {
		return false;
	}

	/**
	 * @param fileLogic the fileLogic to set
	 */
	public void setFileLogic(FileLogic fileLogic) {
		this.fileLogic = fileLogic;
	}
}
