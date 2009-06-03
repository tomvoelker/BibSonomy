package org.bibsonomy.webapp.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.util.XmlUtils;
import org.bibsonomy.util.file.FileUploadInterface;
import org.bibsonomy.util.file.HandleFileUpload;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import resources.Tag;

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
			//called by the my profile tab
			workOnMyProfileTab(command);
			break;
		}
		case 1: {
			//called by the setting tab
			workOnSettingsTab(command);
			break;
		}
		case 2: {
			//called by the importation tab
			workOnImportTab(command);
			break;
		}
		default: {
			errors.reject("error.settings.tab");
			break;
		}
		}
		
		if(errors.hasErrors()) {
			return Views.ERROR;
		}

		return Views.SETTINGSPAGE;
	}
	
	private void workOnMyProfileTab(SettingsViewCommand command) {
		
	}

	private void workOnImportTab(SettingsViewCommand command) {

		// retrieve and parse bookmark.html file

		User loginUser = command.getContext().getLoginUser();

		if (loginUser != null) {

			//firefox import
			if ("firefox".equals(command.getImportType())) {

				importFirefoxBookmarks(command, loginUser);

			
			}
			//delicious import
			else if ("delicious".equals(command.getImportType())) {
				
			}
			//jabref import
			else if ("jabref".equals(command.getImportType())) {

			}
		}

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
				//writes the file into the temporary directory and returns a handle of the file object
				bookmarkFile = up.writeUploadedFilesAndReturnFile(this.docpath);

			} catch (Exception e) {
				// TODO still to catch
			}

			List<Post<?>> bookmarksFromFirefox = null;

			try {
				//extracts all the bookmarks in the file
				bookmarksFromFirefox = getBookmarksFromFirefox(bookmarkFile, loginUser, command.getGrouping());
			} catch (FileNotFoundException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}

			bookmarkFile.delete();

			Iterator<Post<?>> bookmarkIt = bookmarksFromFirefox.iterator();

			//stores all newly added bookmarks
			Map<String, String> newBookmarkEntries = new HashMap<String, String>();

			//stores all the updated bookmarks
			Map<String, String> updatedBookmarkEntries = new HashMap<String, String>();

			//stores all the non imported bookmarks
			List<String> nonCreatedBookmarkEntries = new ArrayList<String>();

			while (bookmarkIt.hasNext()) {
				Post<?> nextBookmark = bookmarkIt.next();

				List<?> singletonList = Collections.singletonList(nextBookmark);

				String bookmarkUrl = ((Post<Bookmark>) singletonList.get(0)).getResource().getUrl();

				try {
					//throws an exception if the bookmark already exists in the system
					List<String> createdPostHash = this.logic.createPosts((List<Post<?>>) singletonList);
					newBookmarkEntries.put(createdPostHash.get(0), bookmarkUrl);
				} catch (IllegalArgumentException e) {
					//checks whether the update bookmarks checkbox is checked
					if (command.getOverwrite()) {

						List<String> createdPostHash = this.logic.updatePosts((List<Post<?>>) singletonList, PostUpdateOperation.UPDATE_ALL);
						updatedBookmarkEntries.put(createdPostHash.get(0), bookmarkUrl);
					} else {
						nonCreatedBookmarkEntries.add(bookmarkUrl);
					}
				}
			}
			
			//stores the result to the command object, that the data can be accessed by the jsp side
			if(newBookmarkEntries.size() > 0) {
				command.setNewBookmarks(newBookmarkEntries);
			}
			//stores the result to the command object, that the data can be accessed by the jsp side
			if(updatedBookmarkEntries.size() > 0) {
				command.setUpdatedBookmarks(updatedBookmarkEntries);
			}
			//stores the result to the command object, that the data can be accessed by the jsp side
			if(nonCreatedBookmarkEntries.size() > 0) {
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

	private List<Post<?>> getBookmarksFromFirefox(File bookmarkFile, User currUser, String groupName) throws FileNotFoundException {

		final Document document = XmlUtils.getDOM(new FileInputStream(bookmarkFile));

		// DEBUG INFOS ERZEUGEN
		// File fout = new File("Desktop/TEST_OUT.html");
		// FileOutputStream out = new FileOutputStream(fout);
		// org.w3c.dom.Document document = tidy.parseDOM(in, out);

		// get first DL-node containing all links and folders
		try {
			final Node mainFolder = document.getElementsByTagName("body").item(0).getChildNodes().item(1);
			if (mainFolder != null) {
				return createBookmarks(mainFolder, null, null, currUser, groupName);
			}
		} catch (final Exception e) {
			log.fatal("Error on importing FireFox bookmarks: " + e);
		}

		return null;
	}

	/**
	 * Parses a given node and extracts all links and folders. Uppertags
	 * contains all tags provided by nodes above the given node (folder).
	 * Bookmarks is requiered because createBookmarks works recursively.
	 * 
	 * @param Node
	 *            folder
	 * @param Vector
	 *            <String> upperTags
	 * @param LinkedList
	 *            <Bookmark>bookmarks
	 * @return
	 */
	private List<Post<?>> createBookmarks(Node folder, Vector<String> upperTags, List<Post<?>> bookmarks, User user, String groupName) {
		// if no add_time attribute can be found fakeDate is used
		Date fakeDate = new Date();
		// if this method is called for the first time bookmarks has to become
		// initialized
		if (bookmarks == null) {
			bookmarks = new LinkedList<Post<?>>();
		}
		// every node requires his own tags
		Vector<String> tags;
		// if tags are provided by upper nodes these tags belong to this node
		// too
		if (upperTags != null) {
			tags = (Vector<String>) upperTags.clone();
		}
		// if no tags are provided create a new vector
		else {
			tags = new Vector<String>();
		}
		// nodelist to parse all children of the given node
		NodeList children = folder.getChildNodes();
		// String to save a foldername if its name is given in a sibling of the
		// concerning DL
		String sepTag = "";

		for (int i = 0; i < children.getLength(); i++) {
			Node currentNode = children.item(i);
			// connect all upper tags with the currentNode
			Vector<String> myTags = (Vector<String>) tags.clone();
			if (!"".equals(sepTag)) {
				myTags.add(sepTag);
			}

			// is currentNode a folder?
			if ("dd".equals(currentNode.getNodeName())) {
				NodeList secondGen = currentNode.getChildNodes();
				// only containing a name?
				// yes, keep tag
				if (secondGen.getLength() == 1 && "h3".equals(secondGen.item(0).getNodeName())) {
					sepTag = secondGen.item(0).getFirstChild().getNodeValue().replaceAll("->|<-|\\s", "_");
				} else if (secondGen.getLength() > 1) { // filtert dd-knoten,
					// die nur einen
					// p-knoten besitzen
					// else find all folders an theis names
					for (int j = 0; j < secondGen.getLength(); j++) {
						Node son = secondGen.item(j);
						if ("h3".equals(son.getNodeName())) {
							// if sepTag != "" remove last added tag and reset
							// sepTag
							if (!"".equals(sepTag)) {
								myTags.remove(sepTag);
								sepTag = "";
							}
							// if upperTags != myTags, a parallel branch was
							// parsed -> reset myTags
							if (tags.size() != myTags.size()) {
								myTags = tags;
							}
							// add a found tag
							myTags.add(son.getFirstChild().getNodeValue().replaceAll("->|<-|\\s", "_"));
						}
						// all dl-nodes are new folders
						if ("dl".equals(son.getNodeName())) {
							// create bookmarks from new found node
							createBookmarks(son, myTags, bookmarks, user, groupName);
						}
					}// for(int j=...
				}// else if
			}// if ("dd".equals....
			// if its no folder.... is it a link?

			/*
			 * sometimes the tidy parser decides that <dt></dt> has childnodes
			 * ... need to check if the childnode of <dt> is an <a> to avoid
			 * NullPointerExceptions!!!!
			 */
			else if ("dt".equals(currentNode.getNodeName()) && "a".equals(currentNode.getFirstChild().getNodeName())) {
				// it is a link
				// create bookmark-object

				// need to check if the <a>-Tag has a name (ChildNodes) i.e. <a
				// href="http://www.foo.bar"></a> causes a failure
				if (currentNode.getFirstChild().hasChildNodes() == true) {
					Post<Bookmark> bookmarkPost = new Post<Bookmark>();
					bookmarkPost.setResource(new Bookmark());
					bookmarkPost.getResource().setTitle(currentNode.getFirstChild().getFirstChild().getNodeValue());
					bookmarkPost.getResource().setUrl(currentNode.getFirstChild().getAttributes().getNamedItem("href").getNodeValue());
					// add tags/relations to bookmark
					if (upperTags != null) {
						// only 1 tag found -> add a tag
						if (upperTags.size() == 1) {

							// bookmark.setTags(upperTags.elementAt(0));
							bookmarkPost.addTag(upperTags.elementAt(0));
						} else {
							// more tags found -> add relations
							for (int tagCount = 0; tagCount < upperTags.size() - 1; tagCount++) {
								String upper = upperTags.elementAt(tagCount);
								String lower = upperTags.elementAt(tagCount + 1);
								// bookmark.addTagRelation(lower, upper);
								bookmarkPost.addTag(upper);
								bookmarkPost.addTag(lower);

							}
						}
					} else {
						/*
						 * link found in "root-folder" -> no folder hierarchy
						 * found
						 * 
						 * check for "TAGS" attribute (common in del.icio.us
						 * export)
						 */
						final Node tagNode = currentNode.getFirstChild().getAttributes().getNamedItem("tags");
						if (tagNode != null) {
							/*
							 * del.icio.us export tags are comma-separated
							 */
							final StringTokenizer token = new StringTokenizer(tagNode.getNodeValue(), ",");
							while (token.hasMoreTokens()) {
								bookmarkPost.addTag(token.nextToken());
							}
						} else {
							// really no tags found -> set imported tag
							// bookmark.setTags(Tag.IMPORTED_TAG);
							bookmarkPost.addTag(Tag.IMPORTED_TAG);
						}
					}
					bookmarkPost.setDate(fakeDate);
					bookmarkPost.setUser(user);
					bookmarkPost.addGroup(groupName);

					// descriptions are saved in a sibling of of a node
					// containing a link
					if (currentNode.getNextSibling() != null && "dd".equals(currentNode.getNextSibling().getNodeName())) {
						// bookmark.setExtended(currentNode.getNextSibling().getFirstChild().getNodeValue());
						bookmarkPost.setDescription(currentNode.getNextSibling().getFirstChild().getNodeValue());
					}
					bookmarks.add(bookmarkPost);
				}
			}
		}
		return bookmarks;
	}

	public UserSettings getUserSettings() {
		return this.userSettings;
	}

	public void setUserSettings(UserSettings userSettings) {
		this.userSettings = userSettings;
	}
}
