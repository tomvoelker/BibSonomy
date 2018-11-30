package org.bibsonomy.webapp.command.reporting;

import org.bibsonomy.webapp.command.BaseCommand;

import java.util.List;

public abstract class ReportingCommand extends BaseCommand {
	private String contentType;
	private String pathToFile;
	private String filename;
	private String format;
	private List<String> fieldKeys;

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getPathToFile() {
		return pathToFile;
	}

	public void setPathToFile(String pathToFile) {
		this.pathToFile = pathToFile;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public List<String> getFieldKeys() {
		return fieldKeys;
	}

	public void setFieldKeys(List<String> fieldKeys) {
		this.fieldKeys = fieldKeys;
	}
}
