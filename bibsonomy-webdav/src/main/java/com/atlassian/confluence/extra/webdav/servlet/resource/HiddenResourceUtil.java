package com.atlassian.confluence.extra.webdav.servlet.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.atlassian.confluence.extra.webdav.servlet.WebdavServlet;

/**
 * Some clients (particularly the Mac) expect file/folder structures to be a
 * certain way when creating or deleting. As such we need to occasionally hide
 * some resources (particularly virtual ones) temporarily to make the client
 * happy.
 */
public final class HiddenResourceUtil {
    private static final Logger LOG = Logger.getLogger( HiddenResourceUtil.class );

    private static final HiddenResourceUtil INSTANCE = new HiddenResourceUtil();

    private Collection<String> hiddenResources;

    private HiddenResourceUtil() {
        this.hiddenResources = new ArrayList<String>();
    }

    public static HiddenResourceUtil getInstance() {
        return INSTANCE;
    }

    /**
     * Hides the resource until it or one of its ancestors is revealed using the
     * {@link #revealResource(Resource)} method.
     * 
     * @param resource
     *            The resource to hide.
     */
    public void hideResource( Resource resource ) {
        String path = resource.getUriPath( WebdavServlet.getCurrentClient() ).toString();
        LOG.debug( "hiding resource: " + path );
        hiddenResources.add( path );
    }

    /**
     * Reveals the resource and any currently-hidden descendents. Even if this
     * specific resource was not hidden, any descendents will be revealed.
     * 
     * @param resource
     *            The resource to reveal.
     */
    public void revealResource( Resource resource ) {
        String path = resource.getUriPath( null ).toString();
        LOG.debug( "revealing resource: " + path );
        Iterator<String> i = hiddenResources.iterator();
        while ( i.hasNext() ) {
            String deleted = i.next();
            if ( deleted.startsWith( path ) )
                i.remove();
        }
    }

    public boolean isHidden( Resource resource ) {
        String path = resource.getUriPath( null ).toString();
        Iterator<String> iterator = hiddenResources.iterator();

        while ( iterator.hasNext() ) {
            String string = iterator.next();
            if ( path.startsWith( string ) ) {
                LOG.debug( "is hidden: " + path );
                return true;
            }
        }

        return false;
    }

    public String toString() {
        String string = "[" + this.getClass().getName() + "] - \n";
        Iterator<String> iterator = hiddenResources.iterator();

        while ( iterator.hasNext() ) {
            string += iterator.next() + "\n";
        }

        return string;
    }
}