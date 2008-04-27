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

import com.atlassian.confluence.extra.webdav.servlet.WebdavClient;

/**
 * Provides a base class for Resources.
 */
public abstract class BaseResource implements Resource {
    /**
     * The parent of the resource.
     */
    private CollectionResource parent;

    private ResourceBackend backend;

    /**
     * Constructs the new resource.
     * 
     * @param parent
     *            The resource's parent. May be <code>null</code>.
     */
    public BaseResource( CollectionResource parent, ResourceBackend backend ) {
        this.parent = parent;
        this.backend = backend;
    }

    /**
     * The parent resource of this resource.
     * 
     * @return The resource's parent.
     */
    public CollectionResource getParent() {
        return parent;
    }

    /**
     * The WebDAV backend for this resource.
     * 
     * @return the WebDAV backend.
     */
    public ResourceBackend getBackend() {
        return backend;
    }

    /**
     * Returns the path of this resource relative to the root, based on it's
     * name and parent. This value should be formatted according to the URI
     * standard (<a href="http://www.ietf.org/rfc/rfc2068.txt">RFC-2068</a>).
     * 
     * @return The path of the resource.
     * @see #getName()
     * @see #getParent()
     */
    public StringBuffer getUriPath( WebdavClient client ) {
        // Escape any '/' characters in the name.
        // String name = getName().replaceAll(Resource.PATH_SEPARATOR, "\\\\/");
        String name = getName();
        if ( client != null ) {
            if ( !client.isFileNameSafe( name ) )
                name = getSafeName();
            name = client.encodeFileName( name, WebdavClient.ISO_8859_1_ENCODING );
        }

        Resource myParent = getParent();
        if ( myParent != null )
            return myParent.getUriPath( client ).append( name );

        return new StringBuffer( name );
    }
}