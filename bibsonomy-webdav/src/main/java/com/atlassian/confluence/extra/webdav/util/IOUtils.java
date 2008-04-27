/*
 * Copyright (c) 2006, David Peterson
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of "randombits.org" nor the names of its contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.atlassian.confluence.extra.webdav.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * @author d.peterson
 */
public final class IOUtils {

    /**
     * Never create an IOUtils instance.
     */
    private IOUtils() {
    }

    /**
     * Reads all the data from the input stream and writes it to the output
     * stream.
     * 
     * @param in
     *            the InputStream to read from
     * @param out
     *            the OutputStream to write to
     * @return the number of bytes transferred
     * @throws IOException
     *             if there was a problem in the transfer
     */
    public static int pipe( InputStream in, OutputStream out ) throws IOException {
        byte[] buff = new byte[1024];
        int size;
        int count = 0;
        while ( ( size = in.read( buff ) ) != -1 ) {
            out.write( buff, 0, size );
            count += size;
        }
        return count;
    }

    /**
     * Reads all the data from the reader and writes it to the writer.
     * 
     * @param in
     *            the Reader to read from
     * @param out
     *            the Writer to write to
     * @return the number of characters transferred
     * @throws IOException
     *             if there was a problem in the transfer
     */
    public static int pipe( Reader in, Writer out ) throws IOException {
        char[] buff = new char[1024];
        int size;
        int count = 0;
        while ( ( size = in.read( buff ) ) != -1 ) {
            out.write( buff, 0, size );
            count += size;
        }
        return count;
    }

    /**
     * Copies the contents of <code>from</code> into the file at
     * <code>to</code>. If the file, or the directories leading to the file
     * at <code>to</code> do not exist, they are created automatically.
     * 
     * @throws FileNotFoundException if one fo the files could not be opened.
     * @throws IOException if there is an IO exception.
     */
    public static int copy( File from, File to ) throws IOException {
        BufferedInputStream in = new BufferedInputStream( new FileInputStream( from ) );
        if ( !to.exists() ) {
            File parent = to.getParentFile();
            if ( !parent.exists() )
                parent.mkdirs();
            to.createNewFile();
        }
        FileOutputStream out = new FileOutputStream( to );

        int transferred = pipe( in, out );

        in.close();
        out.close();

        return transferred;
    }
}
