package org.bibsonomy.webapp.command.reporting;

import org.bibsonomy.webapp.command.EntitySearchAndFilterCommand;

import java.util.List;

public abstract class ReportingCommand extends EntitySearchAndFilterCommand {
	private List<String> fieldKeys;
	private String downloadFormat;

	public String getDownloadFormat() {
		return downloadFormat;
	}

	public void setDownloadFormat(String downloadFormat) {
		this.downloadFormat = downloadFormat;
	}

	public abstract String getFilename();

	public List<String> getFieldKeys() {
		return fieldKeys;
	}

	public void setFieldKeys(List<String> fieldKeys) {
		this.fieldKeys = fieldKeys;
	}
}
