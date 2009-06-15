package org.bibsonomy.importer.event.hypertext09;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.bibsonomy.importer.reader.CSVPostListReader;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.util.BibTexUtils;

/**
 * @author rja
 * @version $Id$
 */
public class Importer {

	public static void main(String[] args) throws IOException {
		/*
		 * read posts from CSV file
		 */
		final CSVPostListReader reader = new CSVPostListReader(new InputStreamReader(new FileInputStream("/tmp/ht09/ht09_prog_ck.csv"), "ISO8859-1"));
		final List<Post<BibTex>> posts = reader.readPostList();
		/*
		 * add additional metadata
		 */
		for (Post<BibTex> post : posts) {
			final BibTex bibtex = post.getResource();
			bibtex.setYear("2009");
			bibtex.setBooktitle("HT '08: Proceedings of the Twentieth ACM Conference on Hypertext and Hypermedia");
			bibtex.setPublisher("ACM");
			bibtex.setAddress("New York, NY, USA");
			bibtex.setEntrytype("inproceedings");
			bibtex.setBibtexKey(BibTexUtils.generateBibtexKey(bibtex));
			/*
			 * debug output
			 */
			System.out.println(BibTexUtils.toBibtexString(bibtex));
		}
		
	}
}
