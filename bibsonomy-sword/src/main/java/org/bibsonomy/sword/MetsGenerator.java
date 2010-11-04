package org.bibsonomy.sword;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
import org.bibsonomy.model.Post;
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


	private Map<String, String> _metadataMap;
	private Result _result; 
	private ArrayList<String> _filenameList; 


	public MetsGenerator() {
		
		// initialize _metadataMap
		this._metadataMap = new HashMap<String, String>();
		
		this._metadataMap.put("title", "");  // title of publication
		this._metadataMap.put("", "");  // title of publication
		this._metadataMap.put("", "");  // title of publication
		this._metadataMap.put("", "");  // title of publication
		this._metadataMap.put("", "");  // title of publication
		this._metadataMap.put("", "");  // title of publication
		this._metadataMap.put("", "");  // title of publication
		this._metadataMap.put("", "");  // title of publication
		this._metadataMap.put("", "");  // title of publication
		this._metadataMap.put("", "");  // title of publication
		this._metadataMap.put("", "");  // title of publication
		this._metadataMap.put("", "");  // title of publication
		this._metadataMap.put("", "");  // title of publication
		this._metadataMap.put("", "");  // title of publication
		this._metadataMap.put("", "");  // title of publication
		this._metadataMap.put("", "");  // title of publication
		this._metadataMap.put("", "");  // title of publication
		this._metadataMap.put("", "");  // title of publication
		this._metadataMap.put("", "");  // title of publication
		this._metadataMap.put("", "");  // title of publication

	}

	
	
	public String getFilename(int elementnumber) {
		return _filenameList.get(elementnumber);
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


	public void setMetadata(Map<String, String> metaDataMap) {
		_metadataMap = metaDataMap;
	}

	public void setMetadata(Post<BibTex> post) {

	// set metaDataMap

	System.out.println(post.toString());
	System.out.println(post.getResource().toString());
		
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
		atts.addAttribute("","","LABEL","CDATA","SWAP Metadata");
		hd.startElement("","","mdWrap",atts);


		// xmlData
		atts.clear();
		hd.startElement("","","xmlData",atts);

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

		// dc.TYPE
		// epdcx:statement/
		// see http://www.ukoln.ac.uk/repositories/digirep/index/Eprints_EntityType_Vocabulary_Encoding_Scheme
		atts.clear();
		atts.addAttribute("","","epdcx:propertyURI","CDATA","http://purl.org/dc/elements/1.1/type");
		atts.addAttribute("","","epdcx:valueURI","CDATA","http://purl.org/eprint/entityType/ScholarlyWork");
		hd.startElement("","","epdcx:statement",atts);
		hd.endElement("","","epdcx:statement");

		// dc.TITLE
		// epdcx:statement/
		atts.clear();
		atts.addAttribute("","","epdcx:propertyURI","CDATA","http://purl.org/dc/elements/1.1/title");
		hd.startElement("","","epdcx:statement",atts);
			// epdcx:valueString/
			atts.clear();
			hd.startElement("","","epdcx:valueString",atts);
			cdatacontent = "TEST-Title"; 
			hd.characters(cdatacontent.toCharArray(),0,cdatacontent.length());
			hd.endElement("","","epdcx:valueString");
		hd.endElement("","","epdcx:statement");

		// dc.ABSTRACT
		// epdcx:statement/
		atts.clear();
		atts.addAttribute("","","epdcx:propertyURI","CDATA","http://purl.org/dc/terms/abstract");
		hd.startElement("","","epdcx:statement",atts);
			// epdcx:valueString/
			atts.clear();
			hd.startElement("","","epdcx:valueString",atts);
			cdatacontent = "TEST-Abstract"; 
			hd.characters(cdatacontent.toCharArray(),0,cdatacontent.length());
			hd.endElement("","","epdcx:valueString");
		hd.endElement("","","epdcx:statement");
		
		// dc.CREATOR
		atts.clear();
		atts.addAttribute("","","epdcx:propertyURI","CDATA","http://purl.org/dc/elements/1.1/creator");
		hd.startElement("","","epdcx:statement",atts);
			// epdcx:valueString/
			atts.clear();
			hd.startElement("","","epdcx:valueString",atts);
			cdatacontent = "Hollies, C.R."; 
			hd.characters(cdatacontent.toCharArray(),0,cdatacontent.length());
			hd.endElement("","","epdcx:valueString");
		hd.endElement("","","epdcx:statement");
		
		// dc.CREATOR (2nd)
		atts.clear();
		atts.addAttribute("","","epdcx:propertyURI","CDATA","http://purl.org/dc/elements/1.1/creator");
		hd.startElement("","","epdcx:statement",atts);
			// epdcx:valueString/
			atts.clear();
			hd.startElement("","","epdcx:valueString",atts);
			cdatacontent = "Monckton, D.G."; 
			hd.characters(cdatacontent.toCharArray(),0,cdatacontent.length());
			hd.endElement("","","epdcx:valueString");
		hd.endElement("","","epdcx:statement");

		// dc.IDENTIFIER
		atts.clear();
		atts.addAttribute("","","epdcx:propertyURI","CDATA","http://purl.org/dc/elements/1.1/identifier");
		hd.startElement("","","epdcx:statement",atts);
			// epdcx:valueString/
			atts.clear();
			atts.addAttribute("","","epdcx:sesURI","CDATA","http://purl.org/dc/terms/URI");
			hd.startElement("","","epdcx:valueString",atts);
			cdatacontent = "http://puma.uni-kassel.de/__URL_oder_BibTeX-Key_As_Identifier"; 
			hd.characters(cdatacontent.toCharArray(),0,cdatacontent.length());
			hd.endElement("","","epdcx:valueString");
		hd.endElement("","","epdcx:statement");
		
		// REF isExpressedAs (Expression)
		atts.clear();
		atts.addAttribute("","","epdcx:propertyURI","CDATA","http://purl.org/eprint/terms/isExpressedAs");
		atts.addAttribute("","","epdcx:valueRef","CDATA","sword-mets-expr-1");
		hd.startElement("","","epdcx:statement",atts);
		hd.endElement("","","epdcx:statement");

		// /epdcx:description
		hd.endElement("","","epdcx:description");
		
		// Expression
		// epdcx:description
		atts.clear();
		atts.addAttribute("","","epdcx:resourceId","CDATA","sword-mets-expr-1");
		hd.startElement("","","epdcx:description",atts);
		
		// dc.type (Expression)
		atts.clear();
		atts.addAttribute("","","epdcx:propertyURI","CDATA","http://purl.org/dc/elements/1.1/type");
		atts.addAttribute("","","epdcx:valueURI","CDATA","http://purl.org/eprint/entityType/Expression");
		hd.startElement("","","epdcx:statement",atts);
		hd.endElement("","","epdcx:statement");

		// dc.LANGUAGE
		atts.clear();
		atts.addAttribute("","","epdcx:propertyURI","CDATA","http://purl.org/dc/elements/1.1/language");
		atts.addAttribute("","","epdcx:vesURI","CDATA","http://purl.org/dc/terms/RFC3066");
		hd.startElement("","","epdcx:statement",atts);
			// epdcx:valueString/
			atts.clear();
			hd.startElement("","","epdcx:valueString",atts);
			cdatacontent = "en"; 
			hd.characters(cdatacontent.toCharArray(),0,cdatacontent.length());
			hd.endElement("","","epdcx:valueString");
		hd.endElement("","","epdcx:statement");
		
		// dc.type
		atts.clear();
		atts.addAttribute("","","epdcx:propertyURI","CDATA","http://purl.org/dc/elements/1.1/type");
		atts.addAttribute("","","epdcx:vesURI","CDATA","http://purl.org/eprint/terms/Type");
		atts.addAttribute("","","epdcx:valueURI","CDATA","http://purl.org/eprint/type/JournalArticle");
		hd.startElement("","","epdcx:statement",atts);
		hd.endElement("","","epdcx:statement");
		
		// dc.date.issued
		atts.clear();
		atts.addAttribute("","","epdcx:propertyURI","CDATA","http://purl.org/dc/terms/available");
		hd.startElement("","","epdcx:statement",atts);
			// epdcx:valueString/
			atts.clear();
			atts.addAttribute("","","epdcx:sesURI","CDATA","http://purl.org/dc/terms/W3CDTF");
			hd.startElement("","","epdcx:valueString",atts);
			cdatacontent = "2001-02"; 
			hd.characters(cdatacontent.toCharArray(),0,cdatacontent.length());
			hd.endElement("","","epdcx:valueString");
		hd.endElement("","","epdcx:statement");
		
		// dc.description.version
		
		// dc.rights.holder

		
		
		// /epdcx:description
		hd.endElement("","","epdcx:description");


		// /epdcx:descriptionSet
		hd.endElement("","","epdcx:descriptionSet");
		hd.endElement("","","xmlData");
		hd.endElement("","","mdWrap");
		hd.endElement("","","dmdSec");

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



		hd.endElement("","","mets");
		hd.endDocument();

	}

	
	
	
}
