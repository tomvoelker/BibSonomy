/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.util.file;

import java.io.File;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.enums.PreviewSize;
import org.bibsonomy.util.Sets;
import org.bibsonomy.util.StringUtils;


/**
 * @author rja
 */
public class FileUtil {
	private static final Set<String> DJV_EXTENSIONS = Sets.asSet("djv", "djvu");
	private static final Set<String> TIFF_EXTENSIONS = Sets.asSet("tif", "tiff");
	private static final Set<String> PNG_EXTENSIONS = Sets.asSet("png");
	private static final Set<String> PLAIN_EXTENSIONS = Sets.asSet("txt", "tex");
	private static final Set<String> PDF_EXTENSIONS = Sets.asSet("pdf");
	private static final Set<String> SVG_EXTENSIONS = Sets.asSet("svg");
	private static final Set<String> POST_SCRIPT_EXTENSIONS = Sets.asSet("ps");

	/**
	 * default file extension for JPEG images
	 */
	public static final String EXTENSION_JPG = "jpg";

	private static final Set<String> JPEG_EXTENSIONS = Sets.asSet(EXTENSION_JPG, "jpeg");

	/**
	 * content type for JPEG images
	 */
	public static final String CONTENT_TYPE_IMAGE_JPEG = "image/jpeg";

	/**
	 * a directory below the document directory, with the default preview images
	 */
	private static final String PREVIEW_DIR = "previews";
	
	/**
	 * The pattern extracts the extension of a file.
	 */
	private static final Pattern fileExtensionPattern = Pattern.compile("(.+)\\.(.+)");
	
	/**
	 * Constructs the file path of a document
	 * 
	 * @param filePath - the absolute path to the document directory in the file system
	 * @param fileName - the file name of the document (an MD5 hash) 
	 * 
	 * @return The absolute path of the document on the file system.
	 */
	public static String getFilePath(final String filePath, final String fileName) {
		File dir = getFileDirAsFile(filePath, fileName);
		if (dir.exists() == false) {
			if (!dir.mkdir()) {
				throw new RuntimeException("directory '" + dir.getAbsolutePath() + "' could not be created");
			}
		}
		return new File(dir, fileName).getAbsolutePath();
	}
	
	/**
	 * Create a path to a document a particular user has attached to a resource.
	 * The difference to getPreviewPath is that we create here only paths to documents
	 * which a particular user has uploaded.
	 * 
	 * @param filePath - the document path
	 * @param fileName - the documents name on disk
	 * @param documentFileName - the original name of the document - used to guess the MIME type
	 * @param preview - the type of preview that is requested
	 * @return The path to the preview image or to a default preview image.
	 */
	public static String getUserDocumentPreviewPath(final String filePath, final String fileName, final String documentFileName, final PreviewSize preview) {
		/*
		 * first check, if real preview exists
		 */
		final String previewFilePath = getFilePath(filePath, fileName + "_" + preview.name() + "." + EXTENSION_JPG);
		if (new File(previewFilePath).isFile()) return previewFilePath;
		/*
		 * guess content type
		 */
		final String contentType = getContentType(documentFileName);
		/*
		 * build file path and name, something like
		 * filePath/previews/image_jpeg_SMALL.jpg
		 */
		return filePath + PREVIEW_DIR + "/" + contentType.replaceAll("[\\./]", "_") + "_" + preview.name() + "." + EXTENSION_JPG;
	}
	
	
	/**
	 * Create a path to a global preview image of a given resource. The difference to
	 * getUserDocumentPreviewPath is that we create here paths to preview images
	 * for which no user has necessarily uploaded a document (especially bookmarks).
	 * 
	 * @param previewPath - the path to the previews
	 * @param intrahash - the intrahash of the resource
	 * @param preview - preview size
	 * @return the path to the preview image
	 */
	public static String getPreviewPath(final String previewPath, final String intrahash, final PreviewSize preview) {
		return getFilePath(previewPath, intrahash) + "_" + preview.name();
	}

	/**
	 * Constructs the directory path of the file using the first two digits of
	 * the file name.
	 * 
	 * @param filePath - the absolute path to the document directory in the file system
	 * @param fileName - the file name of the document (an MD5 hash)
	 * @return The directory of the file
	 */
	public static String getFileDir(final String filePath, final String fileName) {
		return getFileDirAsFile(filePath, fileName).getAbsolutePath();
	}
	
	/**
	 * Constructs the directory path of the file using the first two digits of
	 * the file name.
	 * 
	 * @param filePath - the absolute path to the document directory in the file system
	 * @param fileName - the file name of the document (an MD5 hash)
	 * @return The directory of the file
	 */
	public static File getFileDirAsFile(final String filePath, final String fileName) {
		return new File(filePath, fileName.substring(0, 2));
	}
	
	/**
	 * Depending on the extension of the file, returns the correct MIME content
	 * type. NOTE: the method looks only at the name of the file not at the
	 * content!
	 * 
	 * NOTE: {@link #getUserDocumentPreviewPath(String, String, String, PreviewSize)} depends
	 * on the content types returned by this method. If you add a new content type,
	 * you must also add the corresponding preview image!
	 * 
	 * @param filename
	 *            - name of the file.
	 * @return - the MIME content type of the file.
	 */
	public static String getContentType(final String filename) {
		if (StringUtils.matchExtension(filename, POST_SCRIPT_EXTENSIONS)) {
			return "application/postscript";
		}
		if (StringUtils.matchExtension(filename, PDF_EXTENSIONS)) {
			return "application/pdf";
		}
		if (StringUtils.matchExtension(filename, PLAIN_EXTENSIONS)) {
			return "text/plain";
		}
		if (StringUtils.matchExtension(filename, DJV_EXTENSIONS)) {
			return "image/vnd.djvu";
		}
		if (StringUtils.matchExtension(filename, JPEG_EXTENSIONS)) {
			return CONTENT_TYPE_IMAGE_JPEG;
		}
		if (StringUtils.matchExtension(filename, PNG_EXTENSIONS)) {
			return "image/png";	
		}
		if (StringUtils.matchExtension(filename, TIFF_EXTENSIONS)) {
			return "image/tiff";
		}
		if (StringUtils.matchExtension(filename, SVG_EXTENSIONS)) {
			return "image/svg+xml";
		}
		
		return "application/octet-stream";
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
