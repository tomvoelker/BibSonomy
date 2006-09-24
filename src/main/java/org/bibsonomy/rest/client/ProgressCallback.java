package org.bibsonomy.rest.client;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public interface ProgressCallback
{
   void setPercent( int percent );
}

/*
 * $Log$
 * Revision 1.1  2006-09-24 21:26:21  mbork
 * enabled sending the content-lenght, so that clients now can register callback objects which show the download progress.
 *
 */