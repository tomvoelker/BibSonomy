package org.bibsonomy.webapp.controller.actions;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.rest.utils.FileUploadInterface;
import org.bibsonomy.rest.utils.impl.FileUploadFactory;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.webapp.command.actions.UploadFileCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.UploadFileValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * this controller implements picture upload, converts uploaded picture to 
 * defined size and stores the picture in a defined folder.
 * this is a adapted version of UploadFileController
 * @author Waldemar Lautenschlager
 * @version $Id$
 */
public class PictureUploadController implements ValidationAwareController<UploadFileCommand>, ErrorAware{
	private static final Log log = LogFactory.getLog(UploadFileController.class);

	private Errors errors = null;
	
	/**
	 * Size if a largest side of the converted picture
	 */
	private final int SIZE_OF_LARGEST_SIDE = 200;
	
	/**
     * the factory used to get an instance of a FileUploadHandler.
     */
    private FileUploadFactory uploadFactory;
    
    /**
     * path to picture folder
     */
    private String picturePath;
	
	@Override
	public View workOn(UploadFileCommand command) {
		
		final RequestWrapperContext context = command.getContext();
		final String[] extensions = {"png", "jpg"};
		
		// check if user is logged in, if not throw an error and go directly
		// back to uploadPage
		if (!context.isUserLoggedIn()) {
			errors.reject("error.general.login");
			return Views.ERROR;
		}

		/*
		 * check credentials to fight CSRF attacks 
		 */
		if (!context.isValidCkey()) {
			errors.reject("error.field.valid.ckey");
			/*
			 * FIXME: correct URL?
			 */
			return Views.ERROR;
		}
		
		if (command.getFile() != null) {

			try {
				/*
				 * upload file
				 */
				final FileUploadInterface uploadFileHandler = this.uploadFactory.getFileUploadHandler(Collections.singletonList(command.getFile().getFileItem()), extensions);
				File file = uploadFileHandler.writeUploadedFile().getFile();
				
				/*
				 * convert picture
				 */
				BufferedImage userPic = convert(file);
				
				/*
				 * delete uploaded file
				 */
				file.delete();//TODO muss die Datei wirklich geloescht werden?
				
				String username = context.getLoginUser().getName();
				
				/*
				 * pattern of the picture filename: hash(username)_username.jpg
				 * pattern of the picture path: /picturefolder/"first two chars of filename"
				 */
				String filename = "/" +StringUtils.getMD5Hash(username) + "_" + username + ".jpg";
				String path = picturePath + filename.substring(1, 3);
				
				/*
				 * check existence of target folder
				 */
				file = new File(path);
				if(!file.exists()) {
					file.mkdir();
				}
				
				/*
				 * store picture
				 */
				file = new File(path + filename);
				ImageIO.write(userPic, "jpeg", file);


			} catch (Exception ex) {
				errors.reject("error.upload.failed", new Object[] { ex.getLocalizedMessage() }, "Sorry, we could not process your upload request, an unknown error occurred.");
				return Views.ERROR;
			}
		}
		
		return new ExtendedRedirectView("/settings");
	}
	
	/**
	 * Converts picture to standard size
	 * @param imageFile
	 * @return ready to write BufferedImage
	 * @throws IOException
	 */
	private BufferedImage convert(File imageFile) throws IOException {
		Image image = ImageIO.read(imageFile);
		Image scaledImage = null;
		
		/*
		 * convert picture to the standard size with fixed aspect ratio
		 */
		if (image.getWidth(null) >= image.getWidth(null)) {
			scaledImage = image.getScaledInstance(SIZE_OF_LARGEST_SIDE, 
					-1, Image.SCALE_SMOOTH);
		} else {
			scaledImage = image.getScaledInstance(-1, SIZE_OF_LARGEST_SIDE, 
					Image.SCALE_SMOOTH);
		}
		
		/*
		 * create new BufferedImage with converted picture
		 */
		BufferedImage outImage = new BufferedImage(scaledImage.getWidth(null),
				scaledImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics g = outImage.getGraphics();
		g.drawImage(scaledImage, 0, 0, null);
		g.dispose();
		
		return outImage;
	}
	
	
	
	@Override
	public UploadFileCommand instantiateCommand() {
		return new UploadFileCommand();
	}
	
	
	/**
	 * @param picturePath the picturePath to set
	 */
	public void setPicturePath(String picturePath) {
		this.picturePath = picturePath;
	}

	/**
	 * @return the picturePath
	 */
	public String getPicturePath() {
		return picturePath;
	}

	@Override
	public Errors getErrors() {
		return errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
		
	}


	@Override
	public boolean isValidationRequired(UploadFileCommand command) {
		return true;
	}

	@Override
	public Validator<UploadFileCommand> getValidator() {
		return new UploadFileValidator();
	}
	
	/**
	 * @return FileUploadFactory
	 */
	public FileUploadFactory getUploadFactory() {
		return this.uploadFactory;
	}

	/**
	 * @param uploadFactory
	 */
	public void setUploadFactory(final FileUploadFactory uploadFactory) {
		this.uploadFactory = uploadFactory;
	}


}
