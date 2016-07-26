/**
 * BibSonomy-Rest-Server - The REST-server.
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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
