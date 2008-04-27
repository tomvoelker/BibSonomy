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

import java.util.List;
import java.io.InputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA. User: david Date: Feb 18, 2006 Time: 12:15:02 PM To
 * change this template use File | Settings | File Templates.
 */
public interface CollectionResource extends Resource {
    /**
     * Returns the list of {@link Resource}s who are this collections's
     * children. May be <code>null</code> if no children are present. <p/>
     * <p>
     * <b>Note:</b> This may be an expensive method, since extra resources may
     * have to be retrieved each time. Call it sparingly.
     * 
     * @return The list of resources.
     */
    List<Resource> getChildren();

    /**
     * Returns the direct descendent with the specified name, or
     * <code>null</code> if no such child exists.
     * 
     * @param name
     *            The name of the child.
     * @return the child Resource, or <code>null</code>.
     */
    Resource getChild( String name );

    /**
     * Saves the data to the specified child. If specified child does not exist,
     * a new one is attempted to be created and <code>true</code> is returned.
     * If the existing child is a {@link CollectionResource}, this method
     * should fail.
     * 
     * @param childName
     * @param input
     * @param contentLength
     * @param contentType
     * @param encoding
     * @return <code>true</code> if a new resource was created, or
     *         <code>false</code> if an existing resource was updated.
     * @throws IOException
     */
    boolean saveChildData( String childName, InputStream input, int contentLength, String contentType,
            String encoding ) throws IOException;

    /**
     * Attempts to create a child collection with the specified name.
     * 
     * @param childName
     *            The child collection's desired name.
     * @throws InsufficientAuthorizationException
     *             if the current user does not have permission to create the
     *             child.
     */
    void createChildCollection( String childName );
}
