package org.bibsonomy.sword;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.*;
import javax.xml.parsers.*;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.LayoutRenderingException;
import org.bibsonomy.layout.jabref.JabrefLayout;
import org.bibsonomy.layout.jabref.JabrefLayoutRenderer;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.services.URLGenerator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


/**
 * Generates METS-XML-Files for publication depositing 
 * Supported types: METS/EPDCX
 *
 * METS/MODS could possibly be generated with XSLT 
 * 
 * @author:  sven
 * @version: $Id$
 * $Author$
 * 
 */
public class MetsBibTexMLGenerator {
	private static final Log log = LogFactory.getLog(MetsBibTexMLGenerator.class);


	private Post<BibTex> _post;
	private Result _result; 
	private ArrayList<String> _filenameList; 

	private static JabrefLayoutRenderer layoutRenderer;

	// contains special characters, symbols, etc...
	private static Properties chars = new Properties();

	// load special characters
	static {
		//layoutRenderer = JabrefLayoutRenderer.getInstance();
	}


	/**
	 * Helper method to access JabRef layouts via taglib function
	 * 
	 * @param post
	 * @param layoutName
	 * @return The rendered output as string.
	 */
	public static String renderLayout(final Post<BibTex> post, final String layoutName) {
		final List<Post<BibTex>> posts = new ArrayList<Post<BibTex>>();
		posts.add(post);

		return renderLayouts(posts, layoutName);
	}

	/**
	 * Helper method to access JabRef layouts via taglib function
	 * 
	 * @param posts
	 * @param layoutName
	 * @return The rendered output as string.
	 */
	public static String renderLayouts(final List<Post<BibTex>> posts, final String layoutName) {
		try {
			final JabrefLayout layout = layoutRenderer.getLayout(layoutName, "");
			if (! ".xml".equals(layout.getExtension())) {
				return "The requested layout is not valid; only HTML layouts are allowed. Requested extension is: " + layout.getExtension();
			}
			return layoutRenderer.renderLayout(layout, posts, true).toString();
		} catch (final LayoutRenderingException ex) {
			return ex.getMessage();			
		} catch (final UnsupportedEncodingException ex) {
			return "An Encoding error occured while trying to convert to layout '" + layoutName  + "'.";
		} catch (final IOException ex) {
			return "An I/O error occured while trying to convert to layout '" + layoutName  + "'."; 
		}
	}
    
    

	public MetsBibTexMLGenerator() {
		this._post = new Post<BibTex>();
	}

	public String getFilename(int elementnumber) {
		if (_filenameList.size() > elementnumber) {
			return _filenameList.get(elementnumber);
		} else {
			return null;
		}
	}


	public void setFilenameList(ArrayList<String> filenameList) {
		_filenameList = filenameList;
	}


	/**
	 * Fills url and title of bookmark.
	 * 
	 * @param url
	 * @return
	 */

	public void setMetadata(Post<BibTex> post) {

		_post = post;
	}
	

	
	/**
	 * @param xml tag
	 * @param set of tags 
	 */
	public String addStatement(String tag, Set<Tag> tags) {
		String data = "";
		for (Iterator<Tag> iter = tags.iterator(); iter.hasNext();) {
			data += addStatement(tag, iter.next().getName());
		}
		return data;
	}
	
	/**
	 * @param xml tag
	 * @param list of personName 
	 */
	public String addStatement(String tag, List <PersonName> personNameList) {
		String data = "";
		for (Iterator<PersonName> iter = personNameList.iterator(); iter.hasNext();) {
			data += addStatement(tag, iter.next());
		}
		return data;
	}

	/**
	 * @param xml tag
	 * @param personName  
	 */
	public String addStatement(String tag, PersonName personName) {
		return addStatement(tag, personName.getLastName()+", "+personName.getFirstName());
	}
	
	/**
	 * @param xml tag
	 * @param contents 
	 */
	public String addStatement(String tag, String[] contents) {
		String data = "";
		for (int i=0; i<contents.length; i++) {
			data += addStatement(tag, contents[i]);
		}
		return data;
	}

	/**
	 * @param tag
	 * @param content 
	 */
	public String addStatement(String tag, String content) {
		// TODO convert special characters to xml entities
		return "<bibtex:"+tag+">"+content+"</bibtex:"+tag+">\n";

	}
	
		
	
	public String generateMets(){
	
		String xmlDocument = "";

		
		xmlDocument += "<?xml version=\"1.0\" encoding=\"utf-8\"?>"+"\n";
		xmlDocument += "<mets ID=\"sort-mets_mets\" OBJID=\"sword-mets\" LABEL=\"DSpace SWORD Item\" PROFILE=\"DSpace METS SIP Profile 1.0\" xmlns=\"http://www.loc.gov/METS/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xsi:schemaLocation=\"http://www.loc.gov/METS/ http://www.loc.gov/standards/mets/mets.xsd\">\n";
		xmlDocument += "<metsHdr CREATEDATE=\"2007-09-01T00:00:00\">\n";
		xmlDocument += "<agent ROLE=\"CUSTODIAN\" TYPE=\"ORGANIZATION\">\n";
		xmlDocument += "<name>Sven Stefani</name>\n";
		xmlDocument += "</agent>\n";
		xmlDocument += "</metsHdr>\n";
		xmlDocument += "<dmdSec ID=\"sword-mets-dmd-1\" GROUPID=\"sword-mets-dmd-1_group-1\">\n";
		xmlDocument += "<mdWrap MIMETYPE=\"text/xml\" MDTYPE=\"OTHER\" OTHERMDTYPE=\"BIBTEXML\">\n";
		xmlDocument += "<xmlData>\n";

		
		String bibTexML = "";

		/*
		// generate BibTexML <?xml?>
		System.out.println("generate BibTexML:");
		bibTexML = renderLayout(_post, "bibtexml");
		System.out.println("bibTexML:");

		// remove first line from bibTexML
		// += bibTexML.replaceFirst("^.*$", "");
		bibTexML = bibTexML.substring(bibTexML.indexOf('\n'));
		System.out.println(bibTexML);
		*/
		
		bibTexML += "<bibtex:entry id=\""+_post.getResource().getBibtexKey()+"\" xmlns:bibtex=\"http://puma.uni-kassel.de\">\n";
		bibTexML += "<bibtex:"+_post.getResource().getEntrytype()+">\n";

		// Title
		if (null != _post.getResource().getTitle() && !_post.getResource().getTitle().isEmpty()) 
			bibTexML += addStatement("title", _post.getResource().getTitle());

		// Author
		if (null != _post.getResource().getAuthorList() && !_post.getResource().getAuthorList().isEmpty()) 
			bibTexML += addStatement("author", _post.getResource().getAuthorList());

		// Editor
		if (null != _post.getResource().getEditorList() && !_post.getResource().getEditorList().isEmpty()) 
			bibTexML += addStatement("editor", _post.getResource().getEditorList());
		
		// Booktitle
		if (null != _post.getResource().getBooktitle() && !_post.getResource().getBooktitle().isEmpty()) 
			bibTexML += addStatement("booktitle", _post.getResource().getBooktitle());

		// Journal
		if (null != _post.getResource().getJournal() && !_post.getResource().getJournal().isEmpty()) 
			bibTexML += addStatement("journal", _post.getResource().getJournal());

		// Publisher
		if (null != _post.getResource().getPublisher() && !_post.getResource().getPublisher().isEmpty()) 
			bibTexML += addStatement("publisher", _post.getResource().getPublisher());

		// Year
		if (null != _post.getResource().getYear() && !_post.getResource().getYear().isEmpty()) 
			bibTexML += addStatement("year", _post.getResource().getYear());

		// Month
		if (null != _post.getResource().getMonth() && !_post.getResource().getMonth().isEmpty()) 
			bibTexML += addStatement("month", _post.getResource().getMonth());

		// Day
		if (null != _post.getResource().getDay() && !_post.getResource().getDay().isEmpty()) 
			bibTexML += addStatement("day", _post.getResource().getDay());

		// Volume
		if (null != _post.getResource().getVolume() && !_post.getResource().getVolume().isEmpty()) 
			bibTexML += addStatement("volume", _post.getResource().getVolume());

		// Chapter
		if (null != _post.getResource().getChapter() && !_post.getResource().getChapter().isEmpty()) 
			bibTexML += addStatement("chapter", _post.getResource().getChapter());

		// Pages
		if (null != _post.getResource().getPages() && !_post.getResource().getPages().isEmpty()) 
			bibTexML += addStatement("pages", _post.getResource().getPages());

		// Number
		if (null != _post.getResource().getNumber() && !_post.getResource().getNumber().isEmpty()) 
			bibTexML += addStatement("number", _post.getResource().getNumber());

		// Edition
		if (null != _post.getResource().getEdition() && !_post.getResource().getEdition().isEmpty()) 
			bibTexML += addStatement("edition", _post.getResource().getEdition());

		// Series
		if (null != _post.getResource().getSeries() && !_post.getResource().getSeries().isEmpty()) 
			bibTexML += addStatement("series", _post.getResource().getSeries());

		// Institution
		if (null != _post.getResource().getInstitution() && !_post.getResource().getInstitution().isEmpty()) 
			bibTexML += addStatement("institution", _post.getResource().getInstitution());

		// Organization
		if (null != _post.getResource().getOrganization() && !_post.getResource().getOrganization().isEmpty()) 
			bibTexML += addStatement("organization", _post.getResource().getOrganization());

		// School
		if (null != _post.getResource().getSchool() && !_post.getResource().getSchool().isEmpty()) 
			bibTexML += addStatement("school", _post.getResource().getSchool());

		// Address
		if (null != _post.getResource().getAddress() && !_post.getResource().getAddress().isEmpty()) 
			bibTexML += addStatement("address", _post.getResource().getAddress());

		// HowPublished
		if (null != _post.getResource().getHowpublished() && !_post.getResource().getHowpublished().isEmpty()) 
			bibTexML += addStatement("howpublished", _post.getResource().getHowpublished());

		// Abstract
		if (null != _post.getResource().getAbstract() && !_post.getResource().getAbstract().isEmpty()) 
			bibTexML += addStatement("abstract", _post.getResource().getAbstract());

		// url 
		if (null != _post.getResource().getUrl() && !_post.getResource().getUrl().isEmpty()) 
			bibTexML += addStatement("url", _post.getResource().getUrl());

		// Type
		if (null != _post.getResource().getType() && !_post.getResource().getType().isEmpty()) 
			bibTexML += addStatement("type", _post.getResource().getType());

		// Crossref
		if (null != _post.getResource().getCrossref() && !_post.getResource().getCrossref().isEmpty()) 
			bibTexML += addStatement("crossref", _post.getResource().getCrossref());

		// Annote
		if (null != _post.getResource().getAnnote() && !_post.getResource().getAnnote().isEmpty()) 
			bibTexML += addStatement("annote", _post.getResource().getAnnote());

		// Note
		if (null != _post.getResource().getNote() && !_post.getResource().getNote().isEmpty()) 
			bibTexML += addStatement("note", _post.getResource().getNote());

		// Key
		if (null != _post.getResource().getKey() && !_post.getResource().getKey().isEmpty()) 
			bibTexML += addStatement("key", _post.getResource().getKey());

		// Date
		if (null != _post.getResource().getYear() && !_post.getResource().getYear().isEmpty()) 
			bibTexML += addStatement("date", _post.getResource().getYear()+((null == _post.getResource().getMonth() || _post.getResource().getMonth().isEmpty())?"":"-"+_post.getResource().getMonth()+((null == _post.getResource().getDay() || _post.getResource().getDay().isEmpty())?"":"-"+_post.getResource().getDay())));
		
		// Description
		if (null != _post.getDescription() && !_post.getDescription().isEmpty()) 
			bibTexML += addStatement("description", _post.getDescription());
		
		// Keywords, Tags, Schlagwoerter
		if (null != _post.getTags() && !_post.getTags().isEmpty()) 
			bibTexML += addStatement("keyword", _post.getTags());

		// auswertung des misc-feldes
		// may contain some identifiers like ean, issn, and so on
		// may contain some classification data in formats ddc, pacs, msc, ccs
		// parse misc-filed to compute different fields to property miscField
		_post.getResource().parseMiscField();
		if (null != _post.getResource().getMiscFields() && !_post.getResource().getMiscFields().isEmpty()){ 

			Map<String,String> miscData = _post.getResource().getMiscFields();
			
			for (Iterator<String> iter = miscData.keySet().iterator(); iter.hasNext();) {
				String key = iter.next();
				if (key.equalsIgnoreCase("ean")) {
					bibTexML += addStatement("ean", miscData.get(key).split(" "));
				}

				else if (key.equalsIgnoreCase("isbn")) {
					bibTexML += addStatement("isbn", miscData.get(key).split(" "));
				}
				
				else if (key.equalsIgnoreCase("issn")) {
					bibTexML += addStatement("issn", miscData.get(key).split(" "));
				}
				
				else if (key.equalsIgnoreCase("doi")) {
					bibTexML += addStatement("doi", miscData.get(key).split(" "));
				}

				else if (key.equalsIgnoreCase("ccs")) {
					bibTexML += addStatement("ccs", miscData.get(key).split(" "));
				}

				else if (key.equalsIgnoreCase("ddb")) {
					bibTexML += addStatement("ddb", miscData.get(key).split(" "));
				}

				else if (key.equalsIgnoreCase("ddc")) {
					bibTexML += addStatement("ddc", miscData.get(key).split(" "));
				}
				
				else if (key.equalsIgnoreCase("jel")) {
					bibTexML += addStatement("jel", miscData.get(key).split(" "));
				}
				
				else if (key.equalsIgnoreCase("lcc")) {
					bibTexML += addStatement("lss", miscData.get(key).split(" "));
				}
				
				else if (key.equalsIgnoreCase("lcsh")) {
					bibTexML += addStatement("lcsh", miscData.get(key).split(" "));
				}

				else if (key.equalsIgnoreCase("mesh")) {
					bibTexML += addStatement("mesh", miscData.get(key).split(" "));
				}

				else if (key.equalsIgnoreCase("msc")) {
					bibTexML += addStatement("msc", miscData.get(key).split(" "));
				}

				else if (key.equalsIgnoreCase("pacs")) {
					bibTexML += addStatement("pacs", miscData.get(key).split(" "));
				}

				else if (key.equalsIgnoreCase("swd")) {
					xmlDocument += addStatement("swd", miscData.get(key).split(" "));
				}

				else {
					//System.out.println("misc field: "+_post.getResource().getMiscFields());
					log.info("don't know waht to do with key >>"+key+"<< from misc field");
				}
				
			}
		}

		
		
		bibTexML += "</bibtex:"+_post.getResource().getEntrytype()+">\n";
		bibTexML += "</bibtex:entry>\n";

		xmlDocument += bibTexML;

		xmlDocument += "</xmlData>\n";
		xmlDocument += "</mdWrap>\n";
		xmlDocument += "</dmdSec>\n";
		
		// add FileSec only if Files are available
		if (null != this.getFilename(0)) {
		
			// fileSec
			xmlDocument += "<fileSec>\n";
	
			// fileGrp
			xmlDocument += "<fileGrp USE=\"CONTENT \" ID=\"sword-mets-fgrp-1\">\n";

	
			// file
			xmlDocument += "<file GROUPID=\"sword-mets-fgid-0\" ID=\"sword-mets-file-1\" MIMETYPE=\"application/pdf\">\n";
	
			// FLocat
			xmlDocument += "<FLocat LOCTYPE=\"URL\" xlink:href=\""+this.getFilename(0)+"\"/>\n";
	
			xmlDocument += "</file>\n";	
			xmlDocument += "</fileGrp>\n";	
			xmlDocument += "</fileSec>\n";	

			// structMap
			xmlDocument += "<structMap ID=\"sword-mets-struct-1\" LABEL=\"structure\" TYPE=\"LOGICAL\">\n";	
	
			// div
			xmlDocument += "<div ID=\"sword-mets-div-1\" DMDID=\"sword-mets-dmd-1\" TYPE=\"SWORD Object\">\n";
	
			// div
			xmlDocument += "<div ID=\"sword-mets-div-2\" TYPE=\"File\">\n";
	
			// fptr
			xmlDocument += "<fptr FILEID=\"sword-mets-file-1\"/>\n";
	
			xmlDocument += "</div>\n";	
			xmlDocument += "</div>\n";	
			xmlDocument += "</structMap>\n";	
		}

		// end of document
		xmlDocument += "</mets>\n";

		return xmlDocument;

	}

	
	
	
}
