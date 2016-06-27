/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.util.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * a input stream that limits the read size
 *
 * @author dzo
 */
public class LimitedInputStream extends FilterInputStream {

	private long leftBytes;

	/**
	 * @param in
	 * @param maxBytes 
	 */
	public LimitedInputStream(final InputStream in, final long maxBytes) {
		super(in);
		this.leftBytes = maxBytes;
	}
	
	/* (non-Javadoc)
	 * @see java.io.FilterInputStream#read()
	 */
	@Override
	public int read() throws IOException {
		if (this.leftBytes == 0) {
			return -1;
		}
		final int read = super.read();
		if (read != -1) {
			this.leftBytes--;
		}
		return read;
	}
	
	/* (non-Javadoc)
	 * @see java.io.FilterInputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (this.leftBytes == 0) {
			return -1;
		}
		
		// check the length to read
		final int length = (int) Math.min(len, this.leftBytes);
		final int read = super.read(b, off, length);
		if (read > 0) {
			this.leftBytes -= read;
		}
		return read;
	}
	
	/* (non-Javadoc)
	 * @see java.io.FilterInputStream#skip(long)
	 */
	@Override
	public long skip(long n) throws IOException {
		final long toSkip = Math.min(n, this.leftBytes);
		
		final long skipped = super.skip(toSkip);
		this.leftBytes -= skipped;
		return skipped;
	}

}
