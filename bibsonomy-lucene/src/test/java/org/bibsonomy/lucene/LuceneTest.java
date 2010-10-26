package org.bibsonomy.lucene;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.lucene.util.JNDITestDatabaseBinder;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @author dzo
 * @version $Id$
 */
public abstract class LuceneTest {
    
    private static final Log log = LogFactory.getLog(LuceneTest.class);

    /**
     * binds bibsonomy_lucene context
     */
    @BeforeClass
    public static void bind() {
	JNDITestDatabaseBinder.bind();
    }

    /**
     * unbinds bibsonomy_lucene context
     */
    @AfterClass
    public static void unbind() {
	JNDITestDatabaseBinder.unbind();
    }

    /**
     * deletes a file (even a folder!)
     * 
     * @param file
     * @return <code>true</code> iff the file was deleted successfully
     */
    public static boolean deleteFile(final File file) {
	if (file.isDirectory()) {
	    final File[] children = file.listFiles();
	    for (final File child : children) {
		if (!deleteFile(child)) {
		    log.warn("couldn't delete file " + file.getAbsolutePath());
		}
	    }
	}

	return file.delete();
    }
}
