package org.bibsonomy.webapp.controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;
import org.bibsonomy.webapp.command.GetUrlAjaxCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

/**
 * @author fba
 * @version $Id$
 */
public class GetUrlAjaxController extends AjaxController implements MinimalisticController<GetUrlAjaxCommand> {
	private static final Logger log = Logger.getLogger(GetUrlAjaxController.class);

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
		System.out.println("GetUrlAjaxController getDetailsForUrl " + command.getPageURL());
		
		if ((command.getPageURL() == null) || (command.getPageURL().length() == 0)) return;
		
		URL url;
		try {
			url = new URL(command.getPageURL());
			Tidy tidy = new Tidy();
			tidy.setQuiet(true);
			tidy.setShowWarnings(false);
			Document headElement = tidy.parseDOM(url.openConnection().getInputStream(), null);
			
			NodeList title = headElement.getElementsByTagName("title");
			String pageTitle = title.item(0).getChildNodes().item(0).getNodeValue();
			System.out.println("title:       " + pageTitle);
			command.setPageTitle(pageTitle);
			
			NodeList metaList = headElement.getElementsByTagName("meta");
			for (int i = 0; i < metaList.getLength(); i++) {
				Element metaElement = (Element) metaList.item(i);
				
				Attr nameAttr = metaElement.getAttributeNode("name");
				if (nameAttr == null) continue; 
					
				if (nameAttr.getNodeValue().equalsIgnoreCase("description")) {
					System.out.println("description: " + metaElement.getAttribute("content"));
					command.setPageDescription(metaElement.getAttribute("content"));
				}
				if (nameAttr.getNodeValue().equalsIgnoreCase("keywords")) {
					System.out.println("keywords:    " + metaElement.getAttribute("content"));
					command.setPageKeywords(metaElement.getAttribute("content"));
				}
			}
			
		} catch (MalformedURLException ex) {
			System.out.println("GetUrlAjaxController getDetailsForUrl MalformedURLException");
			ex.printStackTrace();
		} catch (IOException ex) {
			System.out.println("GetUrlAjaxController getDetailsForUrl IOException");
			ex.printStackTrace();
		}
		System.out.println("GetUrlAjaxController getDetailsForUrl finished");
	}
	


	public GetUrlAjaxCommand instantiateCommand() {
		return new GetUrlAjaxCommand();
	}

}
