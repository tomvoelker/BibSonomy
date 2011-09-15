package org.bibsonomy.importer.event.ectel09;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Callback handler for the SAX parser, reading EC-TEL 2009 XML.
 * 
 * 
 * @author rja
 * @version $Id$
 */
public class XMLHandler extends DefaultHandler {
	
	/*
	 * attributes for data handling
	 */
	private StringBuffer buf = new StringBuffer();
	private List<Post<BibTex>> list;

	private Post<BibTex> post;
	private StringBuffer authors;

	@Override
	public void startDocument() {
		list = new LinkedList<Post<BibTex>>();
	}

	@Override
	public void endDocument() {
	}

	@Override
	public void startElement (final String uri, final String name, final String qName, final Attributes atts) {
		if ("paper".equals(qName)) {
			post = new Post<BibTex>();
			post.setResource(new BibTex());
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
		if ("paper".equals(qName)) {
			list.add(post);
		} else if ("paperID".equals(qName)) {
			/*
			 * set the paper id as content id
			 */
			post.setContentId(Integer.parseInt(buf.toString()));
		} else if ("authors".equals(qName)) {
			try {
				post.getResource().setAuthor(PersonNameUtils.discoverPersonNames(authors.toString()));
			} catch (PersonListParserException ex) {
				throw new RuntimeException(ex);
			}
		} else if ("author".equals(qName)) {
			if (authors.toString().trim().equals("")) {
				authors.append(buf);
			} else {
				authors.append(" and " + buf);
			}
		} else if ("title".equals(qName)) {
			post.getResource().setTitle(buf.toString());
		} else if ("abstract".equals(qName)) {
			post.getResource().setAbstract(buf.toString());
		} else if ("tag".equals(qName)) {
			post.addTag(buf.toString());
		} else if ("topic".equals(qName)) {
			post.getResource().addMiscField("topic", buf.toString());
		}
	}

	public List<Post<BibTex>> getList() {
		return this.list;
	}
}
