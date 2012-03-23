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
import java.util.Map;

class Lucene {
	public void luceneSearch(ArrayList<LinkedList<PersonName>> allAuthorsWithCoAuthors) throws IOException, ParseException {
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
				doc.add(new Field("author", EntityIdentification.normalizePerson(author), Field.Store.YES, Field.Index.ANALYZED)); //add the normalized name to author field
				for (PersonName coauthor: allAuthorsOfOnePublication) {
					//add the coauthors as document
					if (author != coauthor) {
						//if (entityIdentification.normalizePerson(author).equals("b.ganter")) System.out.println("add: " + entityIdentification.normalizePerson(coauthor) + " to: " + entityIdentification.normalizePerson(author));
						doc.add(new Field("coauthor", EntityIdentification.normalizePerson(coauthor), Field.Store.YES, Field.Index.ANALYZED)); //add normalized name to coauthor field
					}
				}
			w.addDocument(doc);
			}
		}
		
		w.close();

		String querystr = "author:b.ganter~0.7 AND (coauthor:r.wille~0.7 OR coauthor:g.dorn~0.7)";
		
		Query q = new QueryParser(Version.LUCENE_35, "author", analyzer).parse(querystr);
		int hitsPerPage = 50;
		IndexReader reader = IndexReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
			   
		System.out.println("Found " + hits.length + " hits.");
		for(int i=0;i<hits.length;++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			System.out.println((i + 1) + ". " + d.get("author") + " coauthors: " + d.get("coauthor"));
		}

		searcher.close();
	}
	
	public static Directory createLuceneIndex(List<Map<Integer,String>> allAuthors) {
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
		Directory index = new RAMDirectory();
		
		try {
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35, analyzer);
			IndexWriter w = new IndexWriter(index, config);
		
			for (Map<Integer,String> author: allAuthors) { //iterate every publication
				Document doc = new Document();
				doc.add(new Field("author", author.get("normalized_name"), Field.Store.YES, Field.Index.ANALYZED)); //add the normalized name to author field
				doc.add(new Field("author_id", String.valueOf(author.get("author_id")), Field.Store.YES, Field.Index.ANALYZED)); //add the author_id to author_id field
				w.addDocument(doc);
			}
			w.close();
		}
		catch (IOException e) {}
		return index;
	}	
	
}
