package org.bibsonomy.webapp.controller;

import java.io.File;

import org.bibsonomy.common.enums.PreviewSize;
import org.bibsonomy.util.file.FileUtil;
import org.bibsonomy.webapp.command.actions.DownloadFileCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for preview images of bookmark / publication resources.
 * For a given hash (e.g. 5753d669ec2f407fed3ceb6c73c3b045) it is checked 
 * whether a file exists at
 * 
 *   previewPath/57/5753d669ec2f407fed3ceb6c73c3b045_PREVIEWSIZE
 *   
 * whereby PREVIEWSIZE can be SMALL/MEDIUM/LARGE. If the file exists, it
 * is returned as JPEG image; otherwise a default image for bookmarks or publications.  
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 * @version $Id$
 */
public class PreviewController implements MinimalisticController<DownloadFileCommand> {

	/**
	 * path to the preview images
	 */
	private String previewPath = "";
	
	@Override
	public DownloadFileCommand instantiateCommand() {
		return new DownloadFileCommand();
	}

	@Override
	public View workOn(DownloadFileCommand command) {		
		final String intraHash = command.getIntrahash();
		final PreviewSize preview = command.getPreview();
		final String previewPath = FileUtil.getPreviewPath(this.previewPath, intraHash, preview);		
		/*
		 * set path to preview image, if existent
		 */
		final File previewImage = new File(previewPath);
		if (previewImage.exists()) {
			command.setPathToFile(previewPath);
			command.setFilename(intraHash + FileUtil.EXTENSION_JPG);			
		}
		/*
		 * otherwise, set path to dummy picture (bookmark.jpg / publication.jpg)
		 */
		else {
			command.setPathToFile(this.previewPath + command.getResourcetype().getSimpleName() + "." + FileUtil.EXTENSION_JPG);
			command.setFilename(command.getResourcetype().getSimpleName() + "." + FileUtil.EXTENSION_JPG);
		}
		/*
		 *  preview images are always JPEGs!
		 */
		command.setContentType(FileUtil.CONTENT_TYPE_IMAGE_JPEG);
		return Views.DOWNLOAD_FILE;
	}

	
	/**
	 * Setter for preview path.
	 * @param previewPath
	 */
	public void setPreviewPath(final String previewPath) {
		this.previewPath = previewPath;
	}

}
