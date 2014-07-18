package org.bibsonomy.webapp.validation;

import org.bibsonomy.services.filesystem.JabRefFileLogic;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.file.ServerUploadedFile;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.command.actions.JabRefImportCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * validator for the JabRef layout files
 */
public class JabRefImportValidator implements Validator<SettingsViewCommand> {
	
	private JabRefFileLogic fileLogic;
	
	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return JabRefImportCommand.class.equals(clazz);
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object target, Errors errors) {
		final SettingsViewCommand command = (SettingsViewCommand) target;
		this.checkFileName(command.getFileBegin(), errors, "Begin");
		this.checkFileName(command.getFileItem(), errors, "Item");
		this.checkFileName(command.getFileEnd(), errors, "End");
	}

	/**
	 * @param fileBegin
	 * @param errors 
	 */
	private void checkFileName(CommonsMultipartFile file, Errors errors, final String fieldSuffix) {
		if (file == null) {
			return; // not specified
		}
		if (!this.fileLogic.validJabRefLayoutFile(new ServerUploadedFile(file))) {
			final String field = "file" + fieldSuffix;
			errors.rejectValue(field, "settings.jabRef.error.fileextension", new Object[] { StringUtils.implodeStringCollection(this.fileLogic.allowedJabRefFileExtensions(), ", ") }, "Only the specified extensions allowed");
		}
	}

	/**
	 * @param fileLogic the fileLogic to set
	 */
	public void setFileLogic(JabRefFileLogic fileLogic) {
		this.fileLogic = fileLogic;
	}
}
