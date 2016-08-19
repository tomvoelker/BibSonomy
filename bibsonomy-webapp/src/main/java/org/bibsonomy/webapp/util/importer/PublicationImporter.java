/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.util.importer;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.UnsupportedFileTypeException;
import org.bibsonomy.scraper.converter.EndnoteToBibtexConverter;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.ConversionException;
import org.bibsonomy.services.filesystem.FileLogic;
import org.bibsonomy.services.filesystem.extension.ListExtensionChecker;
import org.bibsonomy.util.Sets;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.file.ServerUploadedFile;
import org.bibsonomy.webapp.command.actions.PostPublicationCommand;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

/**
 * Handles publication posting/upload for other controllers, e.g., the PostPublicationController.
 * 
 * @author rja
 */
public class PublicationImporter {
	private static final Log log = LogFactory.getLog(PublicationImporter.class);
	
	private static final ListExtensionChecker EXTENSION_CHECKER_BIBTEX_ENDNOTE = new ListExtensionChecker(FileLogic.BIBTEX_ENDNOTE_EXTENSIONS);

	
	private FileLogic fileLogic;
	
	/**
	 * converter from Endnote to BibTeX
	 */
	private EndnoteToBibtexConverter endnoteToBibtexConverter;
	
	
	/**
	 * Handles an uploaded file and returns its contents - if necessary 
	 * after converting EndNote to BibTeX;
	 * 
	 * @param command
	 * @param errors 
	 * @return the file contents null if only a file was uploaded
	 */
	public String handleFileUpload(final PostPublicationCommand command, final Errors errors) {
		boolean keepTempFile = false;
		/*
		 * get temp file
		 */
		File file = null;
		String fileContent = null;
		try {
			final MultipartFile uploadedFile = command.getFile();
			if (!present(uploadedFile) || !present(uploadedFile.getName())) {
				errors.reject("error.upload.failed.noFileSelected");
				return null;
			}
			final String fileName = uploadedFile.getOriginalFilename();
			
			// check if uploaded file is one of allowed files, otherwise it can be a endnote or bibtex file
			if (StringUtils.matchExtension(fileName, FileLogic.DOCUMENT_EXTENSIONS)) {
				log.debug("the file is in pdf format");
				file = this.fileLogic.writeTempFile(new ServerUploadedFile(uploadedFile), this.fileLogic.getDocumentExtensionChecker());
				if (!present(command.getFileName())) {
					command.setFileName(new ArrayList<String>());
				}
				command.getFileName().add(file.getName() + fileName);
				keepTempFile = true;
				return null;
			}
			
			file = this.fileLogic.writeTempFile(new ServerUploadedFile(uploadedFile), EXTENSION_CHECKER_BIBTEX_ENDNOTE);

			final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), command.getEncoding()));
			if (!StringUtils.matchExtension(fileName, Sets.asSet(FileLogic.BIBTEX_EXTENSION))) {
				/*
				 * In case the uploaded file is in EndNote or RIS format, we convert it to BibTeX.
				 */
				log.debug("the file is in EndNote format");
				fileContent = this.endnoteToBibtexConverter.endnoteToBibtexString(reader);
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
			 * FIXME add also extensions form DOCUMENT_EXTENSION to the message? 
			 */
			errors.reject("error.upload.failed.filetype", new Object[] {StringUtils.implodeStringCollection(FileLogic.BIBTEX_ENDNOTE_EXTENSIONS, ", ")}, e.getMessage());
		} catch (final Exception ex1) {
			errors.reject("error.upload.failed.fileAccess", "An error occurred while accessing your file.");
		} finally {
			/*
			 * clear temporary file, but keep pdf's
			 */
			if (file != null && !keepTempFile) {
				log.debug("deleting uploaded temp file");
				this.fileLogic.deleteTempFile(file.getName());
			}
		}
		return null;
	}
	
	/**
	 * converts a String into a BibTeX String
	 * if selection is BibTeX nothing happens
	 * if selection is e.g. EndNote is will be converted to BibTex
	 * @param selection
	 * @return the selection in BibTeX format
	 */
	public String handleSelection(final String selection) {
		// FIXME: at this point we must first convert to bibtex!
		if (EndnoteToBibtexConverter.canHandle(selection)) {
			return this.endnoteToBibtexConverter.toBibtex(selection);
		}
		if (RisToBibtexConverter.canHandle(selection)) {
			return new RisToBibtexConverter().toBibtex(selection);
		}
		/*
		 * should be BibTeX
		 */
		return selection;
	}

	/**
	 * @param endnoteToBibtexConverter the endnoteToBibtexConverter to set
	 */
	public void setEndnoteToBibtexConverter(final EndnoteToBibtexConverter endnoteToBibtexConverter) {
		this.endnoteToBibtexConverter = endnoteToBibtexConverter;
	}

	/**
	 * @param fileLogic the fileLogic to set
	 */
	public void setFileLogic(FileLogic fileLogic) {
		this.fileLogic = fileLogic;
	}
}
