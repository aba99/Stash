/*
 * ProtoConnectionFactory.java
 *
 * Copyright: Copyright (c) 2010
 *
 * Company: Orsyp Logiciels Inc.
 */

package com.orsyp.owls.impl;

import java.io.UnsupportedEncodingException;
import java.nio.charset.CharacterCodingException;
import java.util.Hashtable;
import java.util.Map;

import com.orsyp.Area;
import com.orsyp.Environment;
import com.orsyp.SyntaxException;
import com.orsyp.UniverseException;
/*import com.orsyp.afl.SSLNioTestConnection2;
import com.orsyp.afl.SslNioTestConnection;*/
import com.orsyp.api.Context;
import com.orsyp.central.products.EndPointCoordinates;
import com.orsyp.comm.Connection;
import com.orsyp.comm.ConnectionPolicy;
import com.orsyp.log.Log;
import com.orsyp.log.LogFactory;
import com.orsyp.std.ConnectionException;
import com.orsyp.std.ConnectionFactory;

/**
 * A connection factory for tests.
 * Connection's endpoint is hardcoded.
 * Connections made are authenticated (local.key).
 *
 * @author jjt
 * @version $Revision: 1.14 $
 */
public final class ProtoConnectionFactory implements ConnectionFactory {

    private static Log log = LogFactory.getLog(ProtoConnectionFactory.class);

    private static ProtoConnectionFactory instance = new ProtoConnectionFactory();

    private static Map<Environment, LocalKey> localKeys;

    static {
        localKeys = new Hashtable<Environment, LocalKey>();
        try {
            localKeys.put(
                    new Environment(AbstractTest.DUAS_COMPANY, AbstractTest.DUAS_NODE, Area.Application),
                    new LocalKey(AbstractTest.LOCAL_KEY_A_FULL_PATH,
                            AbstractTest.DUAS_HOST));
            
            localKeys.put(
                    new Environment(AbstractTest.DUAS_COMPANY, AbstractTest.DUAS_NODE, Area.Integration),
                    new LocalKey(AbstractTest.LOCAL_KEY_I_FULL_PATH,
                            AbstractTest.DUAS_HOST));
            
            localKeys.put(
                    new Environment(AbstractTest.DUAS_COMPANY, AbstractTest.DUAS_NODE, Area.Simulation),
                    new LocalKey(AbstractTest.LOCAL_KEY_S_FULL_PATH,
                            AbstractTest.DUAS_HOST));
            
            localKeys.put(
                    new Environment(AbstractTest.DUAS_COMPANY, AbstractTest.DUAS_NODE, Area.Exploitation),
                    new LocalKey(AbstractTest.LOCAL_KEY_X_FULL_PATH,
                            AbstractTest.DUAS_HOST));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace(System.out);
        } catch (SyntaxException e) {
            e.printStackTrace(System.out);
        }
    }

    private Map<Environment, Authenticator<LocalKeyParam>> authenticators =
            new Hashtable<Environment, Authenticator<LocalKeyParam>>();



    private String host = AbstractTest.DUAS_HOSTNAME;
    private int port = getPort(AbstractTest.AREA);
    
    private String service = "SIO";
    private String client = "admin";
    private String hostname = "frwpmdev18";
    private LocalKeyParam param = new LocalKeyParam(service, client, hostname);

    private int getPort(Area area) {
        int returnInt = -1;

        switch (area.getCode()) {
        case 'A':
            returnInt = AbstractTest.DUAS_IO_PORT_A;
            break;
        case 'I':
            returnInt = AbstractTest.DUAS_IO_PORT_I;
            break;
        case 'S':
            returnInt = AbstractTest.DUAS_IO_PORT_S;
            break;
        case 'X':
            returnInt = AbstractTest.DUAS_IO_PORT_X;
            break;
        }

        return returnInt;
    }
    
    public static ProtoConnectionFactory getInstance() {

        return instance;
    }


    private ProtoConnectionFactory() {

        super();
    }


  public Connection getConnection(Context context, Service service)
	    throws ConnectionException {
	this.port = getPort(context.getEnvironment().getArea());

	Connection conn = null;
	if (System.getProperty("test.nio.ssl") != null) {
	   //Connection initialConn = new SslNioTestConnection(this.host,
		  //  this.port);
	    //conn = initialConn;
//	    conn = new SslNioConnDecorator((SslNioTestConnection) initialConn);
	} else if (System.getProperty("test2.nio.ssl") != null) {
	   // conn = new SSLNioTestConnection2(this.host, this.port);
	} else {
	    conn = new OwlsIOConnection(this.host, this.port, context);
	}
	// set the ssl flag, to know if the agent is ssl or not
	if (null != context.getUnijobCentral()) {
	    conn.setSslEnabled(context.getUnijobCentral().isSslEnabled());
	}

	log.debug("Starting authentication......");
	// authenticate connection
	Authenticator<LocalKeyParam> authenticator = getLocalKeyAuthenticator(context);
	if ((authenticator == null)
		|| (!authenticator.authenticate(conn, this.param))) {
	    // cannot authenticate connection
	    try {
		conn.close();
	    } catch (UniverseException ignored) {
		if ((log != null) && (log.isErrorEnabled())) {
		    log.error("error closing connection", ignored);
		}
	    }
	    conn = null;
	}
	return conn;
    }


    public <T extends Connection> T getConnection(Class<T> clazz,
            Context context,  Service service) throws ConnectionException {

        throw new RuntimeException("not implemented");
    }

    public Connection getConnection(Context context, Service service,
            EndPointCoordinates coordinates) throws ConnectionException {

        throw new RuntimeException("not implemented");
    }


    private Authenticator<LocalKeyParam> getLocalKeyAuthenticator(Context context) {

        Authenticator<LocalKeyParam> auth =
                this.authenticators.get(context.getEnvironment());
        if (auth == null) {
            synchronized (this) {
                try {
                    LocalKey local = localKeys.get(context.getEnvironment());
                    if (local != null) {
                        auth = new LocalKeyAuthenticator(local.read());
                        this.authenticators.put(context.getEnvironment(), auth);
                    }
                } catch (CharacterCodingException e) {
                    e.printStackTrace(System.out);
                }
            }
        }
        return auth;
    }

    /** The connection policy defining connections properties. */
    private ConnectionPolicy policy;

    public ConnectionPolicy getPolicy () {
        return this.policy;
    }

    public void setPolicy (ConnectionPolicy policy) {
        this.policy = policy;
    }

}
