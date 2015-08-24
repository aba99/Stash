/*
 * EndpointResolver.java
 *
 * Copyright: Copyright (c) 2010
 *
 * Company: Orsyp Logiciels Inc.
 */

package com.orsyp.owls.impl;

import com.orsyp.api.Context;
import com.orsyp.std.ConnectionFactory.Service;

/**
 * Defines a connection endpoint resolver.
 *
 * @author jjt
 * @version $Revision: 1.1 $
 */
public interface EndpointResolver {

    /**
     * Returns the host name corresponding to the specified parameters.
     *
     * @param context              the Dollar Universe context.
     * @param service              the requested service.
     *
     * @return the hostname.
     */
    String getHost(Context context, Service service);

    /**
     * Returns the port number corresponding to the specified parameters.
     *
     * @param context              the Dollar Universe context.
     * @param service              the requested service.
     *
     * @return  the port number.
     */
    int getPort(Context context, Service service);

}
