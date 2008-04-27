package com.atlassian.confluence.extra.webdav.servlet;

public interface WebdavClient {
    /**
     * The default encoding character set ("ISO-8859-1").
     */
    String ISO_8859_1_ENCODING = "ISO-8859-1";

    String UTF_8_ENCODING = "UTF-8";

    /**
     * Checks if the provided, unencoded file name is safe to send to the
     * client. The name should not include any extra path information, such as
     * parent directories, etc. This method does not indicate if the filename
     * needs to be encoded - all filenames should be encoded regardless. What it
     * does test is if the filename can be sent to the client safely, even after
     * being encoded. Some clients to not support having certain characters in
     * their filenames.
     * 
     * @param name
     *            the file name
     * @return <code>true</code> if the name is safe.
     */
    boolean isFileNameSafe( String name );

    /**
     * Encodes the file name (not including any path information) appropriately
     * for the client.
     * 
     * @param name
     *            the file name to encode.
     * @param encoding
     *            the encoding to use (eg. "UTF-8"). Defaults to 'ISO-8859-1' if
     *            <code>null</code> is supplied.
     * @return the encoded file name.
     */
    String encodeFileName( String name, String encoding );

    /**
     * If the client requires that the collection is empty before it will
     * delete, return <code>true</code> here. Typically, clients which require
     * this will send a 'PROPFIND' after deleting sub-elements to confirm the
     * files were deleted.
     * 
     * @return <code>true</code> if the collection must be empty before
     *         deleting.
     */
    boolean requiresEmptyCollectionForDelete();

    /**
     * If the client requires that a new collection is initially empty after
     * being created return <code>true</code>. Typically, clients which
     * require this will send a 'PROPFIND' after creating the collection.
     * 
     * @return
     */
    boolean requiresEmptyCollectionAfterCreate();

    /**
     * If the client moves the old file before saving a new copy, return
     * <code>true</code>.
     * 
     * @return
     */
    boolean requiresMoveBeforeSaving();

    /**
     * Sets a 'content-disposition' header into the request, appropriate for the
     * current client Some clients do not support the official standard for the
     * 'content-disposition' header.
     * 
     * @param resp
     *            The response object.
     * @param filename
     *            The filename to specify. Should not contain any path
     *            information.
     * @param contentEncoding
     *            The content encoding to use.
     */
    void setContentDisposition( WebdavResponse resp, String filename, String contentEncoding );
}
