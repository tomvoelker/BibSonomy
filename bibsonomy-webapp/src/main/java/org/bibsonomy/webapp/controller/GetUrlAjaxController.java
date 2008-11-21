package org.bibsonomy.webapp.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.bibsonomy.webapp.command.GetUrlAjaxCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

/** Returns information about the given URL.
 * 
 * @author fba
 * @version $Id$
 */
public class GetUrlAjaxController extends AjaxController implements MinimalisticController<GetUrlAjaxCommand> {

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
