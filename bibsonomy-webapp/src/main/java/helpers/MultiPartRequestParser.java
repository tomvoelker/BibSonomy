package helpers;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class MultiPartRequestParser {
	
	private Map<String, FileItem> fieldMap;
	
	public Map<String, FileItem> getFields(HttpServletRequest request, String rootPath) throws IOException, FileUploadException {
		
		List items = null;
		DiskFileItemFactory factory;
		ServletFileUpload upload;
		/* restrict the request size for uploading files */
		int maxRequestSize = 1024 * 1024 * 51;
		int maxThreshold = 1024 * 1024 * 10;
		/*
		 * Process upload TODO: Please check for production-ready version of
		 * commons-fileupload-1.1-dev.jar
		 */
		
		factory = new DiskFileItemFactory();
		// maximum size that will be stored in memory
		factory.setSizeThreshold(maxThreshold);
		// the location for saving data that is larger than getSizeThreshold()
		String tempPath = rootPath + "bibsonomy_temp";			
		
		factory.setRepository(new File(tempPath));
		
		upload = new ServletFileUpload(factory);
		// maximum size before a FileUploadException will be thrown
		upload.setSizeMax(maxRequestSize);
		
		/* ************************************************
		 * parse request, get data 
		 * ************************************************/
		/* Parse this request by the handler that gives us a list of items from the request */
		items = upload.parseRequest(request); // if it fails, FileUploadException will be catched below
		
		/* Convert list of items into map for convenience */
		fieldMap = new HashMap<String, FileItem>();
		Iterator iter = items.iterator();		
		while (iter.hasNext()) {
			FileItem temp = (FileItem) iter.next();
			fieldMap.put(temp.getFieldName(), temp);
		}
		
		// delete items (they're not longer needed)
		items.clear();
		
		return fieldMap;
	}
	
}
