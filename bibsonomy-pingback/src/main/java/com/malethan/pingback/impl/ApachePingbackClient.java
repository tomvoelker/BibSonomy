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

package com.malethan.pingback.impl;

import com.malethan.pingback.Link;
import com.malethan.pingback.PingbackClient;
import com.malethan.pingback.PingbackException;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.MalformedURLException;
import java.net.URL;

public class ApachePingbackClient implements PingbackClient {

    public String sendPingback(String articleUrl, Link link) {
        try {

            XmlRpcClient client = configureXmlRcpClient(link);
            Object[] params = new Object[]{articleUrl, link.getUrl()};
            return (String) client.execute("pingback.ping", params);

        } catch (MalformedURLException e) {

            throw new PingbackException("It was not possible to send a pingback, one or more URLs was invalid",
                    e, link.getPingbackUrl(), link.getUrl());

        } catch (XmlRpcException e) {

            throw new PingbackException("Error: " + e.code + ", " + e.getMessage(),
                    e.code, link.getPingbackUrl(), link.getUrl());

        }
    }

    private XmlRpcClient configureXmlRcpClient(Link link) throws MalformedURLException {
        XmlRpcClientConfigImpl config;
        config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL(link.getPingbackUrl()));
        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);
        return client;
    }

}