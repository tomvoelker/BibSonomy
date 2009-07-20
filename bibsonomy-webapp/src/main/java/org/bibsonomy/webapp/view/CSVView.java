package org.bibsonomy.webapp.view;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.services.renderer.LayoutRenderer;
import org.bibsonomy.webapp.command.SimpleResourceViewCommand;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.BaseCommandController;
import org.springframework.web.servlet.view.AbstractView;
import org.springframework.web.servlet.view.JstlView;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * View which uses an {@link LayoutRenderer} to render the output.
 * 
 * @author rja
 * @version $Id$
 */
public class CSVView extends AbstractView {
	private static final Log log = LogFactory.getLog(CSVView.class);

	@Override
	protected void renderMergedOutputModel(final Map model, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		/*
		 * get the data
		 */
		final Object object = model.get(BaseCommandController.DEFAULT_COMMAND_NAME);
		if (object instanceof SimpleResourceViewCommand) {
			/*
			 * we can only handle SimpleResourceViewCommands ...
			 */
			final SimpleResourceViewCommand command = (SimpleResourceViewCommand) object;

			/*
			 * set the content type headers
			 */				
			response.setContentType("text/csv");
			response.setCharacterEncoding("UTF-8");

			/*
			 * get the requested path
			 * we need it to generate the file names for inline content-disposition
			 * FIXME: The path is written into the request by the UrlRewriteFilter 
			 * ... probably this is not a good idea
			 */
			final String requPath = (String) request.getAttribute("requPath");
			//response.setHeader("Content-Disposition", "attachement; filename=" + Functions.makeCleanFileName(requPath) + extension);

			try {
				/*
				 * write the buffer to the response
				 */
				final ServletOutputStream outputStream = response.getOutputStream();
				final CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(outputStream, "UTF-8"));
				/*
				 * write publications
				 */
				final List<Post<BibTex>> publicationList = command.getBibtex().getList();
				if (publicationList != null) {
					for (final Post<BibTex> post : publicationList) {
						final BibTex resource = post.getResource();
						csvWriter.writeNext(getArray(post, resource.getAuthor(), resource.getEditor()));
					}
				}

				/*
				 * write bookmarks
				 */
				final List<Post<Bookmark>> bookmarkList = command.getBookmark().getList();
				if (bookmarkList != null) {
					for (final Post<Bookmark> post : bookmarkList) {
						final Bookmark resource = post.getResource();
						csvWriter.writeNext(getArray(post, null, null));
					}
				}

				csvWriter.close();

			} catch (final IOException e) {
				log.error("Could not render CSV view.", e);
				/*
				 * layout could not be found or contains errors -> set HTTP status to 400
				 */
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				/*
				 * get the errors object and add the error message
				 */
				final BindingResult errors = getBindingResult(model);
				errors.reject("error.layout.rendering", new Object[]{e.getMessage()}, "Could not render layout: " + e.getMessage());
				/*
				 * do the rendering ... a bit tricky: we need to get an appropriate JSTL view and give it the 
				 * application context
				 */
				final JstlView view = new JstlView("/WEB-INF/jsp/error.jspx");
				view.setApplicationContext(getApplicationContext());
				view.render(model, request, response);

			}
		} else {
			/*
			 * FIXME: what todo here?
			 */
		}
	}


	private String[] getArray(final Post<? extends Resource> post, final String author, final String editor) {
		final Resource resource = post.getResource();
		return new String[] {
				post.getResource().getIntraHash(),
				post.getUser().getName(),
				post.getDate().toString(),
				tagsToString(post.getTags()),
				groupsToString(post.getGroups()),
				resource.getTitle(),
				author,
				editor
		};
	}

	/**
	 * Creates the group string for CSV output
	 * @param groups
	 * @return
	 */
	private String groupsToString (final Set<Group> groups) {
		final StringBuffer buf = new StringBuffer();
		if (groups.isEmpty()) {
			buf.append(GroupUtils.getPublicGroup().getName());
		} else {
			for (final Group group: groups) {
				buf.append(group.getName() + " ");
			}
		}
		return buf.toString().trim();
	}

	/**
	 * Creates the tag string for CSV output
	 * @param tags
	 * @return
	 */
	private String tagsToString (final Set<Tag> tags) {
		final StringBuffer buf = new StringBuffer();
		for (final Tag tag: tags) {
			buf.append(tag.getName() + " ");
		}
		return buf.toString().trim();
	}


	/** Gets the BindingResult (containing errors) from the model.
	 * @param model
	 * @return
	 */
	private BindingResult getBindingResult(final Map model){
		for (Object key : model.keySet() ){
			if(((String)key).startsWith(BindingResult.MODEL_KEY_PREFIX))
				return (BindingResult) model.get(key);
		}
		return null;
	}

}
