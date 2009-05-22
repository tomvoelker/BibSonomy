package org.bibsonomy.webapp.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.util.HashUtils;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.XmlUtils;
import org.bibsonomy.util.file.FileUploadInterface;
import org.bibsonomy.util.file.FileUtil;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author Steffen
 * @version $Id: SettingsPageController.java,v 1.2 2009-05-20 12:03:21
 *          voigtmannc Exp $
 */
public class SettingsPageController implements MinimalisticController<SettingsViewCommand>, ErrorAware {
	// TODO
	private static final Log log = LogFactory.getLog(SearchPageController.class);

	private LogicInterface logic;

	private UserSettings userSettings;

	private String docpath;

	/**
	 * hold current errors
	 */
	private Errors errors = null;

	private final Map<String, FileItem> fieldMap = null;
	private String fileHash;
	private String fileName;
	private String md5hash;
	private FileItem upFile;

	/**
	 * @param command
	 * @return the view
	 */
	public View workOn(SettingsViewCommand command) {
		log.info("WorkOn called");
		log.debug(this.getClass().getSimpleName());
		// TODO i18n
		command.setPageTitle("settings");
		log.info("Command getGrouping: " + command.getGrouping());
		log.info("importType: " + command.getImportType());
		log.info("overwrite: " + command.getOverwrite());
		log.info("file: " + command.getFile());

		switch (command.getSelTab()) {
		case 0: {
			break;
		}
		case 1: {
			workOnSettingsTab(command);
			break;
		}
		case 2: {
			workOnImportTab(command);
			break;
		}
		default: {
			errors.reject("error.settings.tab");
			break;
		}
		}

		return Views.SETTINGSPAGE;
	}

	private void workOnImportTab(SettingsViewCommand command) {

		// retrieve and parse bookmark.html file

		User loginUser = command.getContext().getLoginUser();

		if (command.getFile() != null && loginUser != null) {

			// FileItem fileItem = command.getFile().getFileItem();

			final List<FileItem> list = new LinkedList<FileItem>();
			list.add(command.getFile().getFileItem());

			FileUploadInterface up;
			try {
				handleBookmarkFile(list);

				String bookmarkFileName = this.fileName;

				if (!bookmarkFileName.equals("") && StringUtils.matchExtension(bookmarkFileName, "html")) {

					int groupID = Integer.MIN_VALUE;

					if ("private".equals(command.getGrouping())) {

						groupID = GroupUtils.getPrivateGroup().getGroupId();
					} else if ("public".equals(command.getGrouping())) {

						groupID = GroupUtils.getPublicGroup().getGroupId();
					}

					String pathToTmpSore = FileUtil.getDocumentPath(this.docpath, this.fileHash);

					File bookmarkFile = new File(pathToTmpSore);
					
					try{
						this.upFile.write(bookmarkFile);
						
						this.upFile.delete();
						
						List<Post<Bookmark>> bookmarksFromFirefox = getBookmarksFromFirefox(bookmarkFile, loginUser.getName(), groupID);
						
					}catch(IOException e) {
						//TODO Exception handling still missing
					}

				} else {
					// TODO add error message to errors object
				}

			} catch (Exception ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
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
	 * @param userSettings
	 */
	public void setUserSettings(UserSettings userSettings) {
		this.userSettings = userSettings;
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

	private void handleBookmarkFile(final List<FileItem> items) throws Exception {
		Map<String, FileItem> fieldMap = new HashMap<String, FileItem>();

		// copy items into global field map
		for (final FileItem temp : items) {
			if ("file".equals(temp.getFieldName())) {
				fieldMap.put(temp.getFieldName(), temp);
			}
		}

		this.upFile = fieldMap.get("file");
		final String filename = this.upFile.getName();
		if (filename != null) {
			this.fileName = FilenameUtils.getName(filename);
		} else {
			this.fileName = "";
		}

		// check file extensions which we accept
		if (this.fileName.equals("") || !StringUtils.matchExtension(this.fileName, "html")) {
			throw new Exception("Please check your file. Only html files are accepted.");
		}

		// format date
		final Date currDate = new Date();
		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		df.setTimeZone(TimeZone.getDefault());
		final String currDateFormatted = df.format(currDate);

		// create hash over file content
		this.md5hash = HashUtils.getMD5Hash(this.upFile.get());

		this.fileHash = StringUtils.getMD5Hash(this.upFile.getFieldName() + Math.random() + currDateFormatted);
	}

	private List<Post<Bookmark>> getBookmarksFromFirefox(File bookmarkFile, String currUser, int groupid) throws FileNotFoundException {

		final Document document = XmlUtils.getDOM(new FileInputStream(bookmarkFile));

		// DEBUG INFOS ERZEUGEN
		// File fout = new File("Desktop/TEST_OUT.html");
		// FileOutputStream out = new FileOutputStream(fout);
		// org.w3c.dom.Document document = tidy.parseDOM(in, out);

		// get first DL-node containing all links and folders
		try {
			final Node mainFolder = document.getElementsByTagName("body").item(0).getChildNodes().item(1);
			if (mainFolder != null) {
				return createBookmarks(mainFolder, null, null, currUser, groupid);
			}
		} catch (final Exception e) {
			log.fatal("Error on importing FireFox bookmarks: " + e);
		}

		return null;
	}

	private List<Post<Bookmark>> createBookmarks(Node mainFolder, Object object, Object object2, String currUser, int groupid) {
		// TODO Auto-generated method stub
		return null;
	}
}
