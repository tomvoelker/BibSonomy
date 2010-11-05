package org.bibsonomy.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.database.systemstags.search.BibTexKeySystemTag;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.util.XmlUtils;
import org.bibsonomy.webapp.command.ajax.GeneralAjaxCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * Returns information about the given URL.
 * 
 * @author fba
 * @version $Id$
 */
public class GeneralAjaxController extends AjaxController implements MinimalisticController<GeneralAjaxCommand> {

	@Override
	public View workOn(GeneralAjaxCommand command) {
		final String action = command.getAction();

		if ("getTitleForUrl".equals(action)) {
			this.getDetailsForUrl(command);
			return Views.AJAX_GET_TITLE_FOR_URL;
		} else if ("getBibtexKeysForUser".equals(action)) {
			this.getBibtexKeysForUser(command);
			return Views.AJAX_GET_BIBTEXKEYS_FOR_USER;
		}		
		return Views.AJAX_TEXT;
	}

	
	/**
	 * Retrieve bibtex keys of a given user
	 * 
	 * @param command - the command bean for this controller
	 */
	private void getBibtexKeysForUser(GeneralAjaxCommand command) {
		/*
		 * check input params, make sure user is logged in
		 */
		if (!present(command.getQ()) || !command.getContext().isUserLoggedIn() || !present(command.getRequestedUser())) {
			return;
		}
		/*
		 * append percent sign to bibkey for fuzzy matching
		 */
		final String requestedBibTexKey = command.getQ().trim() + "%";
		final String requestedUserName   = command.getRequestedUser();
		/*
		 * fetch posts
		 */		
		final List<String> tags = Collections.singletonList(SystemTagsUtil.buildSystemTagString(BibTexKeySystemTag.NAME, requestedBibTexKey));
		command.setBibtexPosts(this.logic.getPosts(BibTex.class, GroupingEntity.USER, requestedUserName, tags, null, null, null, 0, 20, null));
	}
	
	/**
	 * Retrieve details for a given URL
	 * 
	 * @param command - the command containing the page URL
	 */
	private void getDetailsForUrl(final GeneralAjaxCommand command) {

		final String pageURL = command.getPageURL();
		
		if (!present(pageURL)) return;

		try {
			final Document document = XmlUtils.getDOM(new URL(pageURL));

			final NodeList title = document.getElementsByTagName("title");
			command.setPageTitle(title.item(0).getChildNodes().item(0).getNodeValue());

			final NodeList metaList = document.getElementsByTagName("meta");
			for (int i = 0; i < metaList.getLength(); i++) {
				final Element metaElement = (Element) metaList.item(i);

				final Attr nameAttr = metaElement.getAttributeNode("name");
				if (nameAttr == null) continue; 

				if (nameAttr.getNodeValue().equalsIgnoreCase("description")) {
					command.setPageDescription(metaElement.getAttribute("content"));
				}
				if (nameAttr.getNodeValue().equalsIgnoreCase("keywords")) {
					command.setPageKeywords(metaElement.getAttribute("content"));
				}
			}

		} catch (final Exception ex) {
			// ignore exceptions silently
		}
	}

	@Override
	public GeneralAjaxCommand instantiateCommand() {
		return new GeneralAjaxCommand();
	}

}
