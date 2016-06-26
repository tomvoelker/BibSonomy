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
