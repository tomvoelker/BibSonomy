package org.bibsonomy.lucene.index;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.lucene.index.CorruptIndexException;
import org.bibsonomy.lucene.util.JNDITestDatabaseBinder;
import org.bibsonomy.lucene.util.LuceneSpringContextWrapper;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Resource;
import org.springframework.beans.factory.BeanFactory;

/**
 * generates lucene index for bibtex posts as configured in lucen.properties
 * 
 *  FIXME: configure via spring
 *  
 * @author fei
 *
 */
public class LuceneGenerateBibTexIndex extends LuceneGenerateResourceIndex<BibTex>{
	/** singleton pattern */
	private static LuceneGenerateBibTexIndex instance;
	
	/** bean factory for obtaining instances configured via spring */
	private static BeanFactory beanFactory;
	
	//------------------------------------------------------------------------
	// constructor
	//------------------------------------------------------------------------
	public LuceneGenerateBibTexIndex(Properties props)
	throws ClassNotFoundException, SQLException {
		super(props);
	}
	
	public LuceneGenerateBibTexIndex()
	throws ClassNotFoundException, SQLException {
		super();
	}

	//------------------------------------------------------------------------
	// public interface
	//------------------------------------------------------------------------
	/**
	 * main method - generate index from database as configured in property file
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, SQLException, CorruptIndexException, IOException {
		// configure jndi context
		JNDITestDatabaseBinder.bind();

		// create index
		LuceneGenerateResourceIndex<BibTex> indexer = LuceneGenerateBibTexIndex.getInstance();
		indexer.generateIndex();
		indexer.shutdown();
	}
	//------------------------------------------------------------------------
	// singleton pattern implementation
	//------------------------------------------------------------------------
	/**
	 * get readily configured instance 
	 */
	public static LuceneGenerateBibTexIndex getInstance() {
		if( instance==null ) {
			beanFactory = LuceneSpringContextWrapper.getBeanFactory();
			return (LuceneGenerateBibTexIndex)beanFactory.getBean("luceneBibTexIndexGenerator");
		} else
			return instance;
	}
	/**
	 * singelton instance for spring instantiation
	 */
	public static LuceneGenerateBibTexIndex getPreInitInstance() throws ClassNotFoundException, SQLException {
		if( instance==null )
			return new LuceneGenerateBibTexIndex();
		else
			return instance;
	}
	
	//------------------------------------------------------------------------
	// implementations for abstract methods
	//------------------------------------------------------------------------
	@Override
	protected Class<? extends Resource> getResourceType() {
		return BibTex.class;
	}
	
}
