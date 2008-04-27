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
package com.atlassian.confluence.extra.webdav.servlet.resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA. User: david Date: Feb 18, 2006 Time: 12:26:16 PM To
 * change this template use File | Settings | File Templates.
 */
public interface SingleResource extends Resource {
    /**
     * Opens an input stream to retrieve the resource's content.
     * 
     * @return an open input stream.
     * @throws IOException
     *             if there was a problem creating the input stream.
     */
    InputStream getInputStream() throws IOException;

    /**
     * Opens an output stream to save new content to the resource.
     * 
     * @param input
     *            The input stream to read the new data from.
     * @param contentLength
     * @param contentType
     *            The content type of the new data.
     * @param encoding
     *            The character encoding scheme of the new data.
     * 
     * @throws IOException
     */
    void saveData( InputStream input, int contentLength, String contentType, String encoding )
            throws IOException;

    /**
     * The number of bytes in the resource content.
     * 
     * @return The number of bytes.
     */
    int getContentLength() throws IOException;

    /**
     * The standard MIME content type of the resource. Eg. "text/plain"
     * 
     * @return The resource's content type.
     */
    String getContentType();

    /**
     * The unique tag for the resource. May return <code>null</code> to
     * indicate the entity tag is not supported.
     * 
     * @return The entity tag, or <code>null</code>.
     */
    String getETag();
}
