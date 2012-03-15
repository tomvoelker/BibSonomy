package org.bibsonomy.entity;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.bibsonomy.model.PersonName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

class LuceneTest {
	public void HelloLucene(ArrayList<LinkedList<PersonName>> allAuthorsWithCoAuthors) throws IOException, ParseException {
		//0. Specify the analyzer for tokenizing text.
		//The same analyzer should be used for indexing and searching
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);

		//1. create the index
		Directory index = new RAMDirectory();

		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35, analyzer);
		IndexWriter w = new IndexWriter(index, config);
		
		for (List<PersonName> allAuthorsOfOnePublication: allAuthorsWithCoAuthors) { //iterate every publication
			for (PersonName author: allAuthorsOfOnePublication) { //iterate all authors
				//add the author
				Document doc = new Document();
				doc.add(new Field("author", entityIdentification.normalizePerson(author), Field.Store.YES, Field.Index.ANALYZED));
				addAuthorDoc(w,doc,entityIdentification.normalizePerson(author));
				for (PersonName coauthor: allAuthorsOfOnePublication) {
					//add the coauthors as document
					if (author != coauthor) doc.add(new Field("coauthor", entityIdentification.normalizePerson(coauthor), Field.Store.YES, Field.Index.ANALYZED));;
				}
			w.addDocument(doc);
			}
		}
		
		w.close();

		//2. query
		String querystr = "author:b.ganter AND coauthor:r.wille";

		//the "title" arg specifies the default field to use
		//when no field is explicitly specified in the query.
		Query q = new QueryParser(Version.LUCENE_35, "author", analyzer).parse(querystr);

		//3. search
		int hitsPerPage = 10;
		IndexReader reader = IndexReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
	   
		//4. display results
		System.out.println("Found " + hits.length + " hits.");
		for(int i=0;i<hits.length;++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			System.out.println((i + 1) + ". " + d.get("author"));
		}

		//searcher can only be closed when there
		//is no need to access the documents any more.
		searcher.close();
		}

	public void addAuthorDoc(IndexWriter w, Document doc, String value) throws IOException {
		doc.add(new Field("author", value, Field.Store.YES, Field.Index.ANALYZED));
		w.addDocument(doc);
	}
	
	public void addCoauthorDoc(IndexWriter w, Document doc, String value) throws IOException {
		doc.add(new Field("coauthor", value, Field.Store.YES, Field.Index.ANALYZED));
		w.addDocument(doc);
	}
}
