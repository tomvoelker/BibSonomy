package org.bibsonomy.rest.strategy.users;

import java.io.Writer;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.Document;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.strategy.AbstractCreateStrategy;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.util.fileutil.FileUploadInterface;
import org.bibsonomy.util.fileutil.HandleFileUpload;


/**
 * Handle the request to post a document
 * 
 * @version $Id$
 * @author Christian Kramer
 */
public class PostPostDocumentStrategy extends AbstractCreateStrategy{
	private final String userName;
	private final String resourceHash;
	private final List<FileItem> items;
	private final String docPath;
	private final String projectHome;
	private String uri;
	
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
		this.docPath = context.getAdditionalInfos().get("docPath");
		this.projectHome = context.getAdditionalInfos().get("projectHome");
	}
	
	@Override
	public void validate() throws ValidationException {
		if (!this.userName.equals(this.getLogic().getAuthenticatedUser().getName())) throw new ValidationException("You are not authorized to perform the requested operation");
	}

	@Override
	protected String create() {
		Document doc = new Document();
		
		try {
			// create a fileupload object to get the filename and filehash
			FileUploadInterface up = new HandleFileUpload(this.items);
			
			// fill the document object with all necessary informations
			doc.setUserName(this.userName);
			doc.setFileName(up.getFileName());
			doc.setFileHash(up.getFileHash());
			doc.setMd5hash(up.getMd5Hash());
			
			// add document to post
			this.getLogic().addDocument(doc, this.resourceHash);
			
			// write the file to the hdd
			up.writeUploadedFiles(this.docPath);
			uri = this.projectHome + "api/users/" + doc.getUserName() + "/posts/" + this.resourceHash + "/documents/" + doc.getFileName(); 
			
			return uri;
		} catch (Exception ex) {
			throw new BadRequestOrResponseException(ex.getMessage());
		}
	}

	@Override
	protected void render(Writer writer, String uri) {
		this.getRenderer().serializeURI(writer, uri);
	}

	@Override
	protected String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}
	
}