package org.bibsonomy.rest.fileupload;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.bibsonomy.model.util.data.Data;
import org.bibsonomy.model.util.data.DualDataWrapper;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author jensi
  */
public class DualUploadedFileAccessor extends UploadedFileAccessor {

	/**
	 * construct it
	 * @param request
	 */
	public DualUploadedFileAccessor(HttpServletRequest request) {
		super(request);
	}
	
	@Override
	public Data getData(String multipartName) {
		int separatorIndex = multipartName.indexOf(':');
		if (separatorIndex == -1) {
			return super.getData(multipartName);
		}
		String part1Name = StringUtils.trim(multipartName.substring(0, separatorIndex));
		String part2Name = StringUtils.trim(multipartName.substring(separatorIndex + 1, multipartName.length()));
		
		MultipartFile part1 = getUploadedFileByName(part1Name);
		MultipartFile part2 = getUploadedFileByName(part2Name);
		
		return new DualDataWrapper(new FileUploadData(part1), new FileUploadData(part2));
	}

}
