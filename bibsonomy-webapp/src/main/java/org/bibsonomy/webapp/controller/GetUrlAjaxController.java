package org.bibsonomy.webapp.controller;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.util.XmlUtils;
import org.bibsonomy.webapp.command.GetUrlAjaxCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/** Returns information about the given URL.
 * 
 * @author fba
 * @version $Id$
 */
public class GetUrlAjaxController extends AjaxController implements MinimalisticController<GetUrlAjaxCommand> {

	private static final Log log = LogFactory.getLog(GetUrlAjaxController.class);

	//nur ausführen, wenn isJump() == false
	public View workOn(GetUrlAjaxCommand command) {

		final String action = command.getAction();

		if ("getTitleForUrl".equals(action)) {
			getDetailsForUrl(command);
			return Views.AJAX_GET_TITLE_FOR_URL;
		}
		return Views.AJAX;
	}

	private void getDetailsForUrl(final GetUrlAjaxCommand command) {

		if ((command.getPageURL() == null) || (command.getPageURL().length() == 0)) return;

		try {

			final Document document = XmlUtils.getDOM(new URL(command.getPageURL()));
			

			final NodeList title = document.getElementsByTagName("title");
			command.setPageTitle(title.item(0).getChildNodes().item(0).getNodeValue());

			final NodeList metaList = document.getElementsByTagName("meta");
			for (int i = 0; i < metaList.getLength(); i++) {
				final Element metaElement = (Element) metaList.item(i);

				Attr nameAttr = metaElement.getAttributeNode("name");
				if (nameAttr == null) continue; 

				if (nameAttr.getNodeValue().equalsIgnoreCase("description")) {
					command.setPageDescription(metaElement.getAttribute("content"));
				}
				if (nameAttr.getNodeValue().equalsIgnoreCase("keywords")) {
					command.setPageKeywords(metaElement.getAttribute("content"));
					log.info("KEYWORDS:" + metaElement.getAttribute("content"));
				}
			}

		} catch (final Exception ex) {
			// ignore exceptions silently
		}
	}



	public GetUrlAjaxCommand instantiateCommand() {
		return new GetUrlAjaxCommand();
	}

}
