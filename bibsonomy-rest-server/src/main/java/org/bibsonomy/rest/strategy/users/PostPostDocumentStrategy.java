package org.bibsonomy.rest.strategy.users;

import java.io.Writer;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.Document;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.strategy.AbstractCreateStrategy;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.utils.FileUploadInterface;
import org.bibsonomy.rest.utils.impl.FileUploadFactory;


/**
 * Handle the request to post a document
 * 
 * @author Christian Kramer
 * @version $Id$
 */
public class PostPostDocumentStrategy extends AbstractCreateStrategy{
	private final String userName;
	private final String resourceHash;
	private final List<FileItem> items;
	private final String projectHome;
	private String uri;
	
	private final FileUploadFactory fileUploadFactory;
	
	/**
	 * @param context
	 * @param userName
	 * @param resourceHash
	 */
	public PostPostDocumentStrategy(final Context context, final String userName, final String resourceHash) {
		super(context);
		this.userName = userName;
		this.resourceHash = resourceHash;
		this.items = context.getItemList();
		this.projectHome = context.getAdditionalInfos().get("projectHome");
		this.fileUploadFactory = new FileUploadFactory();
		this.fileUploadFactory.setDocpath(context.getAdditionalInfos().get("docPath"));
		this.fileUploadFactory.setTempPath(false);
	}
	
	@Override
	public void validate() throws ValidationException {
		// TODO: this check is also done by the dblogic
		if (!this.userName.equals(this.getLogic().getAuthenticatedUser().getName())) throw new ValidationException("You are not authorized to perform the requested operation");
	}

	@Override
	protected String create() {
		final FileUploadInterface up = fileUploadFactory.getFileUploadHandler(this.items, FileUploadInterface.fileUploadExt);
		
		try {

			final Document document = up.writeUploadedFile();
			/*
			 * add user name to document (needed by createDocument) 
			 */
			document.setUserName(this.userName);
			
			this.getLogic().createDocument(document, this.resourceHash);
			
			uri = this.projectHome + "api/users/" + this.userName + "/posts/" + this.resourceHash + "/documents/" + document.getFileName();
			
			return uri;
			
		} catch (final Exception ex) {
			throw new BadRequestOrResponseException(ex.getMessage());
		}
	}

	@Override
	protected void render(final Writer writer, final String uri) {
		this.getRenderer().serializeURI(writer, uri);
	}

	@Override
	protected String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}
	
}