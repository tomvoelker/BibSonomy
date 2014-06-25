package org.bibsonomy.webapp.view;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.rest.renderer.impl.JabrefMSOfficeXMLRenderer;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.webapp.command.SimpleResourceViewCommand;
import org.springframework.web.servlet.mvc.BaseCommandController;
import org.springframework.web.servlet.view.AbstractView;

import tags.Functions;

@SuppressWarnings("deprecation")
public class MSOfficeXMLExportView extends AbstractView{
	
	//Injected
	private URLGenerator urlGenerator;
	
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		/*
		 * get the data
		 */
		final Object command = model.get(BaseCommandController.DEFAULT_COMMAND_NAME);
		
		final List<Post<BibTex>> publicationList = getPublicationList(command);
		if(!present(publicationList)) {
			return;
		}

		/*
		 * set the content type headers
		 */
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");
		
		String requPath = (String) request.getAttribute("requPath");
		if (requPath == null) {
			requPath = "";
		}
		response.setHeader("Content-Disposition", "attachement; filename=" + Functions.makeCleanFileName(requPath) + ".xml");
		
		/*
		 * output stream
		 */
		final ServletOutputStream outputStream = response.getOutputStream();
		final OutputStreamWriter writer = new OutputStreamWriter(outputStream);

		JabrefMSOfficeXMLRenderer renderer = new JabrefMSOfficeXMLRenderer(urlGenerator);
		renderer.append(writer, publicationList);

		
		writer.close();	
	}
	
	/**
	 * 
	 * @param commandObject
	 * @return List of publications if supported command is given, null otherwise
	 */
	private List<Post<BibTex>> getPublicationList (Object commandObject) {
		if(commandObject instanceof SimpleResourceViewCommand) {
			SimpleResourceViewCommand command = (SimpleResourceViewCommand)commandObject;
			return command.getBibtex().getList();
		}
		return null;
	}

	public URLGenerator getUrlGenerator() {
		return urlGenerator;
	}

	public void setUrlGenerator(URLGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}

	

	
}
