package org.bibsonomy.webapp.controller.actions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.importer.bookmark.file.FirefoxImporter;
import org.bibsonomy.importer.bookmark.service.DeliciousImporterFactory;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.importer.FileBookmarkImporter;
import org.bibsonomy.services.importer.RelationImporter;
import org.bibsonomy.services.importer.RemoteServiceBookmarkImporter;
import org.bibsonomy.util.file.FileUploadInterface;
import org.bibsonomy.util.file.HandleFileUpload;
import org.bibsonomy.webapp.command.actions.ImportCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.ImportValidator;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author mwa
 * @version $Id$
 */
public class ImportController implements MinimalisticController<ImportCommand>, ErrorAware{

	private static final Log log = LogFactory.getLog(ImportController.class);

	/**
	 * FileBookmarkImporter interface
	 */
	private FileBookmarkImporter fileImporter;
	
	/**
	 * logic interface for the database connectivity
	 */
	private LogicInterface logic;
		
	/**
	 * path of the document
	 */
	private String docpath;
		
	private Errors errors = null;
	
	/**
	 * Main method which starts the import
	 */
	public View workOn(ImportCommand command) {
		
		List<Post<Bookmark>> posts = new LinkedList<Post<Bookmark>>();
		List<Tag> relations = new LinkedList<Tag>();
		
		/** if importType is not set, the controller is loaded the first time
		 * 	and no data is submitted **/
		if(command.getImportType() != null){
			
			/** Validate the command **/
			ImportValidator validator = new ImportValidator();
			validator.validate(command, errors);
					
			/** display potential errors **/
			if(errors.hasErrors()){
				log.debug("Import couldn't get started due to existing errors in form");
				errors.reject("error.requiredFields");
				errors.rejectValue("errorMessage","error.requiredFields ");
				return Views.IMPORT;
			}
			
			if(command.getImportType().equals("delicious")){
				try {
					DeliciousImporterFactory importerFactory = new DeliciousImporterFactory();
					
					/** import posts or bundles? **/
					if(command.getImportData().equals("posts")){
						RemoteServiceBookmarkImporter importer = importerFactory.getImporter();
						importer.setCredentials(command.getUserName(), command.getPassWord());
						posts = importer.getPosts();
					}else if(command.getImportData().equals("bundles")){
						importerFactory.buildURL(DeliciousImporterFactory.BUNDLES_URL_PATH);
						RelationImporter relationImporter = (RelationImporter) importerFactory.getImporter();
						relationImporter.setCredentials(command.getUserName(), command.getPassWord());
						relations = relationImporter.getRelations();
					}else{
						errors.reject("error.import.failed");
						return Views.ERROR;
					}
					
				} catch (IOException ex) {
					errors.reject("error.furtherInformations");
					log.error("IOEXception at ImportController: "+ex.getMessage());
					command.setErrorMessage(ex.getMessage());
				}
			}else if(command.getImportType().equals("firefox")){
				try{
					File file = buildFile(command.getFile());
					fileImporter = new FirefoxImporter();
					fileImporter.initialize(file, command.getContext().getLoginUser(), command.getGrouping());
					posts = fileImporter.getPosts();
				
					file.delete();
				} catch (IOException ex) {
					errors.reject("error.furtherInformations");
					command.setErrorMessage(ex.getMessage());
				}
			}
		
			/** how many posts where found? **/
			command.setTotalCount(posts != null?posts.size():0);
						
			/** store the posts **/
			if(posts.size() > 0){
				storePosts(command, posts);	
			}
			
			/** if available store relations **/
			if(relations.size() > 0){
				storeRelations(relations, command);
			}
		}
		
		return Views.IMPORT;	
	}
	
	/**
	 * Store the received bundles into the database
	 * @param relations
	 * @param command
	 */
	private void storeRelations(List<Tag> relations, ImportCommand command){
		
		command.setStoredConcepts(new LinkedList<String>());
		for(Tag tag: relations){
			String conceptName = this.logic.createConcept(tag, GroupingEntity.USER, command.getContext().getLoginUser().getName());
			command.getStoredConcepts().add(conceptName);
		}
	}
	
	/**
	 * Method stores a list of posts into the database 
	 * @param command
	 * @param posts
	 */
	@SuppressWarnings("unchecked")
	private void storePosts(ImportCommand command, List<Post<Bookmark>> posts){
		
		// stores all newly added bookmarks
		Map<String, String> newBookmarkEntries = new HashMap<String, String>();
		
		// stores all the updated bookmarks
		Map<String, String> updatedBookmarkEntries = new HashMap<String, String>();
		
		// stores all the non imported bookmarks
		List<String> nonCreatedBookmarkEntries = new ArrayList<String>();
		
		for(Post<Bookmark> post: posts){
			 
			if(post.getUser() == null){
				post.setUser(command.getContext().getLoginUser());
			}
			
			List<?> singletonList = Collections.singletonList(post);
			String title = post.getResource().getTitle();
			try {
				// throws an exception if the bookmark already exists in the
				// system
				List<String> createdPostHash = logic.createPosts((List<Post<?>>) singletonList);
				newBookmarkEntries.put(createdPostHash.get(0), title);
			} catch (IllegalArgumentException e) {
				// checks whether the update bookmarks checkbox is checked
				if (command.isOverwrite()) {

					List<String> createdPostHash = logic.updatePosts((List<Post<?>>) singletonList, PostUpdateOperation.UPDATE_ALL);
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
	 * Build a File Object from a CommonsMultiPartFile Object
	 * 
	 * @param command
	 * @return
	 */
	private File buildFile(CommonsMultipartFile file) {

		final List<FileItem> list = new LinkedList<FileItem>();
		// retrieves chosen import file
		list.add(file.getFileItem());

		FileUploadInterface up = null;

		try {

			up = new HandleFileUpload(list, HandleFileUpload.firfoxImportExt);
		} catch (Exception importEx) {
			log.error(importEx.getMessage());
		}

		File bookmarkFile = null;

		try {
			// writes the file into the temporary directory and returns a
			// handle of the file object
			bookmarkFile = up.writeUploadedFilesAndReturnFile(this.docpath);

		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
		return bookmarkFile;

	}
	
	/**
	 * Return a new instance of an ImportCommand 
	 */
	public ImportCommand instantiateCommand() {
		ImportCommand command = new ImportCommand();
		command.setImportData("posts");
		return command;
	}

	/**
	 * @param logic logic interface
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @return a string
	 */
	public String getDocpath() {
		return this.docpath;
	}

	/**
	 * @param docpath
	 */
	public void setDocpath(String docpath) {
		this.docpath = docpath;
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
