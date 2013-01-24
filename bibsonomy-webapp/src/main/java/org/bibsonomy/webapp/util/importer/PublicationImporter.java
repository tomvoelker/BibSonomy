package org.bibsonomy.webapp.util.importer;

import static org.bibsonomy.util.ValidationUtils.present;
import static org.bibsonomy.util.upload.FileUploadInterface.BIBTEX_ENDNOTE_EXTENSIONS;
import static org.bibsonomy.util.upload.FileUploadInterface.FILE_UPLOAD_EXTENSIONS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.UnsupportedFileTypeException;
import org.bibsonomy.model.Document;
import org.bibsonomy.scraper.converter.EndnoteToBibtexConverter;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.ConversionException;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.upload.FileUploadInterface;
import org.bibsonomy.util.upload.impl.FileUploadFactory;
import org.bibsonomy.webapp.command.actions.PostPublicationCommand;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * Handles publication posting/upload for other controllers, e.g., the PostPublicationController.
 * 
 * @author rja
 * @version $Id$
 */
public class PublicationImporter {
	
	private static final Log log = LogFactory.getLog(PublicationImporter.class);
	
	/**
	 * the factory used to get an instance of a FileUploadHandler.
	 */
	private FileUploadFactory uploadFactory;
	
	/**
	 * TODO: we could inject this object using Spring.
	 */
	private EndnoteToBibtexConverter endnoteToBibtexConverter;
	
	/**
	 * Handles an uploaded file and returns its contents - if necessary 
	 * after converting EndNote to BibTeX;
	 * 
	 * @param command
	 * @param errors 
	 * @return
	 */
	public String handleFileUpload(final PostPublicationCommand command, final Errors errors) {
		boolean keepTempFile = false;

		/*
		 * get temp file
		 */
		File file = null;
		String fileContent = null;
		try {

			final CommonsMultipartFile uploadedFile = command.getFile();

			if (!present(uploadedFile) || !present(uploadedFile.getFileItem().getName())) {
				errors.reject("error.upload.failed.noFileSelected");
				return null;
			}

			//check if uploaded file is one of allowed files, otherwise it can be a endnote or bibtex file
			if (StringUtils.matchExtension(uploadedFile.getFileItem().getName(), FILE_UPLOAD_EXTENSIONS)) {
				log.debug("the file is in pdf format");

				handleNonSnippetFile(command, this.uploadFactory.getFileUploadHandler(Collections.singletonList(uploadedFile.getFileItem()), FILE_UPLOAD_EXTENSIONS).writeUploadedFile());
				keepTempFile = true;
				return null;
			}

			final FileUploadInterface uploadFileHandler = this.uploadFactory.getFileUploadHandler(Collections.singletonList(uploadedFile.getFileItem()), FileUploadInterface.BIBTEX_ENDNOTE_EXTENSIONS);

			final Document uploadedDocument = uploadFileHandler.writeUploadedFile();
			file = uploadedDocument.getFile();

			final String fileName = uploadedDocument.getFileName();

			final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), command.getEncoding()));

			if (!StringUtils.matchExtension(fileName, BIBTEX_ENDNOTE_EXTENSIONS[0])) {
				/*
				 * In case the uploaded file is in EndNote or RIS format, we convert it to BibTeX.
				 */
				log.debug("the file is in EndNote format");
				fileContent = endnoteToBibtexConverter.endnoteToBibtexString(reader);
			} else {
				/*
				 * or just use it as it is ...
				 */
				log.debug("the file is in BibTeX format");
				fileContent = StringUtils.getStringFromReader(reader);
			}
			if (present(fileContent)) {
				return fileContent;
			}
			errors.reject("error.upload.failed.emptyFile", "The specified file is empty.");
			return null;

		} catch (final ConversionException e) {
			errors.reject("error.upload.failed.conversion", "An error occurred during converting your EndNote file to BibTeX.");
		} catch (final UnsupportedFileTypeException e) {
			/*
			 * FIXME add also extensions form FILE_UPLOAD_EXTENSIONS to the message? 
			 */
			errors.reject("error.upload.failed.filetype", new Object[] {StringUtils.implodeStringArray(BIBTEX_ENDNOTE_EXTENSIONS, ", ")}, e.getMessage());
		} catch (final Exception ex1) {
			errors.reject("error.upload.failed.fileAccess", "An error occurred while accessing your file.");
		} finally {
			/*
			 * clear temporary file, but keep pdf's
			 */
			if (file != null && !keepTempFile) {
				log.debug("deleting uploaded temp file");
				file.delete();
			}
		}
		return null;
	}
	
	/**
	 * Convertes a String into a BibTeX String
	 * if selection is BibTeX nothing happens
	 * if selection is EndNote is will be converted to BibTex
	 * @param selection
	 * @return
	 */
	public String handleSelection(final String selection) {
		// FIXME: at this point we must first convert to bibtex!
		if (EndnoteToBibtexConverter.canHandle(selection)) {
			return this.endnoteToBibtexConverter.endnoteToBibtex(selection);
		}
		if (RisToBibtexConverter.canHandle(selection)) {
			return new RisToBibtexConverter().risToBibtex(selection);
		}
		/*
		 * should be BibTeX
		 */
		return selection;
	}
	
	/**
	 * Attach the uploaded file to the command. This is required to attach the file to the new post
	 * 
	 * @param command
	 * @param document
	 */
	private void handleNonSnippetFile(final PostPublicationCommand command, final Document document) {
		if (!present(command.getFileName())) {
			command.setFileName(new ArrayList<String>());
		}
		command.getFileName().add(document.getMd5hash() + document.getFile().getName() + document.getFileName());
	}

	public FileUploadFactory getUploadFactory() {
		return this.uploadFactory;
	}

	public void setUploadFactory(FileUploadFactory uploadFactory) {
		this.uploadFactory = uploadFactory;
	}

	public EndnoteToBibtexConverter getEndnoteToBibtexConverter() {
		return this.endnoteToBibtexConverter;
	}

	public void setEndnoteToBibtexConverter(EndnoteToBibtexConverter endnoteToBibtexConverter) {
		this.endnoteToBibtexConverter = endnoteToBibtexConverter;
	}
}
