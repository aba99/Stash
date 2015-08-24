/*
 * AbstractTest.java
 *
 * Copyright: Copyright (c) 2010
 *
 * Company: Orsyp Logiciels Inc.
 */

package com.orsyp.owls.impl;

import static java.lang.System.out;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.orsyp.Area;
import com.orsyp.Environment;
import com.orsyp.Identity;
import com.orsyp.SyntaxException;
import com.orsyp.UniverseException;
import com.orsyp.api.Client;
import com.orsyp.api.Context;
import com.orsyp.api.Product;
import com.orsyp.api.central.UniCentral;
import com.orsyp.std.ClientConnectionManager;
import com.orsyp.std.ConnectionFactory;
import com.orsyp.std.NodeConnectionFactory;
import com.orsyp.std.StdImplFactory;
import com.orsyp.std.central.UniCentralStdImpl;
import com.orsyp.std.security.SecurityManager;
import com.orsyp.std.security.StdSecurityManager;
import com.orsyp.util.ApplicationVariables;

/**
 * Base class for OWLS test classes.
 * Inheriting classes will use <code>ProtoConnectionFactory</code> by default
 * (local.key authenticated connections).
 *
 * @author jjt
 * @version $Revision: 1.15 $
 */
public abstract class AbstractTest {

    /* true if authentication to UVMS  */
    public static final boolean IS_AUTHENTICATE_UVMS = true;
    
    /* GENERAL PARAMETERS */
    public static final String 	DUAS_NODE     = "calpmdev13";
//    public static final String 	DUAS_NODE     = "casdco5201_vch_6g";
    public static final String 	DUAS_COMPANY  = "DEV660";
//    public static final String 	DUAS_COMPANY  = "UAU601";
    public static final String 	DUAS_HOST     = "calpmdev13";
//  public static final String 	DUAS_HOST     = "casdco5201";
    public static final Area 	AREA 	      = Area.Exploitation;
    private static final String GROUP 	      = "ORSYP";
    private static final String SYSTEM 	      = "W32"; //"W32";
    
    /* SPECIFIC UVMS AUTHENTICATION */
    public static final String  UVMS_HOST     = "CALPMPS04.orsypgroup.com";
    public static final int 	UVMS_PORT     = 4184;
    public static final String  UVMS_LOGIN    = "admin";
    public static final String  UVMS_PWD      = "admin";
//    public static final String  UVMS_HOST     = "FRWPMDEV19";
//    public static final int 	UVMS_PORT     = 5184;
//    public static final String  UVMS_LOGIN    = "admin";
//    public static final String  UVMS_PWD      = "admin";
    /* SPECIFIC LOCAL KEY AUTHENTICATION */
    public static final String  DUAS_HOSTNAME = "calpmdev13";
    public static final int    DUAS_IO_PORT_A = 15603;
    public static final int    DUAS_IO_PORT_I = 15602;
    public static final int    DUAS_IO_PORT_S = 15601;
    public static final int    DUAS_IO_PORT_X = 15600;
    public static final String DUAS_DATA_DIR_FULL_PATH =
      "D:\\ORSYP\\DUAS\\DEV660_calpmdev13\\data"; 

    /* Area A*/    
    public static final String LOCAL_KEY_A_FULL_PATH = DUAS_DATA_DIR_FULL_PATH + "\\app\\local.key";
    /* Area I*/    
    public static final String LOCAL_KEY_I_FULL_PATH = DUAS_DATA_DIR_FULL_PATH + "\\int\\local.key";  
    /* Area S*/    
    public static final String LOCAL_KEY_S_FULL_PATH = DUAS_DATA_DIR_FULL_PATH + "\\sim\\local.key";
    /* Area X*/    
    public static final String LOCAL_KEY_X_FULL_PATH = DUAS_DATA_DIR_FULL_PATH + "\\exp\\local.key";
        
    
    public static StdImplFactory implFactory;
    public static SecurityManager securityManager;
    public static UniCentral central;
    
    protected static void init() {
	if (IS_AUTHENTICATE_UVMS) {
	    initUVMS();
	} else {
	    initSkipUVMS();
	}
    }

    protected static void initSkipUVMS() {

        ClientConnectionManager.setDefaultFactory(
            ProtoConnectionFactory.getInstance());
    }
    
    protected static void initUVMS() {

	central = new UniCentral(UVMS_HOST, UVMS_PORT);
	central.setImplementation(new UniCentralStdImpl(central));
	central.setSslEnabled(ApplicationVariables.SSL_SECURITY.equals(System
		.getProperty(ApplicationVariables.SECURITY_TYPE)));

	// login
	try {
	    out.println("login to central ...");
	    central.login(UVMS_LOGIN, UVMS_PWD);
	    out.println(" -> authentified.");
	} catch (UniverseException e) {
	    out.println("login failed.");
	    e.printStackTrace();
	    return;
	}

	ConnectionFactory factory = NodeConnectionFactory.getInstance(central);
	ClientConnectionManager.setDefaultFactory(factory);

	securityManager = new StdSecurityManager();
	implFactory = new StdImplFactory(factory, securityManager);
    }

    protected static void cleanup() {

        ClientConnectionManager.cleanup();
    }

    protected Context makeContext() throws SyntaxException {

	Context context = null;

	Client client = new Client(makeIdentity());
	context = new Context(makeEnvironment(), client, central);
	context.setProduct(Product.OWLS);

	return context;
    }

    private static Environment makeEnvironment() throws SyntaxException {
	return new Environment(DUAS_COMPANY, DUAS_NODE, AREA);
    }


    private static Identity makeIdentity() {
	return new Identity(UVMS_LOGIN, GROUP, DUAS_HOST, SYSTEM);
    }


    protected static void printf(String format, Object ... args) {

        System.out.printf(format, args);
    }

    protected static void println() {

        System.out.println();
    }

    // TOOLS
    static final private Pattern p = Pattern.compile("^[^\\d]*(\\d{4}+)[^\\d]*(\\d{2}+)[^\\d]*(\\d{2}+)[^\\d]*(\\d{2}+)[^\\d]*(\\d{2}+)[^\\d]*(\\d{2}+).*$");
    static final protected String makeDate(String date,String pattern) {
        String  d = date==null ? "" : date.toString().replaceFirst("^0*$", "");
        Matcher m = p.matcher(d);
        return !m.matches() ? date : pattern
            .replace("YYYY", m.group(1))
            .replace("MM", m.group(2))
            .replace("DD", m.group(3))
            .replace("HH", m.group(4))
            .replace("MI", m.group(5))
            .replace("SS", m.group(6));
    }
    
}
