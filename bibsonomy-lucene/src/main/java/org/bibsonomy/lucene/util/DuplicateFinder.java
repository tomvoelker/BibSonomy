package org.bibsonomy.lucene.util;

import java.io.File;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.bibsonomy.lucene.index.LuceneFieldNames;

/**
 * class for finding duplicate entries in a lucene index (with respect to a given field)
 * 
 * credits go to http://lucene.472066.n3.nabble.com/Index-Dedupe-td549923.html
 * 
 * @author fei
 * @version $Id$
 */
public class DuplicateFinder {
	
	/** list of fields to display for duplicates */
	private static final String[] fieldList = {
		LuceneFieldNames.CONTENT_ID,
		LuceneFieldNames.INTERHASH,
		LuceneFieldNames.TITLE
	};
	
	/** lucene index reader */
	private static IndexReader reader;

	/**
	 * search for duplicate entries 
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		if( args.length != 2 ) {
			System.out.println("Usage: \n\t DuplicateFinder <path to index directory> <field name>");
			return;
		}
		
		try {
			findDuplicates(args[0], args[1], false);
		} catch (final Exception e) {
			System.out.println("Error processing index at '"+args[0]+"':");
			e.printStackTrace();
		}
	}

	/**
	 * Use termDocs() to iterate all the terms in a given unique field.  
	 * Duplicate entries are written to stdout and deleted if desired
	 * 
	 * @param indexPath path to the index
	 * @param fieldName name of the unique field
	 * @param doRemove if true, duplicate entries will be removed
	 * 
	 * @throws Exception
	 */
	public static void findDuplicates(final String indexPath, final String fieldName, final boolean doRemove) throws Exception {
		final Directory indexDirectory = FSDirectory.open(new File(indexPath));

		reader = IndexReader.open(indexDirectory);

		final TermEnum theTerms = reader.terms(new Term(fieldName));

		Term term = null;

		do {
			term = theTerms.term();

			if ((term == null) || !term.field().equalsIgnoreCase(fieldName) ) { 
				break;
			}

			if (theTerms.docFreq() > 1) {
				printDupsForTerm(term, theTerms.docFreq());
				if (doRemove) {
					removeDupsForTerm(term);
				}
			}
		} while (theTerms.next());
	}

	/**
	 * write out duplicate entries for given term
	 * 
	 * @param term
	 * @param docFreq number of duplicates
	 */
	private static void printDupsForTerm(final Term term, final int docFreq) throws Exception {
		System.out.print(docFreq+" duplicate entries for \n\t");
		
		final TermDocs td = reader.termDocs(term);

		for (int idx = 0; td.next(); ++idx) {
			System.out.print(td.doc() + "\t");
			final Document document = reader.document(td.doc());
			
			for (final String fieldName : fieldList) {
				System.out.print(document.get(fieldName)+"\t");
			}
			System.out.print("\n\t");
		}
		
		System.out.println();
	}

	/**
	 * skip the first doc for each term
	 * 
	 * @param term
	 * @throws Exception
	 */
	private static void removeDupsForTerm(final Term term) throws Exception {
		final TermDocs td = reader.termDocs(term);
		for (int idx = 0; td.next(); ++idx) {
			if (idx > 0) {
				reader.deleteDocument(td.doc());
			}
		}
	}
	
}
