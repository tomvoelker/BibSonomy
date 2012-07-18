package org.bibsonomy.lucene.ranking;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.function.CustomScoreProvider;

public class FolkRankScoreProvider extends CustomScoreProvider {

	private IndexReader indexReader;
	
	private List<String> tags;
	private List<String> users;
	
//	public FolkRankScoreProvider() {
//		super(null);
//	}
	
	public FolkRankScoreProvider(List<String> tags, List<String> users) {
		super(null);
		this.tags = tags;
		this.users = users;
	}
	
	public void setIndexReader(IndexReader indexReader) {
		this.indexReader = indexReader;
	}

	@Override
	public float customScore(int doc, float subQueryScore, float valSrcScore) {
		
		return customScore(doc, subQueryScore, null);
	}
	
	@Override
	public float customScore(int docId, float subQueryScore, float[] valSrcScores) {
	
		float score = 0f;
		
		Document doc = null;
		try {
			doc = indexReader.document(docId);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
			return 0f;
		} catch (IOException e) {
			e.printStackTrace();
			return 0f;
		}
		
		score += extractScores(doc, tags, "frtag");
		score += extractScores(doc, users, "fruser");
		
		return score;
	}
	
	private float extractScores(Document doc, List<String> items, String scoreFieldName) {
		
		float score = 0f;
		
		Fieldable field = null;
		char firstCharacter;
		
		for (String item : items) {
			firstCharacter = item.charAt(0);
			if (!Character.isLetter(firstCharacter)) {
				firstCharacter = '#';
			}
			field = doc.getFieldable(scoreFieldName + firstCharacter);
			if (field != null) {
				score += searchForItem(field.stringValue(), item);
			}
		}
		
		return score;
	}
	
	private float searchForItem(String fullText, String item) {
		
		String[] values = fullText.split(" ");
		
		for (int i = 0; i < values.length; i += 2) {
			if (values[i].equals(item)) {
				return Float.parseFloat(values[i+1]);
			}
		}
		
		return 0f;
	}
}
