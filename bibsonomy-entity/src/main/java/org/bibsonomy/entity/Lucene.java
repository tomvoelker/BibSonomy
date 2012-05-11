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

	public void createLuceneIndexForAllAuthors(SqlSession sessionRkr) throws IOException, ParseException {
		List<Map<Integer,String>> authorList = sessionRkr.selectList("org.mybatis.example.Entity-Identification.selectCoAuthorsLucene");

		analyzer = new StandardAnalyzer(Version.LUCENE_35);
		index = new RAMDirectory();

		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35, analyzer);
		w = new IndexWriter(index, config);

		int lastAuthorID = 0;
		String coauthors = "";
		Document doc = new Document();
		for (Map<Integer,String> singleAuthorWithCoauthors: authorList) { //iterate every publication
			//every new author is a single document
			if (lastAuthorID != Integer.valueOf(String.valueOf(singleAuthorWithCoauthors.get("author_id")))) {
				coauthors += singleAuthorWithCoauthors.get("normalized_coauthor");
				doc.add(new Field("author", singleAuthorWithCoauthors.get("normalized_name"), Field.Store.YES, Field.Index.ANALYZED)); //add the normalized name to author field
				doc.add(new Field("coauthors", coauthors, Field.Store.YES, Field.Index.ANALYZED)); //add normalized name to coauthor field
				doc.add(new Field("ID", String.valueOf((singleAuthorWithCoauthors.get("author_id"))), Field.Store.YES, Field.Index.NO)); //add normalized name to coauthor field
				w.addDocument(doc);
				
				//System.out.println("new document: author: " + doc.get("author") + "; coauthors: " + doc.get("coauthors")  + "; ID: " + String.valueOf(singleAuthorWithCoauthors.get("author_id")));
				
				doc = new Document();
				coauthors = "";
				lastAuthorID = Integer.valueOf(String.valueOf(singleAuthorWithCoauthors.get("author_id")));
			}
			//System.out.println("we add author: " + singleAuthorWithCoauthors.get("normalized_name") + " coauthors: " + singleAuthorWithCoauthors.get("normalized_coauthor") + " ID: " + String.valueOf(singleAuthorWithCoauthors.get("author_id")));
			coauthors += singleAuthorWithCoauthors.get("normalized_coauthor") + " ";
			//add the coauthors to the document
			
			
		}

		w.close();
	}

	public static List<Integer> searchAuthor(String normalizedName, List<String> normalizedCoauthors) throws IOException, ParseException {

		//create the query string with fields author and coauthor
		String querystr = "author:" + normalizedName + "~0.7";
		if (normalizedCoauthors != null && normalizedCoauthors.size() > 0) {
			querystr += " AND (coauthors:";
			for (int k=0; k<normalizedCoauthors.size()-1; k++) {
				querystr += normalizedCoauthors.get(k) + "~0.7 OR coauthors: ";
			}
			int lastElement = normalizedCoauthors.size()-1;
			if (lastElement > -1) querystr += normalizedCoauthors.get(lastElement) + "~0.7";
			querystr += ")";
		}

		System.out.println("query is: " + querystr);

		Query q = new QueryParser(Version.LUCENE_35, "author", analyzer).parse(querystr);
		int hitsPerPage = 100;
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
			System.out.println((i + 1) + ". " + d.get("author") + " coauthors: " + d.get("coauthors") + " authorID: " + d.get("ID") + " score: " + hits[i].score);
			saveAuthorIDs.add(Integer.valueOf(d.get("ID")));
		}

		searcher.close();
		System.out.println("we compare this lucene IDs: ");
		for (int id: saveAuthorIDs) {
			System.out.println(id);
		}
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
