package org.bibsonomy.importer.bookmark.file;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.services.importer.FileBookmarkImporter;
import org.bibsonomy.services.importer.RelationImporter;

/**
 * 
 * Imports bookmarks and relations from Delicious.
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class FirefoxImporter implements FileBookmarkImporter, RelationImporter {

	private static final Log log = LogFactory.getLog(FirefoxImporter.class);

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
	public void setFile(File file) throws IOException {
		/*
		 * TODO: import bookmarks+relations here
		 */
		
	}
}

