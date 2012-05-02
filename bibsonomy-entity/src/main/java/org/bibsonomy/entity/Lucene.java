package org.bibsonomy.entity;

import org.apache.ibatis.session.SqlSession;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

class Lucene {
	IndexWriter w = null;
	static StandardAnalyzer analyzer = null;
	static Directory index = null;
	static List<Map<Document,Integer>> docIDs = new ArrayList<Map<Document,Integer>>();

	public void createLuceneIndexForAllAuthors(SqlSession sessionRkr) throws IOException, ParseException {
		List<Map<String,String>> authorList = sessionRkr.selectList("org.mybatis.example.Entity-Identification.selectCoAuthorsLucene",1);

		//0. Specify the analyzer for tokenizing text.
		//The same analyzer should be used for indexing and searching
		analyzer = new StandardAnalyzer(Version.LUCENE_35);

		//1. create the index
		index = new RAMDirectory();

		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35, analyzer);
		w = new IndexWriter(index, config);

		int lastAuthorID = 0;
		for (Map<String,String> singleAuthorWithCoauthors: authorList) { //iterate every publication
			Document doc = new Document();
			//every new author is a document
			if (lastAuthorID != Integer.valueOf((singleAuthorWithCoauthors.get("author_id")))) {
				Map<Document,Integer> docMap = new HashMap<Document,Integer>();
				docMap.put(doc, Integer.valueOf((singleAuthorWithCoauthors.get("author_id"))));
				docIDs.add(docMap);
				doc = new Document();
			}
			System.out.println("we add author: " + singleAuthorWithCoauthors.get("normalized_name") + " coauthor: " + singleAuthorWithCoauthors.get("normalized_coauthor"));
			doc.add(new Field("author", singleAuthorWithCoauthors.get("normalized_name"), Field.Store.YES, Field.Index.ANALYZED)); //add the normalized name to author field
			//add the coauthors to the document
			doc.add(new Field("coauthor", singleAuthorWithCoauthors.get("normalized_coauthor"), Field.Store.YES, Field.Index.ANALYZED)); //add normalized name to coauthor field
			w.addDocument(doc);
		}

		w.close();
		System.exit(1);
	}

	public static List<Integer> searchAuthor(String normalizedName, List<String> normalizedCoauthors) throws IOException, ParseException {

		//create the query string with fields author and coauthor
		String querystr = "author:" + normalizedName + "~0.7";
		if (normalizedCoauthors != null && normalizedCoauthors.size() > 0) {
			querystr += " AND (coauthor:";
			for (int k=0; k<normalizedCoauthors.size()-1; k++) {
				querystr += normalizedCoauthors.get(k) + "~0.7 AND ";
			}
			int lastElement = normalizedCoauthors.size()-1;
			if (lastElement > -1) querystr += normalizedCoauthors.get(lastElement) + "~0.7";
			querystr += ")";
		}
		
		System.out.println("query is: " + querystr);
		
		Query q = new QueryParser(Version.LUCENE_35, "author", analyzer).parse(querystr);
		int hitsPerPage = 5;
		IndexReader reader = IndexReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		System.out.println("Found " + hits.length + " hits.");
		Integer authorID = 0;
		List<Integer> saveAuthorIDs = new ArrayList<Integer>();
		for(int i=0;i<hits.length;++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			//get the authorID connected with this document
			for (int k=0; k<docIDs.size(); k++) {
				authorID = docIDs.get(k).get(d);
				if (authorID != null) break;
			}
			System.out.println((i + 1) + ". " + d.get("author") + " coauthors: " + d.get("coauthor"));
		}

		searcher.close();
		return saveAuthorIDs;
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
