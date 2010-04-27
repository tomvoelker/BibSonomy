/**
 *  
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *   
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.util.file;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.util.StringUtils;


/**
 * @author rja
 * @version $Id$
 */
public class FileUtil {
	/**
	 * The pattern extracts the extension of a file.
	 */
	private static final Pattern fileExtensionPattern = Pattern.compile("(.+)\\.(.+)");
	
	/**
	 * Constructs the file path of a document
	 * 
	 * @param documentPath - the absolute path to the document directory in the filesystem
	 * @param documentFileHash - the filehash of the document 
	 * 
	 * @return The absolute path of the document on the file system.
	 */
	public static String getDocumentPath(final String documentPath, final String documentFileHash) {
		return documentPath + documentFileHash.substring(0, 2) + "/" + documentFileHash;
	}

	/**
	 * Depending on the extension of the file, returns the correct MIME content
	 * type. NOTE: the method looks only at the name of the file not at the
	 * content!
	 * 
	 * @param filename
	 *            - name of the file.
	 * @return - the MIME content type of the file.
	 */
	public static String getContentType(final String filename) {
		if (StringUtils.matchExtension(filename, "ps")) {
			return "application/postscript";
		} else if (StringUtils.matchExtension(filename, "pdf")) {
			return "application/pdf";
		} else if (StringUtils.matchExtension(filename, "txt")) {
			return "text/plain";
		} else if (StringUtils.matchExtension(filename, "djv", "djvu")) {
			return "image/vnd.djvu";
		} else {
			return "application/octet-stream";
		}
	}
	
	/**
	 * Extracts the extension of a file (without ".").
	 * 
	 * If no extension is found, "" is returned;
	 * 
	 * @param filename
	 * @return The extension of the given file.
	 */
	public static String getFileExtension(final String filename) {
		final Matcher m = fileExtensionPattern.matcher(filename);
		if (m.find())
			return m.group(2).toLowerCase();
		return "";
	}

}
