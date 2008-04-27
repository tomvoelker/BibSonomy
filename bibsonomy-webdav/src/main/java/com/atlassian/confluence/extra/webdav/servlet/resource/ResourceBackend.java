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

import javax.servlet.ServletContext;

import com.atlassian.confluence.extra.webdav.servlet.WebdavRequest;
import com.atlassian.confluence.extra.webdav.servlet.WebdavResponse;

/**
 * Implementations of this interface provide the connection from the
 * WebdavServlet to the actual data store.
 */
public interface ResourceBackend {
    /**
     * Authenticate the user based on the supplied username and password.
     * <p>
     * Authentication requests are triggered by code throwing a
     * {@link InsufficientAuthorizationException}.
     * 
     * @param username
     *            The username
     * @param password
     *            The password
     * @return <code>true</code> if the username/password was authenticated.
     * @see InsufficientAuthorizationException
     */
    boolean authenticateUser( String username, String password );

    /**
     * Clears any user authentication which may have been set via
     * {@link #authenticateUser(String, String)}.
     * 
     * @return <code>true</code> if the a user was authenticated and is now
     *         cleared.
     */
    boolean clearUserAuthentication();

    /**
     * Checks if a user has been authenticated.
     * 
     * @return <code>true</code> if a user is currently authenticated.
     */
    boolean isUserAuthenticated();

    /**
     * The human-friendly name of the backend.
     * 
     * @return The backend name.
     */
    String getName();

    /**
     * Returns the root resource for the WebDAV backend.
     * 
     * @return the root resource.
     */
    Resource getRootResource();

    /**
     * Checks if the backend is read-only. If so, any calls which would modify
     * the back-end are blocked. This is useful if the backend is potentially
     * writable, but admins want write access turned off.
     * 
     * @return <code>true</code> if the backend is read-only.
     */
    boolean isReadOnly();

    /**
     * Checks if the backend supports either shared or exclusive locking.
     * 
     * @return <code>true</code> if the backend supports locking.
     */
    boolean isLockingSupported();

    /**
     * Called once to inintialise the backend when the servlet is started.
     * 
     * @param servletContext
     *            The servlet context.
     */
    void init( ServletContext servletContext ) throws BackendException;

    /**
     * Called to notify that the servlet is shutting down.
     */
    void destroy();

    /**
     * This method is called each time a request is made to the server. Make as
     * little use of the request as is possible.
     * 
     * @param req
     */
    void initRequest( WebdavRequest req );
    
    /**
     * This method is called when generating an HTML list of the contents of a collection.
     * It is usually only called when WebDAV is accessed via a browser, rather than an
     * actual WebDAV client.
     * 
     * @param collection The collection resource.
     * @param request The WebDAV request.
     * @param response The WebDAV response.
     * @return The XHTML page.
     */
    String getCollectionXHTML( CollectionResource collection, WebdavRequest request, WebdavResponse response );
}
