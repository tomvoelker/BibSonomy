package org.bibsonomy.webapp.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.webapp.command.ExportPageCommand;
import org.springframework.web.servlet.mvc.BaseCommandController;
import org.springframework.web.servlet.view.AbstractView;

/**
 * 
 * View which exports a JSON-Object that contains all available JabRef-layouts.
 * This view is used in conjunction with the BibSonomy Typo3-PlugIn.
 * 
 * @author mwa
 * @version $Id$
 */
public class ExportLayoutView extends AbstractView{

	private static final Log log = LogFactory.getLog(LayoutView.class);
	
	@Override
	protected void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		log.info("ExportLayoutView - renderMergedOutputModel called");
		/*
		 * get the command data
		 */
		final Object object = model.get(BaseCommandController.DEFAULT_COMMAND_NAME);
		if (object instanceof ExportPageCommand) {
		
			/*
			 * Only handle a ExportPageCommand that contains JabrefLayouts
			 */
			final ExportPageCommand command = (ExportPageCommand) object;
			
			/*
			 * the JSON-object contains a JSON-array with the layouts
			 */
			final JSONArray jsonLayouts = new JSONArray();
			
			/*
			 * put each layout into a JSON-object and add it to the JSON-array
			 */
			for(String layoutName: command.getLayoutMap().keySet()){	
				//only support public layouts
				if(command.getLayoutMap().get(layoutName).isPublicLayout()){
					jsonLayouts.put(JSONObject.fromObject(command.getLayoutMap().get(layoutName)));
				}
			}

			final JSONObject json = new JSONObject();
			json.put("layouts", jsonLayouts);
			
			/*
			 * write the output, it will show the JSON-object as a plaintext string
			 */
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getOutputStream().write(json.toString().getBytes("UTF-8"));
		}
	}

}
