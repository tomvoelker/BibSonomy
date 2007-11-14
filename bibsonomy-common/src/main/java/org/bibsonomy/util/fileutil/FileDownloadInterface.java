package org.bibsonomy.util.fileutil;

import java.io.BufferedInputStream;

import org.apache.log4j.Logger;

/**
 *
 * @version $Id$
 * @author  Christian Kramer
 *
 */
public interface FileDownloadInterface {

	public abstract BufferedInputStream getBuf();

}