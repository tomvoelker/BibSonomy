package org.bibsonomy.webapp.view;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.webapp.command.BibtexViewCommand;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.mvc.BaseCommandController;
import org.springframework.web.servlet.view.AbstractView;
import org.springframework.web.servlet.view.JstlView;


/**
 * Outputs posts in BibTeX format.
 * 
 * TODO: could as well be used to return EndNote?!
 * 
 * @author rja
 * @version $Id$
 */
public class BibTeXView extends AbstractView implements ServletContextAware {

	/**
	 * must be injected
	 */
	private Map<String, URLGenerator> urlGenerators;

	/**
	 * TODO: move to FileUtil (together with {@link #getCleanedFilename(String)}?
	 */
	private static final Pattern FILE_NAME_DISALLOWED_CHARACTERS = Pattern.compile("^[A-Za-z0-9_\\-\\.]");
	
	private static final Log log = LogFactory.getLog(BibTeXView.class);
	
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		

		/*
		 * get command
		 */
		final Object object = model.get(BaseCommandController.DEFAULT_COMMAND_NAME);

		if(object instanceof BibtexViewCommand) {
			final BibtexViewCommand command = (BibtexViewCommand)object;


			/*
			 * configure BibTeX export
			 * 
			 * TODO: the next two URL parameters must be added to the command 
			 * and the allowed fields (don't change their name, they are already 
			 * used)
			 */
			final int flags = BibTexUtils.getFlags(false, !command.isLastFirstNames(), command.isGeneratedBibtexKeys());

			try {
				response.setContentType("text/plain; charset=UTF-8");
				response.setCharacterEncoding("UTF-8");

				if (command.isDownload()) { // FIXME: add to command/allowed fields - do we want to use a string or is a boolean sufficient? 
					/*
					 * TODO: How to properly encode the file name? Or should we 
					 * just remove all characters except A-Za-z0-0 
					 */
					response.setHeader("Content-Disposition", "attachement; filename=" + createFileName(command.getBibtex().getList(), command.getFormat()));
				}
				/*
				 * output stream
				 */
				final ServletOutputStream outputStream = response.getOutputStream();
				final OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");

				URLGenerator urlGenerator = urlGenerators.get(command.getUrlGenerator());
				if (urlGenerator == null) {
					urlGenerator = urlGenerators.get("default");
				}
				/*
				 * write posts
				 */
				for (final Post<BibTex> post : command.getBibtex().getList()) {
					writer.append(BibTexUtils.toBibtexString(post, flags, urlGenerator) + "\n\n");
				}
				
				writer.close();
			} catch (IOException e) {
				throw new IOException(e);
			}
		} else {
			log.error("unknown BibtexView command: " + object);
			response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
			final BindingResult errors = ViewUtils.getBindingResult(model);
			errors.reject("error.layout.rendering", new Object[]{}, "Could not render bibtex layout");
			/*
			 * do the rendering ... a bit tricky: we need to get an appropriate JSTL view and give it the 
			 * application context
			 */
			final JstlView view = new JstlView("/WEB-INF/jsp/error.jspx");
			view.setApplicationContext(getApplicationContext());
			view.render(model, request, response);
		}
	}

	private String createFileName(List<Post<BibTex>> list, String format) {
		StringBuilder sb = new StringBuilder();
		if (ValidationUtils.present(list) == false) {
			sb.append("empty");
		} else {
			sb.append(list.get(0).getResource().getEntrytype());
			if (list.size() > 0) {
				sb.append("_etc");
			}
		}
		sb.append(getFileTypeSuffix(format));
		return getCleanedFilename(sb);
	}

	private String getFileTypeSuffix(String format) {
		if ((Views.FORMAT_STRING_BIB.equals(format) || Views.FORMAT_STRING_BIBTEX.equals(format))) {
			return ".bib";
		}
		return "." + format;
	}

	private static String getCleanedFilename(final CharSequence filename) {
		return FILE_NAME_DISALLOWED_CHARACTERS.matcher(filename).replaceAll("");
	}
	
	/**
	 * @return the urlGenerators
	 */
	public Map<String, URLGenerator> getUrlGenerators() {
		return this.urlGenerators;
	}

	/**
	 * @param urlGenerators the urlGenerators to set
	 */
	public void setUrlGenerators(Map<String, URLGenerator> urlGenerators) {
		this.urlGenerators = urlGenerators;
	}

}
