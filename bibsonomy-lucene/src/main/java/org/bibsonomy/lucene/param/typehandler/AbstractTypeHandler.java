package org.bibsonomy.lucene.param.typehandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * abstract base class for type lucene handler
 *  
 * @author fei
 * @version $Id$
 * @param <T> 
 */
public abstract class AbstractTypeHandler<T> implements LuceneTypeHandler<T> {
	protected static final Log log = LogFactory.getLog(AbstractTypeHandler.class);
}
