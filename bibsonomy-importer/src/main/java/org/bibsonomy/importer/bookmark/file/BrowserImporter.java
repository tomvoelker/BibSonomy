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
		
		getBookmarksFromBrowser(file, user, groupName);
	}
	
	private void getBookmarksFromBrowser(File bookmarkFile, User currUser, String groupName) throws FileNotFoundException {

		final Document document = XmlUtils.getDOM(new FileInputStream(bookmarkFile));
		try {
			//if getChildNodes().item(0) returns value the browser is safari otherwise other browsers
			final Node checkFolder = document.getElementsByTagName("body").item(0).getChildNodes().item(0);
			if(checkFolder != null){ 
				
				int size = document.getElementsByTagName("body").item(0).getChildNodes().getLength();
				for(int i = 0;i < size;i++ ){
					final Node mainfolder = document.getElementsByTagName("body").item(0).getChildNodes().item(i);			
					if(mainfolder != null){
						createBookmarks(mainfolder, null, currUser, groupName);
					}
				}
			}else{
				final Node mainFolder = document.getElementsByTagName("body").item(0).getChildNodes().item(1);
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
	 * @return
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
				if (currentNode.getFirstChild().hasChildNodes() == true) {
					final Post<Bookmark> post = new Post<Bookmark>();
					post.setResource(new Bookmark());
					post.getResource().setTitle(currentNode.getFirstChild().getFirstChild().getNodeValue());
					post.getResource().setUrl(currentNode.getFirstChild().getAttributes().getNamedItem("href").getNodeValue());
					// add tags/relations to bookmark
					if (upperTags != null) {
						// only 1 tag found -> add a tag
						if (upperTags.size() == 1) {

							// bookmark.setTags(upperTags.elementAt(0));
							post.addTag(upperTags.elementAt(0));
						} else {
							// more tags found -> add relations
							for (int tagCount = 0; tagCount < upperTags.size() - 1; tagCount++) {
								String upper = upperTags.elementAt(tagCount);
								String lower = upperTags.elementAt(tagCount + 1);
								// bookmark.addTagRelation(lower, upper);
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
								post.addTag(token.nextToken());
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
						// bookmark.setExtended(currentNode.getNextSibling().getFirstChild().getNodeValue());
						post.setDescription(currentNode.getNextSibling().getFirstChild().getNodeValue());
					}
					posts.add(post);
				}
			}
		}
	}

	@Override
	public void setCredentials(String userName, String password) {
		// ...		
	}
}
