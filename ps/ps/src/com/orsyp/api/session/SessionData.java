/*
 * SessionData.java
 *
 * Title: Dollar Universe API
 *
 * Description:
 *
 * Copyright: Copyright (c) 2006
 *
 * Company: Orsyp Logiciels Inc.
 */

package com.orsyp.api.session;

import java.io.Serializable;

import com.orsyp.util.Text;

/**
 * This class implements the data contained in an element of a session tree.
 * A <code>SessionData</code> contains:
 * <ul>
 * <li>  the name of the uproc,
 * <li>  the execution context of the uproc.
 * </ul>
 * Additionnally, it also handles a reference to the session tree atom that
 * contains this data.
 *
 * @author jjt
 * @version $Revision$
 */
public class SessionData implements Serializable {

    // don't change the value of this field, unless you are knowingly making
    // changes to the class which will render it incompatible with old
    // serialized objects.
    private static final long serialVersionUID = 2L;

    /**
     * The internal name of the uproc.
     * @serial
     */
    protected String uprocId;

    /**
     * The external name of the uproc.
     * @serial
     */
    protected String uprocName;
    
    /** 
     * The order number of the session : identifying the article in the session 
     * <p>Non persistent, is computed when extracting the session
     */
    private int numOrdSession;

    /**
     * The execution context of the uproc.
     * Can be a MU name, MU type name, a HDP expression or empty.
     * @serial
     */
    private ExecutionContext executionContext;

    /**
     * The session atom that contains this data.
     * @serial
     */
    protected SessionAtom atom;

    //--- Constructors -------------------------------------------------------

    /**
     * Constructs an empty <code>SessionData</code>.
     */
    public SessionData() {
        super();
        this.numOrdSession = -1;
    }
    
    /**
     * Constructs a <code>SessionData</code> with the specified uproc names and
     * execution context.
     *
     * @param uprocName           the external name of the uproc.
     * @param uprocId             the internal name of the uproc.
     * @param context             the execution context.
     * @param numOrdSession       the numOrdSession value @see {@link #getNumOrdSession()}
     */
    public SessionData(String uprocName, String uprocId, ExecutionContext context, int numOrdSession) {
        super();
        this.uprocName = uprocName;
        this.uprocId = uprocId;
        this.executionContext = context;
        this.numOrdSession = numOrdSession;
    }

    /**
     * Constructs a <code>SessionData</code> with the specified uproc names and
     * execution context.
     *
     * @param uprocName           the external name of the uproc.
     * @param uprocId             the internal name of the uproc.
     * @param context             the execution context.
     */
    public SessionData(String uprocName, String uprocId, ExecutionContext context) {
        this(uprocName, uprocId, context, -1);
    }

    /**
     * Constructs a <code>SessionData</code> with the specified uproc names.
     *
     * @param uprocName           the external name of the uproc.
     * @param uprocId             the internal name of the uproc.
     */
    public SessionData(String uprocName, String uprocId) {

        this(uprocName, uprocId, new ExecutionContext());
    }

    /**
     * Constructs a <code>SessionData</code> with the specified uproc name and
     * execution context.
     *
     * @param uprocName           the external name of the uproc.
     * @param context             the execution context.
     */
    public SessionData(String uprocName, ExecutionContext context) {

        this(uprocName, null, context);
    }

    /**
     * Constructs a <code>SessionData</code> with the specified uproc name.
     *
     * @param uprocName           the external name of the uproc.
     */
    public SessionData(String uprocName) {

        this(uprocName, null, new ExecutionContext());
    }

    //--- Public methods -----------------------------------------------------

    /**
     * Returns the short name of the uproc.
     *
     * @return  the uproc short name.
     */
    public String getUprocId() {

        return this.uprocId;
    }

    /**
     * Returns the extended name of the uproc.
     *
     * @return  the uproc extended name.
     */
    public String getUprocName() {

        return this.uprocName;
    }

    /**
     * Returns the execution context of the uproc.
     * The context can be same MU, a specific MU, or a HDP.
     *
     * @return the execution context.
     */
    public ExecutionContext getExecutionContext() {

        return this.executionContext;
    }

    /**
     * Returns the session tree atom containing this session data.
     *
     * @return the container atom.
     */
    public SessionAtom getAtom() {

        return this.atom;
    }

    /**
     * Specifies the short name of the uproc.
     *
     * @param uprocId             the uproc short name.
     */
    public void setUprocId(String uprocId) {

        this.uprocId = Text.trimRight(uprocId);
    }

    /**
     * Specifies the extended name of the uproc.
     *
     * @param uprocName           the uproc extended name.
     */
    public void setUprocName(String uprocName) {

        this.uprocName = Text.trimRight(uprocName);
    }

    /**
     * Specifies the execution context of the uproc.
     * The context can be same MU, a specific MU, or a HDP.
     *
     * @param context             the execution context.
     */
    public void setExecutionContext(ExecutionContext context) {

        this.executionContext = context;
    }


    /**
     * Specifies the session tree atom that contains this data.
     *
     * @param atom                the container atom.
     */
    public void setAtom(SessionAtom atom) {
    
        this.atom = atom;
    }

    /** 
     * The order number of the session : identifying the article in the session 
     * <p>Non persistent, is computed when extracting the session
     * 
     * @return an integer value starting with 1 for the root item
     */
    public int getNumOrdSession () {
        return this.numOrdSession;
    }

    /** 
     * The order number of the session : identifying the article in the session 
     * <p>Non persistent, is computed when extracting the session
     * 
     * @return an integer value starting with 0
     */
    public void setNumOrdSession (int numOrdSession) {
        this.numOrdSession = numOrdSession;
    }
}
