package org.bibsonomy.lucene.util.generator;

public interface GenerateIndexCallback {
	void updateProgress(int percentage);
    void done();
}
