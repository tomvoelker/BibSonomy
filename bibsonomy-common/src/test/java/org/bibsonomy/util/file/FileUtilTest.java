/**
 *
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
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

import java.io.File;
import junit.framework.Assert;

import org.junit.Test;

/**
 * @author Jens Illig
 */
public class FileUtilTest {
	
	/**
	 * tests getFilePath
	 */
	@Test
	public void getFilePath() {
        String tmpPath = System.getProperty("java.io.tmpdir");
        String exp = tmpPath + "ab" + File.separator +  "abcde";
        Assert.assertEquals(exp, FileUtil.getFilePath(tmpPath + File.separator, "abcde"));
        Assert.assertEquals(exp, FileUtil.getFilePath(tmpPath, "abcde"));
	}
}
