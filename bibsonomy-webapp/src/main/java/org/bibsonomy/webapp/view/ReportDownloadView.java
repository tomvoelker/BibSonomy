package org.bibsonomy.webapp.view;

import org.bibsonomy.common.exceptions.UnsupportedFormatException;
import org.bibsonomy.export.ExcelExporter;
import org.bibsonomy.export.Exporter;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.webapp.command.reporting.ProjectReportingCommand;
import org.bibsonomy.webapp.command.reporting.ReportingCommand;
import org.springframework.web.servlet.mvc.BaseCommandController;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class ReportDownloadView extends AbstractView {

	private void exportProjects(ProjectReportingCommand command, OutputStream outputStream) throws IOException {
		final ReportDownloadViewUtils utils = ReportDownloadViewUtils.INSTANCE;
		final Exporter<Project> exporter;
		switch (command.getFormat()) {
			case "excel":
				exporter = new ExcelExporter<>();
				break;
			default:
				throw new UnsupportedFormatException(command.getFormat());
		}
		//TODO use subset of mappings?
		exporter.save(command.getProjects(), outputStream, utils.getProjectMappings());
	}

	private void setResponseValues(ReportingCommand reportingCommand,
																 HttpServletResponse response) throws UnsupportedEncodingException {
		response.setHeader("Content-Disposition", "inline; filename*='" + StringUtils.CHARSET_UTF_8.toLowerCase() + "'" +
						URLEncoder.encode(reportingCommand.getFilename(), StringUtils.CHARSET_UTF_8));
		response.setContentType(reportingCommand.getContentType());
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
																				 HttpServletResponse response) throws Exception {
		final Object object = model.get(BaseCommandController.DEFAULT_COMMAND_NAME);
		final BufferedOutputStream output = new BufferedOutputStream(response.getOutputStream());
		if (object instanceof ProjectReportingCommand) {
			final ProjectReportingCommand command = (ProjectReportingCommand) object;
			final File document = new File(command.getPathToFile());
			setResponseValues(command, response);
			response.setContentLength((int) document.length());
			exportProjects(command, output);
		}
		output.close();
	}
}
