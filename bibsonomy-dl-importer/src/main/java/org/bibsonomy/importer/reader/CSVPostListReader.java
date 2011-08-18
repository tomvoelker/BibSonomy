package org.bibsonomy.importer.reader;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Reads a CSV file into a list of posts.
 * Only useful for the Hypertext 2009 CSV format.
 * 
 * @author rja
 * @version $Id$
 */
public class CSVPostListReader implements PostListReader {

	private final BufferedReader reader;

	public CSVPostListReader(final BufferedReader reader) {
		this.reader = reader;
	}

	public CSVPostListReader(final Reader reader) throws IOException {
		this(new BufferedReader(reader));
	}

	public CSVPostListReader(final InputStream stream) throws IOException {
		this(new InputStreamReader(stream, "UTF-8"));
	}

	public CSVPostListReader(final String fileName) throws IOException {
		this(new FileInputStream(fileName));
	}



	@Override
	public List<Post<BibTex>> readPostList() throws IOException {
		/*
		 * configure csv reader
		 */
		final CSVReader csvReader = new CSVReader(reader);
		/*
		 * result list
		 */
		final List<Post<BibTex>> posts = new LinkedList<Post<BibTex>>();
		/*
		 * skip first line
		 */
		csvReader.readNext();
		/*
		 * read data
		 */
		String [] l;
		Post<BibTex> post = null;

		while ((l = csvReader.readNext()) != null) {
			/*
			 * format is:
			 * 0         1          2       3          4                 5          6                   7                    8                  9              10
			 * "Session","Paper No","Title","Subtitle","Number of Pages","Abstract","Author First Name","Author Middle Name","Author Last Name","Affiliations","Affiliation Location"
			 * with authors spanning over several lines
			 */
			if (l.length > 1) {
				final String session = l[0];
				if (present(session)) {
					/*
					 * add former post to list
					 */
					if (post != null) {
						posts.add(post);
					}
					/*
					 * new paper
					 */
					post = new Post<BibTex>();
					post.setResource(new BibTex());
					post.getResource().setTitle(l[2]);
					post.getResource().setAbstract(l[5]);
					/*
					 * remember paper id as content_id to allow addition of 
					 * other metadata referencing the paper id
					 */
					post.getResource().setMisc("paperId = {" + l[1] + "}, session = {" + l[0] + "}");
					addAuthor(post.getResource(), l[6], l[7], l[8]);
				} else {
					/*
					 * add authors
					 */
					addAuthor(post.getResource(), l[6], l[7], l[8]);
				}
			}

		}
		/*
		 * add last post to list
		 */
		if (post != null) {
			posts.add(post);
		}

		return posts;
	}
	
	private static void addAuthor(final BibTex bib, final String firstName, final String middleName, final String lastName) {
		if (!present(bib.getAuthor())) {
			// new list
			bib.setAuthor(new LinkedList<PersonName>());
		}
		bib.getAuthor().add(new PersonName(firstName, getName(middleName) + " " + getName(lastName)));
	}
	
	private static String getName(final String name) {
		if (name.length() == 1) return name + ".";
		return name;
	}

}
