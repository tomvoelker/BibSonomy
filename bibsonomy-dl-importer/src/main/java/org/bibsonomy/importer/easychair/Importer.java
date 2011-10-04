package org.bibsonomy.importer.easychair;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.bibsonomy.importer.filter.PostFilterChain;
import org.bibsonomy.importer.reader.PostListReader;
import org.bibsonomy.importer.reader.XMLPostListReader;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.rest.client.RestLogicFactory;

/** 
 * Imports an EasyChair XML file into BibSonomy using the API.
 * 
 * @author rja
 * @version $Id$
 */
public class Importer {

	/**
	 * @param args
	 */
	public static void main(final String[] args) throws Exception {

		/*
		 * Loads the configuration 
		 */
		final Properties prop = new Properties();
		prop.load(Importer.class.getClassLoader().getResourceAsStream("easychair.properties"));

		/*
		 * The reader which will read and parse the XML.
		 */
		final PostListReader reader = new XMLPostListReader(new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "UTF-8")));

		/*
		 * The filter chain to preprocess the posts.
		 */
		final PostFilterChain filter = new PostFilterChain(prop);
		
		/*
		 * An instance of the BiBSonomy API.
		 */
		final String className = Importer.class.getName();
		final RestLogicFactory restLogicFactory = new RestLogicFactory(prop.getProperty(className + ".apiUrl"));
		
		final String userName = prop.getProperty(className + ".apiUser");
		final LogicInterface logic = restLogicFactory.getLogicAccess(userName, prop.getProperty(className + ".apiKey"));
		
		/*
		 * Get list of BibTex posts.
		 */
		final List<Post<BibTex>> bibTeXListFromXML = reader.readPostList();
		
		/*
		 * filter posts and post them to BibSonomy
		 */
		
		for (final Post<BibTex> post : bibTeXListFromXML) {
			filter.filterPost(post);

			/*
			 * add user name to post
			 */
			post.setUser(new User(userName));
			post.setDate(new Date());
			
			logic.createPosts(Collections.<Post<? extends Resource>>singletonList(post));	
		}
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
