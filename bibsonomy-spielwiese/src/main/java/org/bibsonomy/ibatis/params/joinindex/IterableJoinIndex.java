package org.bibsonomy.ibatis.params.joinindex;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.bibsonomy.ibatis.params.beans.TagIndex;

/**
 * Creates JoinIndex implementations that return index-pairs like
 * 
 * <pre>
 *  (1,2), (2,3), ..., (tagIndex.size-1, tagIndex.size)
 * </pre>
 * 
 * @author Christian Schenk
 */
public class IterableJoinIndex implements Iterable<JoinIndex> {

	/** tagIndex.size() is used for the max. amount of JoinIndex pairs */
	private final List<TagIndex> tagIndex;

	public IterableJoinIndex(final List<TagIndex> tagIndex) {
		this.tagIndex = tagIndex;
	}

	public Iterator<JoinIndex> iterator() {
		return new Iterator<JoinIndex>() {
			int indexCount = 1;

			public boolean hasNext() {
				// System.out.println("hasNext() - indexCount: " + this.indexCount + " size: " + tagIndex.size());
				return (this.indexCount < tagIndex.size());
			}

			public JoinIndex next() {
				// System.out.println("next()");
				if (hasNext()) {
					this.indexCount++;
					return new JoinIndex() {
						public int getIndex1() {
							return indexCount - 1;
						}

						public int getIndex2() {
							return indexCount;
						}
					};
				} else {
					throw new NoSuchElementException();
				}
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}