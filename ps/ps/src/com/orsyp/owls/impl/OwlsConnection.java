/*
 * OwlsConnection.java
 *
 * Copyright: Copyright (c) 2010
 *
 * Company: Orsyp Logiciels Inc.
 */

package com.orsyp.owls.impl;

import com.orsyp.UniverseException;
import com.orsyp.api.Context;
import com.orsyp.comm.Protocol;
import com.orsyp.comm.UniverseProtocol;
import com.orsyp.comm.kmeleon.KmeleonProtocol;
import com.orsyp.std.DUIOConnection;

/**
 * Implements connection to OWLS for testing purposes.
 *
 * @author jjt
 * @version $Revision: 1.1 $
 */
public class OwlsConnection extends DUIOConnection {

    private static final Protocol UNIVERSE_PROTOCOL = new UniverseProtocol();
    private static final Protocol UNIJOB_PROTOCOL = new KmeleonProtocol();


    public OwlsConnection(String host, int port, Context context) {
        super(host, port, context.getEnvironment());
        setProtocol(UNIJOB_PROTOCOL); // authentication using kmeleon protocol
    }


    public void close() throws UniverseException {
        setProtocol(UNIVERSE_PROTOCOL);
        if (this.socket != null) {
            super.close();
        }
    }

}
