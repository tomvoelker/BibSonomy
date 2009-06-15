package org.bibsonomy.importer.reader;

import java.io.IOException;
import java.util.List;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.xml.sax.SAXException;

/**
 * @author rja
 * @version $Id$
 */
public interface PostListReader {

	/** Reads a list containing BibTeX posts from an EasyChair XML file.
	 * 
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 */
	public abstract List<Post<BibTex>> readPostList() throws IOException;

}