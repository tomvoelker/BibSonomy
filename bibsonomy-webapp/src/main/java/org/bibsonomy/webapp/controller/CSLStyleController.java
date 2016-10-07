package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Map.Entry;

import org.bibsonomy.layout.csl.CSLFilesManager;
import org.bibsonomy.layout.csl.CSLStyle;
import org.bibsonomy.webapp.command.CSLStyleCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 *
 * @author jp
 */
public class CSLStyleController implements MinimalisticController<CSLStyleCommand> {
	
	/** is used to read metadata from CSL - Name */
	private CSLFilesManager cslFilesManager;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.webapp.util.MinimalisticController#instantiateCommand()
	 */
	@Override
	public CSLStyleCommand instantiateCommand() {
		return new CSLStyleCommand();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.webapp.util.MinimalisticController#workOn(org.bibsonomy.
	 * webapp.command.ContextCommand)
	 */
	@Override
	public View workOn(final CSLStyleCommand command) {
		final String styleName = command.getStyle();
		if (!present(styleName)) {
			final JSONObject layouts = new JSONObject();
			final JSONArray styleArray = new JSONArray();
			for (final Entry<String, CSLStyle> cslFilesEntry : this.cslFilesManager.getCslFiles().entrySet()) {
				final CSLStyle style = cslFilesEntry.getValue();
				final JSONObject styleObject = new JSONObject();
				styleObject.put("source", "CSL");
				styleObject.put("name", cslFilesEntry.getKey());
				styleObject.put("displayName", style.getDisplayName());
				final String aliasedTo = style.getAliasedTo();
				if (present(aliasedTo)) {
					styleObject.put("aliasedTo", aliasedTo);
				}
				styleArray.add(styleObject);
			}
			
			layouts.put("layouts", styleArray);
			command.setResponseString(layouts.toString());
			return Views.AJAX_JSON;
		}
		final CSLStyle style = this.cslFilesManager.getStyleByName(styleName.toLowerCase());
		command.setResponseString(style.getContent());
		return Views.AJAX_XML;
	}

	/**
	 * @param cslFilesManager the cslFilesManager to set
	 */
	public void setCslFilesManager(CSLFilesManager cslFilesManager) {
		this.cslFilesManager = cslFilesManager;
	}
}
