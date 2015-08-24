/*
 * Authenticator.java
 *
 * Copyright: Copyright (c) 2010
 *
 * Company: Orsyp Logiciels Inc.
 */

package com.orsyp.owls.impl;

import com.orsyp.comm.Connection;

/**
 * Authenticates a connection.
 *
 * @author jjt
 * @version $Revision: 1.1 $
 *
 * @param<T> The type of authentication parameters.
 */
public interface Authenticator<T extends AuthenticationParam> {

    /**
     * AUthenticates the specified connection using the specified parameters.
     *
     * @param conn                the connection to authenticate.
     * @param param               the authentication parameters.
     * @return boolean
     */
    boolean authenticate(Connection conn, T param);

}
