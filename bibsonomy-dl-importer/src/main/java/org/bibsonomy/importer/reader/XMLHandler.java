package org.bibsonomy.importer.reader;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Callback handler for the SAX parser, reading EasyChair XML.
 * 
 * 
 * @author rja
 * @version $Id$
 */
public class XMLHandler extends DefaultHandler {
	private StringBuffer buf = new StringBuffer();
	private List<Post<BibTex>> list;

	private Post<BibTex> post;
	private StringBuffer authors;

	private String authorFirstName;
	private String authorLastName;

	@Override
	public void startDocument() {
		list = new LinkedList<Post<BibTex>>();
	}

	@Override
	public void endDocument() {
	}

	@Override
	public void startElement (final String uri, final String name, final String qName, final Attributes atts) {
		if ("submission".equals(qName)) {
			post = new Post<BibTex>();
			post.setResource(new BibTex());
			/*
             * default entry type for EasyChair publications 
             */
			post.getResource().setEntrytype("inproceedings");
			/*
			 * set the EasyChair submission number as content id
			 */
			post.setContentId(Integer.parseInt(atts.getValue("number")));
		} else if ("authors".equals(qName)) {
			authors = new StringBuffer();
		}
		buf = new StringBuffer();
	}

	/** Collect characters.
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters (final char ch[], final int start, final int length) {
		buf.append(ch, start, length);
	}

	@Override
	public void endElement (final String uri, final String name, final String qName) {
		if ("submission".equals(qName)) {
			list.add(post);
		} else if ("authors".equals(qName)) {
			post.getResource().setAuthor(authors.toString());
		} else if ("author".equals(qName)) {
			if (authors.toString().trim().equals("")) {
				authors.append(authorFirstName + " " + authorLastName);
			} else {
				authors.append(" and " + authorFirstName + " " + authorLastName);
			}
		} else if ("first_name".equals(qName)) {
			authorFirstName = buf.toString();
		} else if ("last_name".equals(qName)) {
			authorLastName = buf.toString();
		} else if ("title".equals(qName)) {
			post.getResource().setTitle(buf.toString());
		} else if ("abstract".equals(qName)) {
			post.getResource().setAbstract(buf.toString());
		} else if ("keyword".equals(qName)) {
			post.addTag(buf.toString());
		}
	}

	public List<Post<BibTex>> getList() {
		return this.list;
	}
}
