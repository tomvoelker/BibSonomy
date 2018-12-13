package org.bibsonomy.webapp.command.reporting;

import org.bibsonomy.webapp.command.BaseCommand;

import java.util.List;

public abstract class ReportingCommand extends BaseCommand {
	private String format;
	private List<String> fieldKeys;

	public abstract String getFilename();

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
