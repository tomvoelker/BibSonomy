package org.bibsonomy.lucene.ranking;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.function.CustomScoreQuery;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class FolkRankQuery extends CustomScoreQuery {
	
	private static final long serialVersionUID = 4574170132365598782L;
	
	private FolkRankScoreProvider folkRankScoreProvider;
	
	private List<String> preferredTags;
	private List<String> preferredUsers;
	
	public FolkRankQuery(Query subQuery) {
		
		super(subQuery);
		
		preferredTags = new ArrayList<String>();
		preferredUsers = new ArrayList<String>();
	}
	
	@Override
	protected FolkRankScoreProvider getCustomScoreProvider(IndexReader indexReader) {
		
		if (folkRankScoreProvider == null) {
			folkRankScoreProvider = new FolkRankScoreProvider(preferredTags, preferredUsers);
		}
		
		folkRankScoreProvider.setIndexReader(indexReader);
		
		return folkRankScoreProvider;
	}
	
	public void addToPreferredTags(String... tags) {
		for (String tag : tags) {
			preferredTags.add(tag.toLowerCase());
		}
	}
	
	public void addToPreferredUsers(String... users) {
		for (String user : users) {
			preferredUsers.add(user.toLowerCase());
		}
	}
}
