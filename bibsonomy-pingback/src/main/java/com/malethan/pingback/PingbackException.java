//    Copyright (c) 2009 Elwyn Malethan
//
//    This file is part of java-pingback.
//
//    java-pingback is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    java-pingback is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.

//    You should have received a copy of the GNU General Public License
//    along with java-pingback.  If not, see <http://www.gnu.org/licenses/>.

package com.malethan.pingback;

public class PingbackException extends RuntimeException {
    //----------------------------------------------------------------------- Static Properties and Constants

    private static final long serialVersionUID = 2438222297923707852L;

    //----------------------------------------------------------------------- Static Methods
    //----------------------------------------------------------------------- Instance Properties

    private String xmlrpcServer;
    private String targetUrl;
    private int faultCode;

    //----------------------------------------------------------------------- Constructors

    public PingbackException(String message, int faultCode, String xmlrpcServer, String targetUrl) {
        super(message);
        this.xmlrpcServer = xmlrpcServer;
        this.targetUrl = targetUrl;
        this.faultCode = faultCode;
    }

    public PingbackException(String message, Throwable cause, String xmlrpcServer, String targetUrl) {
        super(message, cause);
        this.xmlrpcServer = xmlrpcServer;
        this.targetUrl = targetUrl;
        this.faultCode = -1;
    }

    //----------------------------------------------------------------------- Getters and Setters

    public String getXmlrpcServer() {
        return xmlrpcServer;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public int getFaultCode() {
        return faultCode;
    }

    //----------------------------------------------------------------------- Instance Methods
}
