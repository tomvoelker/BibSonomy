package org.bibsonomy.webapp.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.importer.bookmark.file.FirefoxImporter;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.services.importer.FileBookmarkImporter;
import org.bibsonomy.util.file.FileUploadInterface;
import org.bibsonomy.util.file.HandleFileUpload;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * @author Steffen
 * @version $Id: SettingsPageController.java,v 1.2 2009-05-20 12:03:21
 *          voigtmannc Exp $
 */
public class SettingsPageController implements MinimalisticController<SettingsViewCommand>, ErrorAware {

	private static final Log log = LogFactory.getLog(SearchPageController.class);

	/**
	 * logic interface for the database connectivity
	 */
	private LogicInterface logic;

	/**
	 * path of the document
	 */
	private String docpath;

	/**
	 * hold current errors
	 */
	private Errors errors = null;

	/**
	 * settings of the user
	 */
	private UserSettings userSettings;

	/**
	 * @param command
	 * @return the view
	 */
	public View workOn(SettingsViewCommand command) {

		command.setPageTitle("settings");

		switch (command.getSelTab()) {
		case 0: {
			// called by the my profile tab
			workOnMyProfileTab(command);
			break;
		}
		case 1: {
			// called by the setting tab
			workOnSettingsTab(command);
			break;
		}
		case 2: {
			// called by the importation tab
			workOnImportTab(command);

			if (errors.hasErrors()) {
				return Views.ERROR;
			}
			break;
		}
		default: {
			errors.reject("error.settings.tab");
			break;
		}
		}

		if (errors.hasErrors()) {

			if (errors.hasFieldErrors("error.general.login")) {

				return Views.SETTINGSPAGE;
			}

			return Views.ERROR;
		}

		return Views.SETTINGSPAGE;
	}

	private void workOnMyProfileTab(SettingsViewCommand command) {

	}

	private void workOnImportTab(SettingsViewCommand command) {

		// retrieve and parse bookmark.html file

		User loginUser = command.getContext().getLoginUser();

		if (loginUser.getName() != null) {

			// firefox import
			if ("firefox".equals(command.getImportType())) {

				importFirefoxBookmarks(command, loginUser);

			}
			// delicious import
			else if ("delicious".equals(command.getImportType())) {

			}
			// jabref import
			else if ("jabref".equals(command.getImportType())) {

				importJabrefLayout(command, loginUser);
			}
		} else {
			errors.reject("error.general.login");
		}
	}

	private void importJabrefLayout(SettingsViewCommand command, User loginUser) {

	}

	@SuppressWarnings("unchecked")
	private void importFirefoxBookmarks(SettingsViewCommand command, User loginUser) {

		// checks whether a file for import is chosen or not
		if (command.getFile() != null && command.getFile().getSize() > 0) {

			final List<FileItem> list = new LinkedList<FileItem>();
			// retrieves chosen import file
			list.add(command.getFile().getFileItem());

			FileUploadInterface up = null;

			try {

				up = new HandleFileUpload(list, HandleFileUpload.firfoxImportExt);
			} catch (Exception importEx) {
				// TODO Auto-generated catch block
				importEx.printStackTrace();
			}

			File bookmarkFile = null;

			try {
				// writes the file into the temporary directory and returns a
				// handle of the file object
				bookmarkFile = up.writeUploadedFilesAndReturnFile(this.docpath);

			} catch (Exception e) {
				// TODO still to catch
			}

			List<Post<Bookmark>> bookmarksFromFirefox = null;

			// extracts all the bookmarks in the file
			FileBookmarkImporter firefoxBookmarkImporter = new FirefoxImporter();
			
			try {
				firefoxBookmarkImporter.initialize(bookmarkFile, loginUser, command.getGrouping());
				bookmarksFromFirefox = firefoxBookmarkImporter.getPosts();
			} catch (IOException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}

			bookmarkFile.delete();

			Iterator<Post<Bookmark>> bookmarkIt = bookmarksFromFirefox.iterator();

			// stores all newly added bookmarks
			Map<String, String> newBookmarkEntries = new HashMap<String, String>();

			// stores all the updated bookmarks
			Map<String, String> updatedBookmarkEntries = new HashMap<String, String>();

			// stores all the non imported bookmarks
			List<String> nonCreatedBookmarkEntries = new ArrayList<String>();

			while (bookmarkIt.hasNext()) {
				Post<?> nextBookmark = bookmarkIt.next();

				List<?> singletonList = Collections.singletonList(nextBookmark);

				String bookmarkUrl = ((Post<Bookmark>) singletonList.get(0)).getResource().getUrl();

				try {
					// throws an exception if the bookmark already exists in the
					// system
					List<String> createdPostHash = this.logic.createPosts((List<Post<?>>) singletonList);
					newBookmarkEntries.put(createdPostHash.get(0), bookmarkUrl);
				} catch (IllegalArgumentException e) {
					// checks whether the update bookmarks checkbox is checked
					if (command.getOverwrite()) {

						List<String> createdPostHash = this.logic.updatePosts((List<Post<?>>) singletonList, PostUpdateOperation.UPDATE_ALL);
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
	}

	private void workOnSettingsTab(SettingsViewCommand command) {
		command.getContext().getLoginUser().getSettings().getTagboxStyle();
		command.getContext().getLoginUser().getSettings().getTagboxSort();
	}

	/**
	 * @param logic
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @return the current command
	 */
	public SettingsViewCommand instantiateCommand() {
		final SettingsViewCommand command = new SettingsViewCommand();
		command.setGrouping(GroupUtils.getPublicGroup().getName());
		return command;
	}

	@Override
	public Errors getErrors() {

		return this.errors;
	}

	@Override
	public void setErrors(Errors errors) {

		this.errors = errors;
	}

	public String getDocpath() {
		return this.docpath;
	}

	public void setDocpath(String docpath) {
		this.docpath = docpath;
	}

	public UserSettings getUserSettings() {
		return this.userSettings;
	}

	public void setUserSettings(UserSettings userSettings) {
		this.userSettings = userSettings;
	}
}
