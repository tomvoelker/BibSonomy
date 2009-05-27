package org.bibsonomy.webapp.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.PostUpdateOperation;
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
import org.w3c.dom.NodeList;

import resources.Tag;

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

	@SuppressWarnings("unchecked")
	private void workOnImportTab(SettingsViewCommand command) {

		// retrieve and parse bookmark.html file

		User loginUser = command.getContext().getLoginUser();

		if (command.getFile() != null && loginUser != null) {

			// FileItem fileItem = command.getFile().getFileItem();

			final List<FileItem> list = new LinkedList<FileItem>();
			list.add(command.getFile().getFileItem());

			FileUploadInterface up;
			// try {
			try {
				handleBookmarkFile(list);
			} catch (Exception ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}

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

				try {
					this.upFile.write(bookmarkFile);

					this.upFile.delete();
				} catch (Exception e) {
					//TODO still to catch
				}

				List<Post<?>> bookmarksFromFirefox = null;
				
				try {
					bookmarksFromFirefox = getBookmarksFromFirefox(bookmarkFile, loginUser, command.getGrouping());
				} catch (FileNotFoundException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}

				bookmarkFile.delete();

				Iterator<Post<?>> bookmarkIt = bookmarksFromFirefox.iterator();

				List<String> createdPosts = new ArrayList<String>();

				while (bookmarkIt.hasNext()) {
					Post<?> nextBookmark = bookmarkIt.next();

					List<?> singletonList = Collections.singletonList(nextBookmark);

					List<String> createdPost = null;

					try {
						createdPost = this.logic.createPosts((List<Post<?>>) singletonList);
					} catch (IllegalArgumentException e) {
						if (command.getOverwrite()) {

							createdPost = this.logic.updatePosts((List<Post<?>>) singletonList, PostUpdateOperation.UPDATE_ALL);
						}
					}

					if (createdPost != null) {

						createdPosts.addAll(createdPost);
					}
				}
			} else {
				// TODO add error message to errors object
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

					// bookmark.setToIns(true);
					bookmarkPost.setUser(user);
					bookmarkPost.addGroup(groupName);
					// bookmark.setGroupid(groupid);
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
}
