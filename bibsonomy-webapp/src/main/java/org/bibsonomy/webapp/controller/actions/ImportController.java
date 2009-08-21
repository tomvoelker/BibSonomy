package org.bibsonomy.webapp.controller.actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.importer.bookmark.file.FirefoxImporter;
import org.bibsonomy.importer.bookmark.service.DeliciousImporterFactory;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.rest.utils.FileUploadInterface;
import org.bibsonomy.rest.utils.impl.FileUploadFactory;
import org.bibsonomy.rest.utils.impl.HandleFileUpload;
import org.bibsonomy.services.importer.FileBookmarkImporter;
import org.bibsonomy.services.importer.RelationImporter;
import org.bibsonomy.services.importer.RemoteServiceBookmarkImporter;
import org.bibsonomy.webapp.command.actions.ImportCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.ImportValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;

/**
 * @author mwa
 * @version $Id: ImportController.java,v 1.3 2009-06-23 14:23:15 voigtmannc Exp
 *          $
 */
public class ImportController implements MinimalisticController<ImportCommand>, ErrorAware, ValidationAwareController<ImportCommand> {

	private static final Log log = LogFactory.getLog(ImportController.class);

	/**
	 * logic interface for the database connectivity
	 */
	private LogicInterface logic;

	/**
	 * The factory used to get a Delicious Importer. 
	 */
	private DeliciousImporterFactory importerFactory;

	/**
	 * the factory used to get an instance of a FileUploadHandler.
	 */
	private FileUploadFactory uploadFactory;

	private Errors errors = null;

	public View workOn(ImportCommand command) {
		final RequestWrapperContext context = command.getContext();

		/*
		 * only users which are logged in might post -> send them to
		 * login page
		 */
		if (!context.isUserLoggedIn()) {
			/*
			 * FIXME: send user back to this controller
			 */
			return new ExtendedRedirectView("/login");
		}

		final User loginUser = context.getLoginUser();

		/*
		 * check credentials to fight CSRF attacks 
		 * 
		 */
		if (!context.isValidCkey()) {
			errors.reject("error.field.valid.ckey");
			/*
			 * FIXME: correct URL?
			 */
			return Views.IMPORT;
		}




		if (errors.hasErrors()) {
			return Views.IMPORT;
		}



		List<Post<Bookmark>> posts = new LinkedList<Post<Bookmark>>();
		List<Tag> relations = new LinkedList<Tag>();

		final String importType = command.getImportType();
		final String importData = command.getImportData();
		
		if ("delicious".equals(importType)) {
			try {

				/** import posts or bundles? **/
				if ("posts".equals(importData)) {
					final RemoteServiceBookmarkImporter importer = importerFactory.getBookmarkImporter();
					importer.setCredentials(command.getUserName(), command.getPassWord());
					posts = importer.getPosts();
				} else if ("bundles".equals(importData)) {
					final RelationImporter relationImporter = importerFactory.getRelationImporter();
					relationImporter.setCredentials(command.getUserName(), command.getPassWord());
					relations = relationImporter.getRelations();
				} else {
					/*
					 * FIXME: why did import fail (missing data?)? giv a hint to the user!
					 */
					errors.reject("error.import.failed");
				}
			} catch (IOException ex) {
				errors.reject("error.furtherInformations", new Object[]{ex.getMessage()}, "The following error occured: {0}");
				log.error("Delicious-Import failed.", ex);
			}
		} else if ("firefox".equals(importType)) {
			try {

				final FileUploadInterface uploadFileHandler = this.uploadFactory.getFileUploadHandler(Collections.singletonList(command.getFile().getFileItem()), HandleFileUpload.firefoxImportExt);

				final Document document = uploadFileHandler.writeUploadedFile();
				/*
				 * FileBookmarkImporter interface
				 */
				final FileBookmarkImporter fileImporter = new FirefoxImporter();
				fileImporter.initialize(document.getFile(), loginUser, command.getGrouping());
				posts = fileImporter.getPosts();
				
				/*
				 * clear temporary file
				 */
				document.getFile().delete();

			} catch (final Exception ex) {
				errors.reject("error.furtherInformations", new Object[]{ex.getMessage()}, "The following error occured: {0}");
			}
		}

		/** how many posts were found? **/
		command.setTotalCount(posts != null ? posts.size() : 0);

		/** store the posts **/
		if (posts.size() > 0) {
			storePosts(command, posts);
		}

		/** if available store relations **/
		if (relations.size() > 0) {
			storeRelations(relations, command);
		}

		return Views.IMPORT;
	}

	/**
	 * Store the received bundles into the database
	 * 
	 * @param relations
	 * @param command
	 */
	private void storeRelations(List<Tag> relations, ImportCommand command) {
		command.setStoredConcepts(new LinkedList<String>());
		for (Tag tag : relations) {
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
	@SuppressWarnings("unchecked")
	private void storePosts(ImportCommand command, List<Post<Bookmark>> posts) {

		// stores all newly added bookmarks
		final Map<String, String> newBookmarkEntries = new HashMap<String, String>();

		// stores all the updated bookmarks
		final Map<String, String> updatedBookmarkEntries = new HashMap<String, String>();

		// stores all the non imported bookmarks
		final List<String> nonCreatedBookmarkEntries = new ArrayList<String>();

		for (final Post<Bookmark> post : posts) {

			if (post.getUser() == null) {
				post.setUser(command.getContext().getLoginUser());
			}

			final List<?> singletonList = Collections.singletonList(post);
			final String title = post.getResource().getTitle();
			try {
				// throws an exception if the bookmark already exists in the
				// system
				final List<String> createdPostHash = logic.createPosts((List<Post<?>>) singletonList);
				newBookmarkEntries.put(createdPostHash.get(0), title);
			} catch (IllegalArgumentException e) {
				// checks whether the update bookmarks checkbox is checked
				if (command.isOverwrite()) {

					final List<String> createdPostHash = logic.updatePosts((List<Post<?>>) singletonList, PostUpdateOperation.UPDATE_ALL);
					updatedBookmarkEntries.put(createdPostHash.get(0), title);
				} else {
					nonCreatedBookmarkEntries.add(title);
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
	public ImportCommand instantiateCommand() {
		final ImportCommand command = new ImportCommand();
		command.setImportData("posts");
		return command;
	}

	/**
	 * @param logic
	 *            logic interface
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
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
	 * This factory returns pre-configured Delicious-Importers.
	 * 
	 * @return The factory.
	 */
	public DeliciousImporterFactory getImporterFactory() {
		return this.importerFactory;
	}

	/**
	 * This factory returns pre-configured Delicious-Importers.
	 * 
	 * @param importerFactory
	 */
	@Required
	public void setImporterFactory(DeliciousImporterFactory importerFactory) {
		this.importerFactory = importerFactory;
	}

	@Override
	public Validator<ImportCommand> getValidator() {
		return new ImportValidator();
	}

	@Override
	public boolean isValidationRequired(ImportCommand command) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @return FileUploadFactory
	 */
	public FileUploadFactory getUploadFactory() {
		return this.uploadFactory;
	}

	/**
	 * @param uploadFactory
	 */
	public void setUploadFactory(FileUploadFactory uploadFactory) {
		this.uploadFactory = uploadFactory;
	}

}
