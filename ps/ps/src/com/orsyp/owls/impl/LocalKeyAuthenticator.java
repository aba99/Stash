/*
 * LocalKeyAuthenticator.java
 *
 * Copyright: Copyright (c) 2010
 *
 * Company: Orsyp Logiciels Inc.
 */

package com.orsyp.owls.impl;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;

import com.orsyp.UniverseException;
import com.orsyp.comm.Connection;
import com.orsyp.comm.Protocol;
import com.orsyp.comm.Request;
import com.orsyp.comm.UniverseProtocol;
import com.orsyp.comm.kmeleon.AuthenticationResponse;
import com.orsyp.comm.kmeleon.HelloRequest;
import com.orsyp.comm.kmeleon.HelloResponse;
import com.orsyp.comm.kmeleon.InternLocalRequest;
import com.orsyp.comm.kmeleon.KmeleonProtocol;
import com.orsyp.log.Log;
import com.orsyp.log.LogFactory;

/**
 * Authenticates a connection using local key.
 *
 * @author jjt
 * @version $Revision: 1.2 $
 */
public class LocalKeyAuthenticator implements Authenticator<LocalKeyParam> {

    private static final Protocol UNIVERSE_PROTOCOL = new UniverseProtocol();
    private static final Protocol UNIJOB_PROTOCOL = new KmeleonProtocol();

    private static Log log = LogFactory.getLog(LocalKeyAuthenticator.class);


    private String key;


    public LocalKeyAuthenticator(byte[] rawKey) throws CharacterCodingException {

        super();
        CharsetDecoder decoder = LocalKey.getCharsetDecoder();
        CharBuffer buff = decoder.decode(ByteBuffer.wrap(rawKey));
        this.key = buff.toString();
    }


    public boolean authenticate(Connection conn, LocalKeyParam param) {

        try {
            conn.setProtocol(UNIJOB_PROTOCOL);
            makeHelloRequest(param).send(conn);
            HelloResponse hello = new HelloResponse(conn.read());
            if (!hello.isOk()) {
                if ((log != null) && (log.isErrorEnabled())) {
                    log.error("hello ko");
                }
                return false;
            }

            conn.setProtocol(UNIVERSE_PROTOCOL);
            makeAuthenticationRequest(param, this.key).send(conn);
            AuthenticationResponse res = new AuthenticationResponse(conn.read());
            if (!res.isAccessGranted()) {
                if ((log != null) && (log.isErrorEnabled())) {
                    log.error("access denied");
                }
                return false;
            }
        } catch (UniverseException e) {
            if ((log != null) && (log.isErrorEnabled())) {
                log.error("authentication error", e);
            }
            return false;
        }
        return true;
    }


    private static Request makeHelloRequest(LocalKeyParam param) {

        return new HelloRequest(param.getService(),
                                param.getClient(),
                                param.getHostname());
    }


    private static Request makeAuthenticationRequest(LocalKeyParam param,
                                                     String key) {

        return new InternLocalRequest(param.getService(),
                                      param.getClient(),
                                      key);
    }
}
