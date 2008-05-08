package org.bibsonomy.scraper.url;

import helpers.picatobibtex.PicaParser;
import helpers.picatobibtex.PicaRecord;
import helpers.picatobibtex.Row;
import helpers.picatobibtex.SubField;

import java.io.ByteArrayInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import sun.text.Normalizer;

/**
 * @author C. Kramer
 * @version $Id$
 */
public class PicaToBibtexConverter {
	private static final Logger log = Logger.getLogger(PicaToBibtexConverter.class);
	
	private static PicaRecord pica;
	private String url;
	
	/**
	 * @param scrapingcont 
	 * @param type
	 * @param url
	 */
	public PicaToBibtexConverter(final String scrapingcont, final String type, final String url){
		this.pica = new PicaRecord();
		this.url = url;
		
		if ("html".equals(type.toLowerCase())){
			parseContentHtml(scrapingcont);	
		}
		if ("xml".equals(type.toLowerCase())){
			parseContentXml(scrapingcont);
		}
		
	}
	
	private void parseContentHtml(String sc){
		
	}
	
	/**
	 * creates an PicaRecord object out of the given content
	 * 
	 * @param sc
	 */
	private void parseContentXml(String sc){
		try {
			// create an xml parser
			DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = fac.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(sc.getBytes("UTF-8")));
			
			// get the nodelist which holds the "rows"
			NodeList nodes = doc.getElementsByTagName("LONGTITLE");
			NodeList childs = nodes.item(0).getChildNodes();
			
			// for every node type 3 ( text content ) call processRow
			for (int i=0; i<childs.getLength(); i++){
				Node child = childs.item(i);
				if (child.getNodeType() == 3){
					processRow(child.getTextContent().trim());
				}
			}	
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
	}
	
	/**
	 * This method should extract the necessary content with regex and put the information
	 * to the PicaRecord object
	 * 
	 * @param cont
	 */
	private void processRow(String cont){
		// pattern to extract the pica category
		Pattern p = Pattern.compile("^(\\d{3}[A-Z@]{1}/\\d{2}|\\d{3}[A-Z@]{1}).*$");
		Matcher m = p.matcher(cont);
		
		if(m.matches()){
			Row _tmpRow = new Row(m.group(1));
			String _cont = cont.replaceFirst(m.group(1), "");
			
			// etract the subfield of each category
			Pattern p1 = Pattern.compile("(\\$[0-9a-zA-Z]{1})([^\\$]+)");
			Matcher m1 = p1.matcher(_cont);
			
			// put it to the row object
			while(m1.find()){
				_tmpRow.addSubField(new SubField(m1.group(1),m1.group(2)));
			}
			
			// and finally put the row to the picarecord
			pica.addRow(_tmpRow);
		}
	}
	
	/**
	 * @return PicaRecord
	 */
	public PicaRecord getActualPicaRecord(){
		return PicaToBibtexConverter.pica;
	}
	
	/**
	 * @return Bibtex String
	 */
	public String getBibResult() throws Exception{
		PicaParser parser = new PicaParser(pica, url);
		return parser.getBibRes();
	}
}
