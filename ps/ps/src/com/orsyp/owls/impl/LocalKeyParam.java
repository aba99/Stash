/*
 * LocalKeyParam.java
 *
 * Copyright: Copyright (c) 2010
 *
 * Company: Orsyp Logiciels Inc.
 */

package com.orsyp.owls.impl;

/**
 * Implements parameters for local.key authentication.
 *
 * @author jjt
 * @version $Revision: 1.1 $
 */
public class LocalKeyParam implements AuthenticationParam {

    private String service;
    private String client;
    private String hostname;


    public LocalKeyParam(String service, String client, String hostname) {

        super();
        this.service = service;
        this.client = client;
        this.hostname = hostname;
    }


    public String getService() {

        return this.service;
    }


    public String getClient() {

        return this.client;
    }


    public String getHostname() {

        return this.hostname;
    }

}
