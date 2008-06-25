package org.bibsonomy.importer.easychair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.bibsonomy.importer.filter.PostFilterChain;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;

/** 
 * Converts an EasyChair XML file into BibTeX.
 * 
 * @author rja
 * @version $Id$
 */
public class BibTeXConverter {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		/*
		 * Load properties for configuring the filters. 
		 */
		final Properties prop = new Properties();
		prop.load(BibTeXConverter.class.getClassLoader().getResourceAsStream("easychair.properties"));
		/*
		 * The reader which will read and parse the XML.
		 */
		final XMLPostListReader reader = new XMLPostListReader(new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "UTF-8")));

		/*
		 * The filter chain to preprocess the posts.
		 */
		final PostFilterChain filter = new PostFilterChain(prop);
		
		/*
		 * Get list of BibTex posts.
		 */
		final List<Post<BibTex>> bibTeXListFromXML = reader.readPostList();
		
		/*
		 * filter posts and write them to into a file.
		 */
		
		final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), "UTF-8"));
		
		for (final Post<BibTex> post:bibTeXListFromXML) {
			filter.filterPost(post);
			writer.write(toString(post) + "\n");
		}
		
		writer.close();
		

	}

	
	/** Converts a BibTeX posts into BibTeX. Does not include all BibTeX fields! 
	 * 
	 * 
	 * @param post
	 * @return
	 */
	public static String toString(final Post<BibTex> post) {
		final BibTex resource = post.getResource();
		
		return "@inproceedings{" + resource.getBibtexKey() + ",\n" + 
		   	   "  title     = \"" + resource.getTitle() + "\",\n" +
		       "  authors   = \"" + resource.getAuthor() + "\",\n" +
		       "  url       = \"" + resource.getUrl() + "\",\n" +
		       "  year      = \"" + resource.getYear() + "\",\n" +
		       "  month     = \"" + resource.getMonth() + "\",\n" +
		       "  booktitle = \"" + resource.getBooktitle() + "\",\n" +
		       "  publisher = \"" + resource.getPublisher() + "\",\n" +
		       "  address   = \"" + resource.getAddress() + "\",\n" +
		       "  series    = \"" + resource.getSeries() + "\",\n" +
		       "  editor    = \"" + resource.getEditor() + "\",\n" +
		       "  keywords  = \"" + toString(post.getTags()) + "\"\n" +
		       "}\n";
		
	}
	
	public static String toString(final Collection<Tag> tags) {
		final StringBuffer buf = new StringBuffer();
		for (final Tag tag: tags) {
			buf.append(tag.getName() + " ");
		}
		
		return buf.toString().trim();
	}

}
