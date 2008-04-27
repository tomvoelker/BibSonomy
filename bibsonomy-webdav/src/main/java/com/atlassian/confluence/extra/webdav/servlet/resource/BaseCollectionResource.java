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
 * Created by IntelliJ IDEA. User: david Date: Feb 18, 2006 Time: 2:02:15 PM To
 * change this template use File | Settings | File Templates.
 */
public abstract class BaseCollectionResource extends BaseResource implements CollectionResource {
    /**
     * Constructs the new resource.
     * 
     * @param parent
     *            The resource's parent. May be <code>null</code>.
     * @param backend
     *            The WebDAV backend of the resource.
     */
    public BaseCollectionResource( CollectionResource parent, ResourceBackend backend ) {
        super( parent, backend );
    }

    /**
     * Returns the standard path with a {@link Resource#PATH_SEPARATOR}
     * appended.
     * 
     * @return The path of this resource.
     */
    public StringBuffer getUriPath( WebdavClient client ) {
        return super.getUriPath( client ).append( Resource.PATH_SEPARATOR );
    }
}