package org.bibsonomy.webapp.view;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import net.sf.json.processors.PropertyNameProcessor;
import net.sf.json.util.PropertyFilter;

import org.bibsonomy.layout.csl.CslModelConverter;
import org.bibsonomy.layout.csl.model.Date;
import org.bibsonomy.layout.csl.model.Person;
import org.bibsonomy.layout.csl.model.Record;
import org.bibsonomy.layout.csl.model.RecordList;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.webapp.command.SimpleResourceViewCommand;
import org.springframework.web.servlet.mvc.BaseCommandController;
import org.springframework.web.servlet.view.AbstractView;

/**
 * View to export data in CSL-compatible JSON-Format
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 * @version $Id$
 */
public class CSLView extends AbstractView {

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		/*
		 * get the data
		 */
		final Object object = model.get(BaseCommandController.DEFAULT_COMMAND_NAME);
		
		final List<? extends Post<? extends BibTex>> publicationList = getPublicationList(object);
		if(!present(publicationList)) {
			return;
		}

			/*
			 * set the content type headers
			 */
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			/*
			 * output stream
			 */
			final ServletOutputStream outputStream = response.getOutputStream();
			final OutputStreamWriter writer = new OutputStreamWriter(outputStream);

			if (publicationList != null) {
				RecordList recList = new RecordList();
				for (final Post<? extends BibTex> post : publicationList) {
					final Record rec = CslModelConverter.convertPost(post);
					recList.add(rec);
				}
				writer.write(JSONSerializer.toJSON(recList, getJsonConfig()).toString());
				writer.close();
			}
	}

	private JsonConfig getJsonConfig() {
		JsonConfig jsonConfig = new JsonConfig();
		// output only not-null fields
		jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
			@Override
			public boolean apply(Object source, String name, Object value) {
				if (value == null) {
					return true;
				}
				return false;
			}
		});
		// transform underscores into "-"
		jsonConfig.registerJsonPropertyNameProcessor(Person.class, new PropertyNameProcessor() {

			@Override
			public String processPropertyName(Class arg0, String arg1) {
				return arg1.replace("_", "-");
			}
		});
		jsonConfig.registerJsonPropertyNameProcessor(Record.class, new PropertyNameProcessor() {

			@Override
			public String processPropertyName(Class arg0, String arg1) {
				// special handling for abstract field
				if ("abstractt".equals(arg1)) {
					return "abstract";
				}
				return arg1.replace("_", "-");
			}
		});
		jsonConfig.registerJsonPropertyNameProcessor(Date.class, new PropertyNameProcessor() {

			@Override
			public String processPropertyName(Class arg0, String arg1) {
				return arg1.replace("_", "-");
			}
		});
		return jsonConfig;
	}
	
	/**
	 * 
	 * @param commandObject
	 * @return List of publications if supported command is given, null otherwise
	 */
	private List<? extends Post<? extends BibTex>> getPublicationList (Object commandObject) {
		if(commandObject instanceof SimpleResourceViewCommand) {
			SimpleResourceViewCommand command = (SimpleResourceViewCommand)commandObject;
			return command.getBibtex().getList();
		}
		return null;
	}
	
}
