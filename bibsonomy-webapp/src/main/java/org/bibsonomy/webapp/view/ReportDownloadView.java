package org.bibsonomy.webapp.view;

import org.apache.commons.io.output.CountingOutputStream;
import org.bibsonomy.common.exceptions.UnsupportedFormatException;
import org.bibsonomy.export.ExcelExporter;
import org.bibsonomy.export.Exporter;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.GoldStandard;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.webapp.command.reporting.PersonReportingCommand;
import org.bibsonomy.webapp.command.reporting.ProjectReportingCommand;
import org.bibsonomy.webapp.command.reporting.PublicationReportingCommand;
import org.bibsonomy.webapp.command.reporting.ReportingCommand;
import org.springframework.web.servlet.mvc.BaseCommandController;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @author pda
 */
public class ReportDownloadView extends AbstractView {

	private static <T> Exporter<T> getExporter(String name) {
		switch (name) {
			case "excel":
				return new ExcelExporter<>();
			default:
				throw new UnsupportedFormatException(name);
		}
	}

	private void exportProjects(ProjectReportingCommand command, OutputStream outputStream,
															HttpServletResponse response) throws IOException {
		final Exporter<Project> exporter = getExporter(command.getDownloadFormat());
		setResponseValues(exporter, response, command.getFilename());
		//TODO use subset of mappings?
		exporter.save(command.getProjects().getList(), outputStream,
						ReportDownloadViewUtils.PROJECT_FIELD_MAPPINGS);
	}

	private void exportPublications(PublicationReportingCommand command, OutputStream outputStream,
																	HttpServletResponse response) throws IOException {
		final Exporter<Post<GoldStandardPublication>> exporter = getExporter(command.getDownloadFormat());
		setResponseValues(exporter, response, command.getFilename());
		//TODO use subset of mappings?
		exporter.save(command.getPublications().getList(), outputStream,
						ReportDownloadViewUtils.PUBLICATION_FIELD_MAPPINGS);
	}

	private void exportPersons(PersonReportingCommand command, OutputStream outputStream,
														 HttpServletResponse response) throws IOException {
		final Exporter<Person> exporter = getExporter(command.getDownloadFormat());
		setResponseValues(exporter, response, command.getFilename());
		//TODO use subset of mappings?
		exporter.save(command.getPersons().getList(), outputStream,
						ReportDownloadViewUtils.PERSON_FIELD_MAPPINGS);
	}

	private void setResponseValues(Exporter<?> exporter, HttpServletResponse response, String fileName)
					throws UnsupportedEncodingException {
		response.setHeader("Content-Disposition",
						"inline; filename*='" + StringUtils.CHARSET_UTF_8.toLowerCase() + "'" +
										URLEncoder.encode(fileName + exporter.getFileExtension(), StringUtils.CHARSET_UTF_8));
		response.setContentType(exporter.getContentType());
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
																				 HttpServletResponse response) throws Exception {
		final Object object = model.get(BaseCommandController.DEFAULT_COMMAND_NAME);
		final CountingOutputStream output = new CountingOutputStream(new BufferedOutputStream(response.getOutputStream()));
		if (!(object instanceof ReportingCommand)) {
			return;
		}
		if (object instanceof ProjectReportingCommand) {
			final ProjectReportingCommand command = (ProjectReportingCommand) object;
			exportProjects(command, output, response);
		}
		if (object instanceof PersonReportingCommand) {
			final PersonReportingCommand command = (PersonReportingCommand) object;
			exportPersons(command, output, response);
		}
		if (object instanceof PublicationReportingCommand) {
			final PublicationReportingCommand command = (PublicationReportingCommand) object;
			exportPublications(command, output, response);
		}
		response.setContentLength((int) output.getByteCount());
		output.close();
	}
}
