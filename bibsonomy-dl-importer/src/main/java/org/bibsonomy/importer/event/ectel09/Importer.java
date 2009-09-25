package org.bibsonomy.importer.event.ectel09;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.bibsonomy.importer.filter.PostFilterChain;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.model.util.TagUtils;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

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
		prop.load(Importer.class.getClassLoader().getResourceAsStream("ectel09.properties"));
	}

	/**
	 * @throws Exception
	 */
	public void importPapers() throws Exception {
		/*
		 * The filter chain to preprocess the posts.
		 */
		final PostFilterChain postFilter = new PostFilterChain(prop);

		/*
		 * read the XML file
		 */
		final BufferedReader reader = getReader("files.paper");

		/*
		 * parse the XML file
		 */
		final XMLReader xr = XMLReaderFactory.createXMLReader();
		final XMLHandler handler = new XMLHandler();
		xr.setContentHandler(handler);
		xr.setErrorHandler(handler);
		xr.parse(new InputSource(reader));
		final List<Post<BibTex>> posts = handler.getList();


		/*
		 * add additional metadata
		 */
		for (final Post<BibTex> post : posts) {
			final BibTex bibtex = post.getResource();
			/*
			 * fill year, booktitle, publisher, ...
			 */
			addDefaultFields(bibtex);
			/*
			 * remove empty abstracts
			 */
			if ("N/A".equals(bibtex.getAbstract())) {
				bibtex.setAbstract("");
			}
			
			/*
			 * parse misc field, to access paper ID and track
			 */
			BibTexUtils.parseMiscField(bibtex);
			final Integer paperId = post.getContentId();
			final String sessionId = bibtex.getMiscField("topic");

			/*
			 * filter post to clean tags
			 */
//			postFilter.filterPost(post);
			/*
			 * add default tags (paperId / sessionId)
			 */
			addDefaultTags(post.getTags(), paperId.toString(), sessionId);
			/*
			 * add tags as misc field such that they're included in toBibtexString()
			 */
			bibtex.addMiscField("keywords", TagUtils.toTagString(post.getTags(), " "));
		}
		
		/*
		 * remove certain "papers"
		 */
		final Iterator<Post<BibTex>> iterator = posts.iterator();
		while (iterator.hasNext()) {
			final Post<BibTex> post = iterator.next();
			final BibTex bib = post.getResource();
			if (
					"Workshop Committee".equals(bib.getAuthor()) ||
					"Coffee Lovers".equals(bib.getAuthor()) ||
					"Lunch Break".equals(bib.getTitle()) ||
					"Coffee Break".equals(bib.getTitle()) ||
					"Introduction to the Workshop".equals(bib.getTitle()) ||
					bib.getTitle().startsWith("Panel session:") ||
					bib.getTitle().startsWith("Keynote speech:") ||
					"Keynote".equals(bib.getMiscField("topic"))
			) {
				iterator.remove();
			}
		}
		

		/*
		 * writes the BibTeX for BibSonomy import
		 */
		final BufferedWriter bibtexWriter = getWriter("files.bibtex");
		for (final Post<BibTex> post : posts) {
			/*
			 * print resulting bibtex
			 */
			bibtexWriter.write(BibTexUtils.toBibtexString(post.getResource()) + "\n");
		}
		bibtexWriter.close();
		
		System.err.println("wrote " + posts.size() + " posts");


		/*
		 * writes the CSV file to map the posts to bibsonomy for the copy link
		 * format is:
		 * paperId, intraHash,  
		 */
		final CSVWriter mapWriter = new CSVWriter(getWriter("files.mapping"));
		mapWriter.writeNext(new String[]{"paperId", "intraHash", "copyLink"});
		for (final Post<BibTex> post : posts) {
			post.getResource().recalculateHashes();
			mapWriter.writeNext(new String[]{
					post.getContentId().toString(),
					post.getResource().getIntraHash(),
					"http://www.bibsonomy.org/ShowBibtexEntry?hash=" + post.getResource().getIntraHash() + "&user=" + getProperty("copyurl.user") + "&copytag=" + getTagString(post.getTags()) + "&tags=" + getProperty("copyurl.tag")

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


	/**
	 * Returns a new writer for the file defined by propery fileName
	 * 
	 * @param fileName
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	private BufferedWriter getWriter(final String fileName) throws UnsupportedEncodingException, FileNotFoundException {
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getProperty(fileName)), "UTF-8"));
	}

	private BufferedReader getReader(final String fileName) throws UnsupportedEncodingException, FileNotFoundException {
		return new BufferedReader(new InputStreamReader(new FileInputStream(getProperty(fileName)), "UTF-8"));
	}
	
	
	private static String getCamelCase(final String s) {
		final StringBuffer buf = new StringBuffer();
		final String[] split = s.split("\\s+");
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
		tags.add(new Tag(getProperty("default.tag")));
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
		//bibtex.addMiscField("isbn", getProperty("field.isbn"));
		//bibtex.setPages(getProperty("field.pages"));
		bibtex.setEditor(getProperty("field.editor"));
		bibtex.setSeries(getProperty("field.series"));
		bibtex.setSeries(getProperty("field.volume"));
		bibtex.setBooktitle(getProperty("field.booktitle"));
		bibtex.setPublisher(getProperty("field.publisher"));
		bibtex.setAddress(getProperty("field.address"));
		bibtex.setEntrytype(getProperty("field.type"));
		bibtex.setBibtexKey(BibTexUtils.generateBibtexKey(bibtex));
	}
}

