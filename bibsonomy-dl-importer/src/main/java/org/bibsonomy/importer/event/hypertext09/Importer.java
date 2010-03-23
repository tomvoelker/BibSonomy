package org.bibsonomy.importer.event.hypertext09;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.bibsonomy.importer.filter.PostFilterChain;
import org.bibsonomy.importer.reader.CSVPostListReader;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.model.util.TagUtils;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * @author rja
 * @version $Id$
 */
public class Importer {


	private final Properties prop;

	public Importer() throws IOException {
		/*
		 * Load properties for configuring the filters. 
		 */
		this.prop = new Properties();
		prop.load(Importer.class.getClassLoader().getResourceAsStream("hypertext09.properties"));
	}

	/**
	 * @throws Exception
	 */
	public void importPapers() throws Exception {
		/*
		 * The filter chain to preprocess the posts.
		 */
		final PostFilterChain postFilter = new PostFilterChain(prop);

		final CSVPostListReader reader = new CSVPostListReader(new InputStreamReader(new FileInputStream(getProperty("files.paper")), "ISO8859-1"));
		final List<Post<BibTex>> posts = reader.readPostList();

		/*
		 * read tags from CSV file
		 */
		final Map<String, Set<String>> tags = readTags(new InputStreamReader(new FileInputStream(getProperty("files.tags")), "ISO8859-1"));
		

		
		/*
		 * add additional metadata
		 */
		for (Post<BibTex> post : posts) {
			/*
			 * fill year, booktitle, publisher, ...
			 */
			addDefaultFields(post.getResource());
			/*
			 * parse misc field, to access paper ID and track
			 */
			post.getResource().parseMiscField();
			final String paperId = post.getResource().getMiscField("paperId");
			final String sessionId = post.getResource().getMiscField("session");

			/*
			 * add tags
			 */
			final Set<String> tagSet = tags.get(paperId);
			for (final String tag: tagSet) {
				post.addTag(cleanTag(tag));
			}
//			printTags(post);
			/*
			 * filter post to clean tags
			 */
			postFilter.filterPost(post);
//			printTags(post);
			/*
			 * add default tags (paperId / sessionId)
			 */
			addDefaultTags(post.getTags(), paperId, sessionId);
			/*
			 * add tags as misc field such that they're included in toBibtexString()
			 */
			post.getResource().addMiscField("keywords", TagUtils.toTagString(post.getTags(), " "));
			
		}

		/*
		 * writes the BibTeX for BibSonomy import
		 */
		final BufferedWriter bibtexWriter = getWriter("files.bibtex");
		for (Post<BibTex> post : posts) {
			/*
			 * print resulting bibtex
			 */
			bibtexWriter.write(BibTexUtils.toBibtexString(post.getResource()) + "\n");
		}
		bibtexWriter.close();
		
		
		/*
		 * writes the CSV file to map the posts to bibsonomy for the copy link
		 * format is:
		 * paperId, intraHash,  
		 */
		final CSVWriter mapWriter = new CSVWriter(getWriter("files.mapping"));
		mapWriter.writeNext(new String[]{"paperId", "intraHash", "copyLink"});
		for (Post<BibTex> post : posts) {
			post.getResource().recalculateHashes();
			mapWriter.writeNext(new String[]{
					post.getResource().getMiscField("paperId"),
					post.getResource().getIntraHash(),
					"http://www.bibsonomy.org/ShowBibtexEntry?hash=" + post.getResource().getIntraHash() + "&user=ht09&copytag=" + getTagString(post.getTags()) + "&tags=ht09"
					
			});
		}
		mapWriter.close();
	}
	
	private String getTagString(final Set<Tag> tags) {
		final StringBuffer buf = new StringBuffer();
		for (final Tag t: tags) {
			buf.append(t.getName() + "+");
		}
		return buf.toString();
	}
	

	private BufferedWriter getWriter(final String fileName) throws UnsupportedEncodingException, FileNotFoundException {
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getProperty(fileName)), "UTF-8"));
	}

	private static String getCamelCase(final String s) {
		final StringBuffer buf = new StringBuffer();
		final String[] split = s.split("\\s");
		boolean first = true;
		for (final String string : split) {
			if (first) {
				buf.append(string.toLowerCase());
			} else {
				buf.append(string.substring(0,1).toUpperCase() + string.substring(1).toLowerCase());
			}
			first = false;
		}
		return buf.toString();
	}

	private void addDefaultTags(final Set<Tag> tags, final String paperId, final String sessionId) {
		tags.add(new Tag("ht09"));
		tags.add(new Tag(paperId));
		tags.add(new Tag(getCamelCase(sessionId)));
	}

	private static void printTags(final Post<BibTex> post) {
		System.out.print("[");
		for (final Tag t: post.getTags()) {
			System.out.print(t.getName() + ", ");
		}
		System.out.println("]");
	}


	private String getProperty(final String key) {
		return prop.getProperty(this.getClass().getName() + "." + key);
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		final Importer im = new Importer();
		im.importPapers();

	}

	private static String cleanTag(final String tag) {
		return tag;
	}

	/**
	 * Read tags from CSV file.
	 * 
	 * @param tags
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static Map<String, Set<String>> readTags(final InputStreamReader reader) throws UnsupportedEncodingException, FileNotFoundException, IOException {
		final Map<String, Set<String>> tags = new HashMap<String, Set<String>>();
		final CSVReader csvReader = new CSVReader(reader);
		String[] line;
		while ((line = csvReader.readNext()) != null) {
			if (line.length > 1) {
				final String paperId = line[0];
				final String tag = line[1];
				if (!tags.containsKey(paperId)) {
					tags.put(paperId, new HashSet<String>());
				}
				tags.get(paperId).add(tag);
			}
		}
		csvReader.close();
		return tags;
	}

	/**
	 * Fills the remaining bibtex fields with defaults. 
	 * 
	 * @param bibtex
	 */
	private void addDefaultFields(final BibTex bibtex) {
		/*
		 * TODO missing fields:
		 * - url
		 * - ISBN
		 * - DOI
		 * - pages
		 */
		bibtex.setYear(getProperty("field.year"));
		bibtex.setMonth(getProperty("field.month"));
		//bibtex.setMonth(getProperty("field.doi"));
		//bibtex.setUrl(getProperty("field.url"));
		bibtex.addMiscField("isbn", getProperty("field.isbn"));
		//bibtex.setPages(getProperty("field.pages"));
		//bibtex.setEditor(getProperty("field.editor"));
		//bibtex.setSeries(getProperty("field.series"));
		bibtex.setBooktitle(getProperty("field.booktitle"));
		bibtex.setPublisher(getProperty("field.publisher"));
		bibtex.setAddress(getProperty("field.address"));
		bibtex.setEntrytype(getProperty("field.type"));
		bibtex.setBibtexKey(BibTexUtils.generateBibtexKey(bibtex));
	}
}

