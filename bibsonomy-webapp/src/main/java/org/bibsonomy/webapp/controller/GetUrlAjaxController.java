package org.bibsonomy.webapp.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.bibsonomy.recommender.multiplexer.MultiplexingTagRecommender;
import org.bibsonomy.webapp.command.GetUrlAjaxCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Configuration;
import org.w3c.tidy.Tidy;

/** Returns information about the given URL.
 * 
 * @author fba
 * @version $Id$
 */
public class GetUrlAjaxController extends AjaxController implements MinimalisticController<GetUrlAjaxCommand> {
	// 2009/02/03,fei: take character encodings into account
	// FIXME: Version up to 7 of jTidy use some strange proprietary 
	//        encoding naming scheme - we have to map them to java 
	enum ENCODING_NAMES {
		raw, ASCII, ISO8859_1, UTF8, 
		JIS, MacRoman, UnicodeLittle, 
		UnicodeBig, Unicode, Cp1252, Big5, 
		SJIS};
		
		
	//nur ausführen, wenn isJump() == false
	public View workOn(GetUrlAjaxCommand command) {

		final String action = command.getAction();

		if ("getTitleForUrl".equals(action)) {
			getDetailsForUrl(command);
			return Views.AJAX_GET_TITLE_FOR_URL;
		}
		return Views.AJAX;
	}
	
	private void getDetailsForUrl(GetUrlAjaxCommand command) {
		
		if ((command.getPageURL() == null) || (command.getPageURL().length() == 0)) return;
		
		try {

			final URL url = new URL(command.getPageURL());
			final Tidy tidy = new Tidy();
			tidy.setQuiet(true);
			tidy.setShowWarnings(false);

			// 2009/02/03,fei: take character encodings into account
			// FIXME: Version up to 7 of jTidy use some strange proprietary 
			//        encoding naming scheme - we have to map them to java 
			//        Better switch to newer library
			String encodingName = url.openConnection().getContentEncoding();
			if( encodingName==null )
				encodingName = "UTF8";
			int encodingNr = 3;   // default to UTF8, additionally we could parse 
								  // <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
			try{
				encodingNr = ENCODING_NAMES.valueOf(encodingName).ordinal();
			} catch (Exception ex) {
			}
			tidy.setCharEncoding(encodingNr);
			
			
			final Document headElement = tidy.parseDOM(url.openConnection().getInputStream(), null);
			
			final NodeList title = headElement.getElementsByTagName("title");
			command.setPageTitle(title.item(0).getChildNodes().item(0).getNodeValue());
			
			final NodeList metaList = headElement.getElementsByTagName("meta");
			for (int i = 0; i < metaList.getLength(); i++) {
				final Element metaElement = (Element) metaList.item(i);
				
				Attr nameAttr = metaElement.getAttributeNode("name");
				if (nameAttr == null) continue; 
					
				if (nameAttr.getNodeValue().equalsIgnoreCase("description")) {
					command.setPageDescription(metaElement.getAttribute("content"));
				}
				if (nameAttr.getNodeValue().equalsIgnoreCase("keywords")) {
					command.setPageKeywords(metaElement.getAttribute("content"));
					final Logger log = Logger.getLogger(GetUrlAjaxController.class);
					log.error("KEYWORDS::[ "+tidy.getCharEncoding()+"]"+metaElement.getAttribute("content"));
				}
			}
			
		} catch (MalformedURLException ex) {
			// ignore exceptions silently
		} catch (IOException ex) {
			// ignore exceptions silently
		}
	}
	


	public GetUrlAjaxCommand instantiateCommand() {
		return new GetUrlAjaxCommand();
	}

}
