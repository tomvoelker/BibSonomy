/**
 * BibSonomy-Importer - Various importers for bookmarks and publications.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.importer.bookmark.file;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.TagUtils;
import org.bibsonomy.services.importer.FileBookmarkImporter;
import org.bibsonomy.services.importer.RelationImporter;
import org.bibsonomy.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * extracts bookmarks from the export files of Firefox, Chrome, IE and Opera
 * @author rja, haile
 */
public class BrowserImporter implements FileBookmarkImporter, RelationImporter{
	private static final Log log = LogFactory.getLog(BrowserImporter.class);

	private final List<Post<Bookmark>> posts = new LinkedList<Post<Bookmark>>();
	private final List<Tag> relations = new LinkedList<Tag>();


	@Override
	public List<Post<Bookmark>> getPosts() {
		return posts;
	}

	@Override
	public List<Tag> getRelations() {
		return relations;
	}


	@Override
	public void initialize(File file, User user, String groupName) throws IOException {
		this.getBookmarksFromBrowser(file, user, groupName);
	}
	
	private void getBookmarksFromBrowser(File bookmarkFile, User currUser, String groupName) throws FileNotFoundException {
		final Document document = XmlUtils.getDOM(new FileInputStream(bookmarkFile));
		try {
			/* 
			 * if getChildNodes().item(0) returns value the browser
			 * is safari otherwise other browsers
			 */
			final NodeList childNotes = document.getElementsByTagName("body").item(0).getChildNodes();
			final Node checkFolder = childNotes.item(0);
			if (checkFolder != null) {
				int size = childNotes.getLength();
				for (int i = 0;i < size;i++ ) {
					final Node mainfolder = childNotes.item(i);
					if (mainfolder != null) {
						createBookmarks(mainfolder, null, currUser, groupName);
					}
				}
			} else {
				final Node mainFolder = childNotes.item(1);
				if (mainFolder != null) {
					createBookmarks(mainFolder, null, currUser, groupName);
				}
			}
		} catch (final Exception e) {
			log.fatal("Error on importing bookmarks.", e);
		}
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
	 */
	private void createBookmarks(final Node folder, final Vector<String> upperTags, final User user, final String groupName) {
		// the post gets today's time
		final Date today = new Date();

		// every node requires his own tags
		Vector<String> tags;
		// if tags are provided by upper nodes these tags belong to this node
		// too
		if (upperTags != null) {
			tags = new Vector<String>(upperTags);
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
			//if child node is <hr> jump to the next parent node
			if("dd".equals(currentNode.getNodeName()) && "hr".equals(currentNode.getFirstChild().getNodeName())){
				currentNode = children.item(i++);
			}
			// connect all upper tags with the currentNode
			Vector<String> myTags = new Vector<String>(tags);
			if (!"".equals(sepTag)) {
				myTags.add(sepTag);
			}

			// is currentNode a folder?
			if ("dd".equals(currentNode.getNodeName())) {
				NodeList secondGen = currentNode.getChildNodes();
				// only containing a name?
				// yes, keep tag
				if (secondGen.getLength() == 1 && "h3".equals(secondGen.item(0).getNodeName())) {
					sepTag = cleanTag(secondGen.item(0).getFirstChild().getNodeValue());
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
							myTags.add(cleanTag(son.getFirstChild().getNodeValue()));
						}
						// all dl-nodes are new folders
						if ("dl".equals(son.getNodeName())) {
							// create bookmarks from new found node
							createBookmarks(son, myTags, user, groupName);
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
				if (currentNode.getFirstChild().hasChildNodes()) {
					final Post<Bookmark> post = new Post<Bookmark>();
					final Bookmark bookmark = new Bookmark();
					post.setResource(bookmark);
					bookmark.setTitle(currentNode.getFirstChild().getFirstChild().getNodeValue());
					bookmark.setUrl(currentNode.getFirstChild().getAttributes().getNamedItem("href").getNodeValue());
					// add tags/relations to bookmark
					if (upperTags != null) {
						// only 1 tag found -> add a tag
						if (upperTags.size() == 1) {
							post.addTag(upperTags.elementAt(0));
						} else {
							// more tags found -> add relations
							for (int tagCount = 0; tagCount < upperTags.size() - 1; tagCount++) {
								final String upper = upperTags.elementAt(tagCount);
								final String lower = upperTags.elementAt(tagCount + 1);
								post.addTag(upper);
								post.addTag(lower);
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
								post.addTag(cleanTag(token.nextToken()));
							}
						} else {
							// really no tags found -> set imported tag
							post.setTags(Collections.singleton(TagUtils.getEmptyTag()));
						}
					}
					post.setDate(today);
					post.setUser(user);
					post.addGroup(groupName);
					
					// no tags available? -> add one tag to the resource and mark it as "imported"
					if (!present(post.getTags())) {
						post.setTags(Collections.singleton(TagUtils.getEmptyTag()));
					}

					// descriptions are saved in a sibling of of a node
					// containing a link
					if (currentNode.getNextSibling() != null && "dd".equals(currentNode.getNextSibling().getNodeName())) {
						post.setDescription(currentNode.getNextSibling().getFirstChild().getNodeValue());
					}
					posts.add(post);
				}
			}
		}
	}
	
	/**
	 * cleans the tag by replacing white spaches and upper and lower prefixes
	 * @param tag
	 * @return the cleaned tag
	 */
	protected String cleanTag(final String tag) {
		return tag.replaceAll("->|<-|\\s", "_");
	}

	@Override
	public void setCredentials(String userName, String password) {
		// ...		
	}
}
