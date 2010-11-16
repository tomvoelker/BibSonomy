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
public class MetsGenerator {
	private static final Log log = LogFactory.getLog(MetsGenerator.class);


	private Post<BibTex> _post;
	private Result _result; 
	private ArrayList<String> _filenameList; 

	private static JabrefLayoutRenderer layoutRenderer;

	// contains special characters, symbols, etc...
	private static Properties chars = new Properties();

	// load special characters
	static {
		layoutRenderer = JabrefLayoutRenderer.getInstance();
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
    
    
	public final static HashMap<String, String> BIBTEXT2EPDCX = new HashMap<String, String>();
	static {
		BIBTEXT2EPDCX.put("article", "http://purl.org/eprint/type/JournalArticle");
		BIBTEXT2EPDCX.put("book", "http://purl.org/eprint/type/Book");
		BIBTEXT2EPDCX.put("booklet", "http://purl.org/eprint/type/ScholarlyText");  		// TODO: change to a more specific type 
		BIBTEXT2EPDCX.put("conference", "http://purl.org/eprint/type/ConferencePaper");
		BIBTEXT2EPDCX.put("electronic", "http://purl.org/eprint/type/ScholarlyText");  		// TODO: change to a more specific type
		BIBTEXT2EPDCX.put("inbook", "http://purl.org/eprint/type/ScholarlyText");  			// TODO: change to a more specific type 
		BIBTEXT2EPDCX.put("incollection", "http://purl.org/eprint/type/ScholarlyText");  	// TODO: change to a more specific type 
		BIBTEXT2EPDCX.put("inproceedings", "http://purl.org/eprint/type/ScholarlyText");  	// TODO: change to a more specific type 
		BIBTEXT2EPDCX.put("manual", "http://purl.org/eprint/type/ScholarlyText");  			// TODO: change to a more specific type 
		BIBTEXT2EPDCX.put("mastersthesis", "http://purl.org/eprint/type/Thesis");
		BIBTEXT2EPDCX.put("misc", "http://purl.org/eprint/type/ScholarlyText");  			// TODO: change to a more specific type 
		BIBTEXT2EPDCX.put("patent", "http://purl.org/eprint/type/Patent");
		BIBTEXT2EPDCX.put("periodical", "http://purl.org/eprint/type/ScholarlyText");  		// TODO: change to a more specific type 
		BIBTEXT2EPDCX.put("phdthesis", "http://purl.org/eprint/type/Thesis");
		BIBTEXT2EPDCX.put("preamble", "http://purl.org/eprint/type/ScholarlyText");  		// TODO: change to a more specific type 
		BIBTEXT2EPDCX.put("presentation", "http://purl.org/eprint/type/ScholarlyText");  	// TODO: change to a more specific type 
		BIBTEXT2EPDCX.put("proceedings", "http://purl.org/eprint/type/ScholarlyText");  	// TODO: change to a more specific type 
		BIBTEXT2EPDCX.put("standard", "http://purl.org/eprint/type/ScholarlyText");  		// TODO: change to a more specific type 
		BIBTEXT2EPDCX.put("techreport", "http://purl.org/eprint/type/ScholarlyText");  		// TODO: change to a more specific type 
		BIBTEXT2EPDCX.put("unpublished", "http://purl.org/eprint/type/ScholarlyText");  	// TODO: change to a more specific type 
	};
	

	public MetsGenerator() {
		this._post = new Post<BibTex>();
	}

	private String convertToEpdcxType(String bibTexType) {
		return BIBTEXT2EPDCX.get(bibTexType);
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
	
	
	public String toString() {
		
		String o = new String(); 
		o.concat("title: "+_post.getResource().getTitle()+"\n");
		o.concat("title: "+_post.getResource().getAuthor()+"\n");
		o.concat("abstract: "+_post.getResource().getAbstract()+"\n");
		return null;

		
	}
	
	
	/**
	 * @param hd TransformerHandler
	 * @param tags set of Tags for content of epdcx:valueString inside epdcx:statement, if necessary
	 * @param propertyURI value of propertyURI attribute
	 * @param valueURI value of valueURI attribute
	 * @param vesURI value of vesURI attribute
	 * @param valueSesUri value of valueSesUri attribute, if available
	 */
	public void addStatement(TransformerHandler hd, Set<Tag> tags, String propertyURI, String valueURI, String vesURI, String valueSesUri) {
		for (Iterator<Tag> iter = tags.iterator(); iter.hasNext();) {
			addStatement(hd, iter.next().getName(), propertyURI, valueURI, vesURI, valueSesUri);
		}
	}
	
	/**
	 * @param hd TransformerHandler
	 * @param personNameList content of epdcx:valueString inside epdcx:statement
	 * @param propertyURI value of propertyURI attribute
	 * @param valueURI value of valueURI attribute
	 * @param vesURI value of vesURI attribute
	 * @param valueSesUri value of valueSesUri attribute, if available
	 */
	public void addStatement(TransformerHandler hd, List <PersonName> personNameList, String propertyURI, String valueURI, String vesURI, String valueSesUri) {
		for (Iterator<PersonName> iter = personNameList.iterator(); iter.hasNext();) {
			addStatement(hd, iter.next(), propertyURI, valueURI, vesURI, valueSesUri);
		}
	}

	/**
	 * @param hd TransformerHandler
	 * @param personName content of epdcx:valueString inside epdcx:statement
 	 * @param propertyURI value of propertyURI attribute
	 * @param valueURI value of valueURI attribute
	 * @param vesURI value of vesURI attribute
	 * @param valueSesUri value of valueSesUri attribute, if available
	 */
	public void addStatement(TransformerHandler hd, PersonName personName, String propertyURI, String valueURI, String vesURI, String valueSesUri) {
		addStatement(hd, personName.getLastName()+", "+personName.getFirstName(), propertyURI, valueURI, vesURI, valueSesUri);
	}
	
	/**
	 * @param hd TransformerHandler
	 * @param contents of epdcx:valueString inside epdcx:statement
	 * @param propertyURI value of propertyURI attribute
	 * @param valueURI value of valueURI attribute
	 * @param vesURI value of vesURI attribute
	 * @param valueSesUri value of valueSesUri attribute, if available
	 */
	public void addStatement(TransformerHandler hd, String[] contents, String propertyURI, String valueURI, String vesURI, String valueSesUri) {
		for (int i=0; i<contents.length; i++) {
			addStatement(hd, contents[i], propertyURI, valueURI, vesURI, valueSesUri);
		}
	}

	/**
	 * @param hd TransformerHandler
	 * @param content Text of epdcx:valueString inside epdcx:statement, if necessary
	 * @param propertyURI value of propertyURI attribute
	 * @param valueURI value of valueURI attribute
	 * @param vesURI value of vesURI attribute
	 * @param valueSesUri value of valueSesUri attribute, if available
	 */
	public void addStatement(TransformerHandler hd, String content, String propertyURI, String valueURI, String vesURI, String valueSesUri) {
		AttributesImpl atts = new AttributesImpl();
		atts.clear();
		if (propertyURI != null) atts.addAttribute("","","epdcx:propertyURI","CDATA",propertyURI);
		if (valueURI != null) atts.addAttribute("","","epdcx:valueURI","CDATA",valueURI);
		if (vesURI != null) atts.addAttribute("","","epdcx:vesURI","CDATA",vesURI);

		try {
			hd.startElement("","","epdcx:statement",atts);

			if ((content != null) && (!content.isEmpty())) {
				AttributesImpl atts2 = new AttributesImpl();
				atts2.clear();
				if (valueSesUri != null) atts2.addAttribute("","","epdcx:sesURI","CDATA",valueSesUri);
				hd.startElement("","","epdcx:valueString", atts2);
				hd.characters(content.toCharArray(),0,content.length());
				hd.endElement("","","epdcx:valueString");
			}
			
			hd.endElement("","","epdcx:statement");
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	
	
	/**
	 * If no title could be found (e.g., for non-HTML pages),
	 * we use a part of the URL as title.
	 * 
	 * @param url
	 * @return
	 * @throws SAXException 
	 * @throws TransformerConfigurationException 
	 */
	public void setResult(Result streamResult) throws SAXException, TransformerConfigurationException {

		
		SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
		// SAX2.0 ContentHandler.
		TransformerHandler hd = tf.newTransformerHandler();
		Transformer serializer = hd.getTransformer();
		serializer.setOutputProperty(OutputKeys.ENCODING,"utf-8");
		//serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,"users.dtd");
		serializer.setOutputProperty(OutputKeys.INDENT,"yes");
		hd.setResult(streamResult);
		hd.startDocument();
		String cdatacontent = "";
		AttributesImpl atts = new AttributesImpl();
		
		// mets
		atts.clear();
		atts.addAttribute("","","ID","CDATA","sort-mets_mets");
		atts.addAttribute("","","OBJID","CDATA","sword-mets");
		atts.addAttribute("","","LABEL","CDATA","DSpace SWORD Item");
		atts.addAttribute("","","PROFILE","CDATA","DSpace METS SIP Profile 1.0");
		atts.addAttribute("","","xmlns","CDATA","http://www.loc.gov/METS/");
		atts.addAttribute("","","xmlns:xsi","CDATA","http://www.w3.org/2001/XMLSchema-instance");
		atts.addAttribute("","","xmlns:xlink","CDATA","http://www.w3.org/1999/xlink");
		atts.addAttribute("","","xsi:schemaLocation","CDATA","http://www.loc.gov/METS/ http://www.loc.gov/standards/mets/mets.xsd");

		hd.startElement("", "","mets",atts);

		// metsHdr
		atts.clear();
		atts.addAttribute("","","CREATEDATE","CDATA","2007-09-01T00:00:00");
		hd.startElement("","","metsHdr",atts);

		// agent
		atts.clear();
		atts.addAttribute("","","ROLE","CDATA","CUSTODIAN");
		atts.addAttribute("","","TYPE","CDATA","ORGANIZATION");
		hd.startElement("","","agent",atts);

		// name
		atts.clear();
		hd.startElement("","","name",atts);
		cdatacontent = "Sven Stefani"; 
		hd.characters(cdatacontent.toCharArray(),0,cdatacontent.length());
		hd.endElement("","","name");

		// /agent
		hd.endElement("","","agent");

		// /metsHdr
		hd.endElement("","","metsHdr");


		// dmdSec
		atts.clear();
		atts.addAttribute("","","ID","CDATA","sword-mets-dmd-1");
		atts.addAttribute("","","GROUPID","CDATA","sword-mets-dmd-1_group-1");
		hd.startElement("","","dmdSec",atts);

		// mdWrap
		atts.clear();
		atts.addAttribute("","","MIMETYPE","CDATA","text/xml");
		atts.addAttribute("","","MDTYPE","CDATA","OTHER");
		atts.addAttribute("","","OTHERMDTYPE","CDATA","EPDCX");
		hd.startElement("","","mdWrap",atts);


		// xmlData
		atts.clear();
		hd.startElement("","","xmlData",atts);

       
		String metsFormat = "BibTexML";
		String bibTexML = ""; 
		if (metsFormat.equals("BibTexML")) {
			// generate BibTexML 
			System.out.println("generate BibTexML:");
			bibTexML = renderLayout(_post, "bibtexml");
			System.out.println("bibTexML:");
			System.out.println(bibTexML);
			hd.characters(bibTexML.toCharArray(), 0, bibTexML.length());
		}
		else if (metsFormat.equals("EPDCX")) {
	        // epdcx:descriptionSet
			atts.clear();
			atts.addAttribute("","","xmlns:epdcx","CDATA","http://purl.org/eprint/epdcx/2006-11-16/");
			atts.addAttribute("","","xmlns:xsi","CDATA","http://www.w3.org/2001/XMLSchema-instance");
			atts.addAttribute("","","xsi:schemaLocation","CDATA","http://purl.org/eprint/epdcx/2006-11-16/ http://purl.org/eprint/epdcx/xsd/2006-11-16/epdcx.xsd");
			hd.startElement("","","epdcx:descriptionSet",atts);
	
	        // epdcx:description
			atts.clear();
			atts.addAttribute("","","epdcx:resourceId","CDATA","sword-mets-epdcx-1");
			hd.startElement("","","epdcx:description",atts);
			
			//	public void addDimField(TransformerHandler hd, String content, String propertyURI, String valueURI, String vesURI, String valueSesUri) {
	
			addStatement(hd, "", "http://purl.org/dc/elements/1.1/type", "http://purl.org/eprint/entityType/ScholarlyWork", null, null);
	
			// Title
			if (null != _post.getResource().getTitle() && !_post.getResource().getTitle().isEmpty()) 
				addStatement(hd, _post.getResource().getTitle(), "http://purl.org/dc/elements/1.1/title", null, null, null);
	
			// Author
			if (null != _post.getResource().getAuthorList() && !_post.getResource().getAuthorList().isEmpty()) 
				addStatement(hd, _post.getResource().getAuthorList(), "http://purl.org/dc/elements/1.1/creator", null, null, null);
	
			
			// Abstract
			if (null != _post.getResource().getAbstract() && !_post.getResource().getAbstract().isEmpty()) 
				addStatement(hd, _post.getResource().getAbstract(), "http://purl.org/dc/terms/abstract", null, null, null);
	
			
	// url 
			if (null != _post.getResource().getUrl() && !_post.getResource().getUrl().isEmpty()) 
				addStatement(hd, _post.getResource().getUrl(), "http://purl.org/dc/elements/1.1/identifier", null, null, "http://purl.org/dc/terms/URI");
		
			
			// name
			atts.clear();
			atts.addAttribute("","","epdcx:propertyURI","CDATA","http://purl.org/eprint/terms/isExpressedAs");
			atts.addAttribute("","","epdcx:valueRef","CDATA","sword-mets-expr-1");
	
			hd.startElement("","","epdcx:statement",atts);
			hd.endElement("","","epdcx:statement");
	
			hd.endElement("","","epdcx:description");
			
	        // epdcx:description
			atts.clear();
			atts.addAttribute("","","epdcx:resourceId","CDATA","sword-mets-expr-1");
			hd.startElement("","","epdcx:description",atts);
			
			addStatement(hd, "", "http://purl.org/dc/elements/1.1/type", "http://purl.org/eprint/entityType/Expression", null, null);
			
			addStatement(hd, "en", "http://purl.org/dc/elements/1.1/language", null, "http://purl.org/dc/terms/RFC3066", null);
	
			// Type
			if (null != _post.getResource().getEntrytype() && !_post.getResource().getEntrytype().isEmpty()) {
				addStatement(hd, "", "http://purl.org/dc/elements/1.1/type", convertToEpdcxType(_post.getResource().getEntrytype()), "http://purl.org/eprint/terms/Type", null);
			}
	
			// date issued
			if (null != _post.getResource().getYear() && !_post.getResource().getYear().isEmpty()) 
				addStatement(hd, _post.getResource().getYear()+((null == _post.getResource().getMonth() || _post.getResource().getMonth().isEmpty())?"":"-"+_post.getResource().getMonth()+((null == _post.getResource().getDay() || _post.getResource().getDay().isEmpty())?"":"-"+_post.getResource().getDay())), "http://purl.org/dc/terms/available", null, null, "http://purl.org/dc/terms/W3CDTF");
	
			
			addStatement(hd, "", "http://purl.org/eprint/terms/status", "http://purl.org/eprint/status/PeerReviewed", "http://purl.org/eprint/terms/Status", null);
			addStatement(hd, "Nature Publishing Group", "http://purl.org/eprint/terms/copyrightHolder", null, null, null);
			
			
			
			
			/*
			// Publisher
			if (null != _post.getResource().getPublisher() && !_post.getResource().getPublisher().isEmpty()) 
				addStatement(hd, _post.getResource().getPublisher(), "publisher", null, "dc", null);
	
			// description.everything
			if (null != _post.getDescription() && !_post.getDescription().isEmpty()) 
				addStatement(hd, _post.getDescription(), "description", "everything", "dc", null);
			
			// tags - Schlagwoerter
			if (null != _post.getTags() && !_post.getTags().isEmpty()) 
				addStatement(hd, _post.getTags(), "subject", "swd", "dc", null);
	
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
						addStatement(hd, miscData.get(key).split(" "), "identifier", "ean", "dc", null);
					}
	
					else if (key.equalsIgnoreCase("isbn")) {
						addStatement(hd, miscData.get(key).split(" "), "identifier", "isbn", "dc", null);
					}
					
					else if (key.equalsIgnoreCase("issn")) {
						addStatement(hd, miscData.get(key).split(" "), "identifier", "issn", "dc", null);
					}
					
					else if (key.equalsIgnoreCase("doi")) {
						addStatement(hd, miscData.get(key).split(" "), "identifier", "doi", "dc", null);
					}
	
					else if (key.equalsIgnoreCase("classification.ddc")) {
						addStatement(hd, miscData.get(key).split(" "), "subject", "ddc", "dc", null);
					}
					
					else if (key.equalsIgnoreCase("classification.ccs")) {
						addStatement(hd, miscData.get(key).split(" "), "subject", "ccs", "dc", null);
					}
					
					else if (key.equalsIgnoreCase("classification.pacs")) {
						addStatement(hd, miscData.get(key).split(" "), "subject", "pacs", "dc", null);
					}
					
					else if (key.equalsIgnoreCase("classification.msc")) {
						addStatement(hd, miscData.get(key).split(" "), "subject", "msc", "dc", null);
					}
					else {
						System.out.println("misc field: "+_post.getResource().getMiscFields());
						log.info("don't know waht to do with key >>"+key+"<< from misc field");
					}
					
				}
			}
	*/
			hd.endElement("","","epdcx:description");
			hd.endElement("","","epdcx:descriptionSet");
		} // end of if EPDCX
		
		hd.endElement("","","xmlData");
		hd.endElement("","","mdWrap");
		hd.endElement("","","dmdSec");

		
		// add FileSec only if Files are available
		if (null != this.getFilename(0)) {
		
			// fileSec
			atts.clear();
			hd.startElement("","","fileSec",atts);
	
			// fileGrp
			atts.clear();
			atts.addAttribute("","","USE","CDATA","CONTENT");
			atts.addAttribute("","","ID","CDATA","sword-mets-fgrp-1");
			hd.startElement("","","fileGrp",atts);
	
			// file
			atts.clear();
			atts.addAttribute("","","GROUPID","CDATA","sword-mets-fgid-0");
			atts.addAttribute("","","ID","CDATA","sword-mets-file-1");
			atts.addAttribute("","","MIMETYPE","CDATA","application/pdf");
			hd.startElement("","","file",atts);
	
	
			// FLocat
			atts.clear();
			atts.addAttribute("","","LOCTYPE","CDATA","URL");
			atts.addAttribute("","","xlink:href","CDATA", this.getFilename(0));
			hd.startElement("","","FLocat",atts);
			hd.endElement("","","FLocat");
	
	
			hd.endElement("","","file");
			hd.endElement("","","fileGrp");
			hd.endElement("","","fileSec");

			// structMap
			atts.clear();
			atts.addAttribute("","","ID","CDATA","sword-mets-struct-1");
			atts.addAttribute("","","LABEL","CDATA","structure");
			atts.addAttribute("","","TYPE","CDATA","LOGICAL");
			hd.startElement("","","structMap",atts);
	
			// div
			atts.clear();
			atts.addAttribute("","","ID","CDATA","sword-mets-div-1");
			atts.addAttribute("","","DMDID","CDATA","sword-mets-dmd-1");
			atts.addAttribute("","","TYPE","CDATA","SWORD Object");
			hd.startElement("","","div",atts);
	
			// div
			atts.clear();
			atts.addAttribute("","","ID","CDATA","sword-mets-div-2");
			atts.addAttribute("","","TYPE","CDATA","File");
			hd.startElement("","","div",atts);
	
			// fptr
			atts.clear();
			atts.addAttribute("","","FILEID","CDATA","sword-mets-file-1");
			hd.startElement("","","fptr",atts);
	
			hd.endElement("","","fptr");
			hd.endElement("","","div");
			hd.endElement("","","div");
			hd.endElement("","","structMap");

		}


		hd.endElement("","","mets");
		hd.endDocument();

	}

	
	
	
}
