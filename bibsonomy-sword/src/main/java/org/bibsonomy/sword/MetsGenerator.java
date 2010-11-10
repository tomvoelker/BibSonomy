package org.bibsonomy.sword;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
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


	public MetsGenerator() {
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
	
	
	public String toString() {
		
		String o = new String(); 
		o.concat("title: "+_post.getResource().getTitle()+"\n");
		o.concat("title: "+_post.getResource().getAuthor()+"\n");
		o.concat("abstract: "+_post.getResource().getAbstract()+"\n");
		return null;

		
	}
	
	
	/**
	 * @param hd TransformerHandler
	 * @param tags set of Tags
	 * @param element value of element attribute
	 * @param qualifier value of qualifier attribute
	 * @param mdschema value of mdschema attribute
	 * @param language value of element attribute, if available
	 */
	public void addDimField(TransformerHandler hd, Set<Tag> tags, String element, String qualifier, String mdschema, String language) {
		for (Iterator<Tag> iter = tags.iterator(); iter.hasNext();) {
			addDimField(hd, iter.next().getName(), element, qualifier, mdschema, language);
		}
	}
	
	/**
	 * @param hd TransformerHandler
	 * @param personNameList List of personName values
	 * @param element value of element attribute
	 * @param qualifier value of qualifier attribute
	 * @param mdschema value of mdschema attribute
	 * @param language value of element attribute, if available
	 */
	public void addDimField(TransformerHandler hd, List <PersonName> personNameList, String element, String qualifier, String mdschema, String language) {
		for (Iterator<PersonName> iter = personNameList.iterator(); iter.hasNext();) {
			addDimField(hd, iter.next(), element, qualifier, mdschema, language);
		}
	}

	/**
	 * @param hd TransformerHandler
	 * @param personName 
	 * @param element value of element attribute
	 * @param qualifier value of qualifier attribute
	 * @param mdschema value of mdschema attribute
	 * @param language value of element attribute, if available
	 */
	public void addDimField(TransformerHandler hd, PersonName personName, String element, String qualifier, String mdschema, String language) {
		addDimField(hd, personName.getLastName()+", "+personName.getFirstName(), element, qualifier, mdschema, language);
	}
	
	/**
	 * @param hd TransformerHandler
	 * @param contents array of strings
	 * @param element value of element attribute
	 * @param qualifier value of qualifier attribute
	 * @param mdschema value of mdschema attribute
	 * @param language value of element attribute, if available
	 */
	public void addDimField(TransformerHandler hd, String[] contents, String element, String qualifier, String mdschema, String language) {
		for (int i=0; i<contents.length; i++) {
			addDimField(hd, contents[i], element, qualifier, mdschema, language);
		}
	}

	/**
	 * @param hd TransformerHandler
	 * @param content Text
	 * @param element value of element attribute
	 * @param qualifier value of qualifier attribute
	 * @param mdschema value of mdschema attribute
	 * @param language value of element attribute, if available
	 */
	public void addDimField(TransformerHandler hd, String content, String element, String qualifier, String mdschema, String language) {
		AttributesImpl atts = new AttributesImpl();
		atts.clear();
		if (element != null) atts.addAttribute("","","element","CDATA",element);
		if (qualifier != null) atts.addAttribute("","","qualifier","CDATA",qualifier);
		if (mdschema != null) atts.addAttribute("","","mdschema","CDATA",mdschema);
		if (language != null) atts.addAttribute("","","language","CDATA",language);

		try {
			hd.startElement("","","dim:field",atts);
			hd.characters(content.toCharArray(),0,content.length());
			hd.endElement("","","dim:field");
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
		atts.addAttribute("","","OTHERMDTYPE","CDATA","DIM");
		hd.startElement("","","mdWrap",atts);


		// xmlData
		atts.clear();
		hd.startElement("","","xmlData",atts);

		// dim:dim
		atts.clear();
		atts.addAttribute("","","dspaceType","CDATA","ITEM");
		hd.startElement("","","dim:dim",atts);

		// dim:field  -  Author
		if (null != _post.getResource().getAuthorList() && !_post.getResource().getAuthorList().isEmpty()) 
			addDimField(hd, _post.getResource().getAuthorList(), "contributor", "author", "dc", null);
		
		// dim:field  -  Title
		if (null != _post.getResource().getTitle() && !_post.getResource().getTitle().isEmpty()) 
			addDimField(hd, _post.getResource().getTitle(), "title", null, "dc", null);

		// dim:field  -  Abstract
		if (null != _post.getResource().getAbstract() && !_post.getResource().getAbstract().isEmpty()) 
			addDimField(hd, _post.getResource().getAbstract(), "description", "abstract", "dc", null);

		// dim:field  -  date issued
		if (null != _post.getResource().getYear() && !_post.getResource().getYear().isEmpty()) 
			addDimField(hd, _post.getResource().getYear()+((null == _post.getResource().getMonth() || _post.getResource().getMonth().isEmpty())?"":"-"+_post.getResource().getMonth()+((null == _post.getResource().getDay() || _post.getResource().getDay().isEmpty())?"":"-"+_post.getResource().getDay())), "date", "issued", "dc", null);

		// dim:field  -  Publisher
		if (null != _post.getResource().getPublisher() && !_post.getResource().getPublisher().isEmpty()) 
			addDimField(hd, _post.getResource().getPublisher(), "publisher", null, "dc", null);

		// dim:field  -  Type
		if (null != _post.getResource().getType() && !_post.getResource().getType().isEmpty()) 
			addDimField(hd, _post.getResource().getType(), "type", null, "dc", "en");
		
		// dim:field  -  description.everything
		if (null != _post.getDescription() && !_post.getDescription().isEmpty()) 
			addDimField(hd, _post.getDescription(), "description", "everything", "dc", null);
		
		// dim:field  -  tags - Schlagwoerter
		if (null != _post.getTags() && !_post.getTags().isEmpty()) 
			addDimField(hd, _post.getTags(), "subject", "swd", "dc", null);
		
		// dim:field  -  url 
		if (null != _post.getResource().getUrl() && !_post.getResource().getUrl().isEmpty()) 
			addDimField(hd, _post.getResource().getUrl(), "identifier", "url", "dc", null);
		

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
					addDimField(hd, miscData.get(key).split(" "), "identifier", "ean", "dc", null);
				}

				else if (key.equalsIgnoreCase("isbn")) {
					addDimField(hd, miscData.get(key).split(" "), "identifier", "isbn", "dc", null);
				}
				
				else if (key.equalsIgnoreCase("issn")) {
					addDimField(hd, miscData.get(key).split(" "), "identifier", "issn", "dc", null);
				}
				
				else if (key.equalsIgnoreCase("doi")) {
					addDimField(hd, miscData.get(key).split(" "), "identifier", "doi", "dc", null);
				}

				else if (key.equalsIgnoreCase("classification.ddc")) {
					addDimField(hd, miscData.get(key).split(" "), "subject", "ddc", "dc", null);
				}
				
				else if (key.equalsIgnoreCase("classification.ccs")) {
					addDimField(hd, miscData.get(key).split(" "), "subject", "ccs", "dc", null);
				}
				
				else if (key.equalsIgnoreCase("classification.pacs")) {
					addDimField(hd, miscData.get(key).split(" "), "subject", "pacs", "dc", null);
				}
				
				else if (key.equalsIgnoreCase("classification.msc")) {
					addDimField(hd, miscData.get(key).split(" "), "subject", "msc", "dc", null);
				}
				else {
					System.out.println("misc field: "+_post.getResource().getMiscFields());
					log.info("don't know waht to do with key >>"+key+"<< from misc field");
				}
				
			}
		}

		hd.endElement("","","dim:dim");
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
