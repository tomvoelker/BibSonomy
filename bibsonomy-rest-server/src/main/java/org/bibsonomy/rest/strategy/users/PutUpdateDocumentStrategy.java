package org.bibsonomy.rest.strategy.users;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.Writer;

import org.apache.commons.io.FilenameUtils;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.model.Document;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.strategy.AbstractUpdateStrategy;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.services.filesystem.extension.ExtensionChecker;

/**
 * strategy for renaming documents
 * 
 * @author lukas
 */
public class PutUpdateDocumentStrategy extends AbstractUpdateStrategy {
	private static final String[][] consistentSuffixes = {{"jpg","jpeg","png", "tif", "tiff"}, {"djv", "djvu"}, {"txt", "tex"},
		{"doc", "docx", "ppt", "pptx", "xls", "xlsx"}, {"ods", "odt", "odp"}, {"htm", "html"}};

	private final String oldFilename;
	private final String resourceHash;
	private final String userName;
	private final ExtensionChecker extensionChecker;
	
	
	/**
	 * @param context
	 * @param userName
	 * @param resourceHash
	 * @param oldFilename 
	 */
	public PutUpdateDocumentStrategy(final Context context, final String userName, final String resourceHash, final String oldFilename) {
		super(context);
		this.userName = userName;
		this.resourceHash = resourceHash;
		this.oldFilename = oldFilename;
		this.extensionChecker = context.getFileLogic().getDocumentExtensionChecker();
	}
	
	@Override
	protected void render(Writer writer, String resourceID) {
		this.getRenderer().serializeResourceHash(writer, resourceHash);
	}
	
	@Override
	protected String update() {
		/*
		 * check if document exists
		 */
		final Document toUpdate = this.getLogic().getDocument(this.userName, this.resourceHash, this.oldFilename);
		
		if (!present(toUpdate)) {
			throw new ObjectNotFoundException(oldFilename);
		}
		
		final Document sentDocument = this.getRenderer().parseDocument(this.doc, this.getUploadAccessor());
		final String newFilename = sentDocument.getFileName();
		
		if (!present(newFilename)) {
			throw new BadRequestOrResponseException("No new filename was specified.");
		}
		
		// FIXME: duplicate code @see DocumentsController
		// check for consistent suffixes
		if (!this.checkConsistency(newFilename)) {
			throw new BadRequestOrResponseException("The file suffixes are inconsistent!");
		}
		
		// check supported file extensions
		if (!this.extensionChecker.checkExtension(newFilename)) {
			throw new BadRequestOrResponseException("The new filename has no valid file suffix.");
		}
		
		// check for forbidden symbols
		if (newFilename.matches(".*[<>/\\\\].*")) {
			throw new BadRequestOrResponseException("The new filename contains one ore more forbidden symbol.");
		}
		
		if (!toUpdate.getUserName().equals(this.userName)) {
			throw new AccessDeniedException("Only the owner of the file is allowed to change it!");
		}
		
		this.getLogic().updateDocument(toUpdate, resourceHash, newFilename);
		
		return resourceHash;
	}

	/**
	 * checks whether the file suffixes are consistent or not
	 * 
	 * @param newFilename the new filename
	 * @return true if the suffixes are consistent, false if not
	 */
	private boolean checkConsistency(String newFilename) {
		final String newSuffix = FilenameUtils.getExtension(newFilename);
		final String oldSuffix = FilenameUtils.getExtension(this.oldFilename);
		
		if (oldSuffix.equalsIgnoreCase(newSuffix)) {
			return true;
		}
		
		boolean foundOld = false;
		boolean foundNew = false;
		for (int i = 0; i < consistentSuffixes.length; i++) {
			for (int j = 0; j < consistentSuffixes[i].length; j++) {
				if (oldSuffix.equalsIgnoreCase(consistentSuffixes[i][j])) {
					foundOld = true;
				}
				else if(newSuffix.equalsIgnoreCase(consistentSuffixes[i][j])) {
					foundNew = true;
				}
			}
			if (foundOld || foundNew) {
				return foundOld && foundNew;
			}
		}
		
		return false;
	}

}
