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

import java.util.Date;

import com.atlassian.confluence.extra.webdav.servlet.WebdavClient;

/**
 * Created by IntelliJ IDEA. User: david Date: Feb 18, 2006 Time: 12:13:46 PM To
 * change this template use File | Settings | File Templates.
 */
public interface Resource {
    /**
     * This is the character ("/") which should be used to separate resources in
     * the value returned from {@link #getPath()}.
     */
    char PATH_SEPARATOR_CHAR = '/';

    /**
     * This is the character ("/") which should be used to separate resources in
     * the value returned from {@link #getPath()}.
     */
    String PATH_SEPARATOR = String.valueOf( PATH_SEPARATOR_CHAR );

    /**
     * Returns the backend this resource is based on.
     * 
     * @return the WebDAV backend.
     */
    ResourceBackend getBackend();

    /**
     * The parent of this resource. May be <code>null</code> if the resource
     * is the root of the WebDAV share.
     * 
     * @return the resource's parent.
     */
    CollectionResource getParent();

    /**
     * The name of this resource.
     * 
     * @return the resource name.
     */
    String getName();

    /**
     * Provides a name for the resource that is suitable for presentation to a
     * user.
     * 
     * @return the display name.
     */
    String getDisplayName();

    /**
     * Provides an alternate name for this resource which can be included in a
     * URL if the value from {#getName()} is incompatible.
     * 
     * @return the encoded name for this resource.
     */
    String getSafeName();

    /**
     * The date the resource was created.
     * 
     * @return the resource's creation date.
     */
    Date getCreationDate();

    /**
     * Returns the date the resource was last modified. May be <code>null</code>
     * if not relevant.
     * 
     * @return the date.
     */
    Date getLastModified();

    /**
     * The language the resource is in.
     * 
     * @return the content language.
     */
    String getContentLanguage();

    /**
     * If <code>true</code> this resource should be ignored when performing
     * infinite depth modifications (eg move, copy, etc) because it doesn't
     * really exist.
     * 
     * @return <code>true</code> if the resource is virtual.
     */
    boolean isVirtual();

    /**
     * <p>
     * Returns the path of the resource, relative to the root resource. This
     * does <b>not</b> include any of the URL leading up to the root resource.
     * Resource elements should be separated with the {@link #PATH_SEPARATOR}
     * character.
     * 
     * <p>
     * Also, {@link CollectionResource}s should ensure the last character of
     * their path is a {@link #PATH_SEPARATOR}. Eg: '/foo/bar/'}
     * <p>
     * Finally, the path should be correctly URL-encoded, with any non-standard
     * characters being escaped.
     * 
     * @param client The Webdav client to produce the path for.
     * @return the path of the resource.
     */
    StringBuffer getUriPath( WebdavClient client );

    /**
     * This method is called to delete this resource. If it is a
     * {@link CollectionResource}, it should also attempt to delete any
     * children it may contain.
     * 
     * <p>
     * If this resource cannot be deleted, an error should be added to the error
     * report and <code>false</code> returned.
     * <p>
     * If this resource is a collection, and any of its children cannot be
     * deleted, this resource should not be deleted and <code>false</code>
     * should be returned. No extra error message should be added by the parent
     * resource. It is up to the implementation whether the delete operation is
     * continued on the other children if one fails.
     * 
     * @param errs
     *            The list of errors
     * @return <code>true</code> if the resource and all of its children (if
     *         present) were deleted.
     */
    boolean delete( ErrorReport errs );

    /**
     * Attempts to copy this resource to the specified destination. The copy may
     * fail for a variety of reasons, including but not restricted to: invalid
     * destination, insufficient permissions at either the source or the
     * destination, etc.
     * 
     * @param parent
     *            The parent resource to copy into.
     * @param childName
     *            The name of the new child location.
     * @param overwrite
     *            If <code>true</code> overwrite any existing resource with
     *            the same name.
     * @param deepCopy
     *            If <code>true</code> copy any children this resource has
     *            too.
     * @param errs
     *            Any errors are recorded here, and <code>false</code> must be
     *            returned to display them.
     * @return <code>false</code> if the resource or some of it's children
     *         could not be copied.
     */
    boolean copyTo( CollectionResource parent, String childName, boolean overwrite, boolean deepCopy,
            ErrorReport errs );

    /**
     * Attempts to move this resource to the specified destination. The move may
     * fail for a variety of reasons, including but not restricted to: invalid
     * destination, insufficient permissions at either the sourc or the
     * destintion, the destination already existing and <code>overwrite</code>
     * being false, etc.
     * 
     * @param parent
     *            The parent collection the resource is being moved into.
     * @param childName
     *            The new name of the child.
     * @param overwrite
     *            If <code>true</code> overwrite any existing resource with
     *            the target name, if it exists.
     * @param errs
     *            The collection of errors which occurred while moving.
     * @return <code>true</code> if this resource and all it's children were
     *         moved successfully.
     */
    boolean moveTo( CollectionResource parent, String childName, boolean overwrite, ErrorReport errs );
}
