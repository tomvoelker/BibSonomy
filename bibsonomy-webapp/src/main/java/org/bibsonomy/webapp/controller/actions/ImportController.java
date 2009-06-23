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
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.importer.bookmark.file.FirefoxImporter;
import org.bibsonomy.importer.bookmark.service.DeliciousImporterFactory;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.importer.FileBookmarkImporter;
import org.bibsonomy.services.importer.RemoteServiceBookmarkImporter;
import org.bibsonomy.util.file.FileUploadInterface;
import org.bibsonomy.util.file.HandleFileUpload;
import org.bibsonomy.webapp.command.actions.ImportCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author mwa
 * @version $Id$
 */
public class ImportController implements MinimalisticController<ImportCommand>{

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
	
	/**
	 * Main method which starts the import
	 */
	public View workOn(ImportCommand command) {
		
		List<Post<Bookmark>> posts = new LinkedList<Post<Bookmark>>();
		
		if(command.getImportType().equals("delicious")){
			try {
				DeliciousImporterFactory importerFactory = new DeliciousImporterFactory();
				RemoteServiceBookmarkImporter importer = importerFactory.getImporter();
				importer.setCredentials(command.getUserName(), command.getPassWord());
				posts = importer.getPosts();
			} catch (IOException ex) {
				log.error(ex.getMessage());
			}
		}else if(command.getImportType().equals("firefox")){
			try{
				File file = buildFile(command.getFile());
				fileImporter = new FirefoxImporter();
				fileImporter.initialize(file, command.getContext().getLoginUser(), command.getGrouping());
				posts = fileImporter.getPosts();
			} catch (IOException ex) {
				log.error(ex.getMessage());
			}
		}
				
		if(!posts.isEmpty()){
			storePosts(command, posts);
		}
	
		return Views.IMPORT_SUCCESS;
	
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
			List<?> singletonList = Collections.singletonList(post);
			String bookmarkUrl = post.getResource().getUrl();
			try {
				// throws an exception if the bookmark already exists in the
				// system
				List<String> createdPostHash = logic.createPosts((List<Post<?>>) singletonList);
				newBookmarkEntries.put(createdPostHash.get(0), bookmarkUrl);
			} catch (IllegalArgumentException e) {
				// checks whether the update bookmarks checkbox is checked
				if (command.isOverwrite()) {

					List<String> createdPostHash = logic.updatePosts((List<Post<?>>) singletonList, PostUpdateOperation.UPDATE_ALL);
					updatedBookmarkEntries.put(createdPostHash.get(0), bookmarkUrl);
				} else {
					nonCreatedBookmarkEntries.add(bookmarkUrl);
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
		return new ImportCommand();
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
	
}
