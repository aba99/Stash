/*
 * SessionId.java
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

import com.orsyp.SyntaxException;
import com.orsyp.api.InvalidSessionException;
import com.orsyp.api.InvalidVersionException;
import com.orsyp.api.NameIdentifier;

/**
 * This class implements the identifier of a session.
 * A session is identified by a name and a version.
 * <ul>
 * <li> In the application, integration and simulation areas, sessions are
 *      managed by version. The version can vary from 001 to 999 (included).
 * <li> In the production area only one version of a session is available.
 *      This version must be 000.
 * </ul>
 * <p>
 * The above rules are not check in this class since the environment is not
 * available.
 *
 * @author fda
 * @author jjt
 * @version $Revision$
 */
public class SessionId extends NameIdentifier {

    // don't change the value of this field, unless you are knowingly making
    // changes to the class which will render it incompatible with old
    // serialized objects.
    private static final long serialVersionUID = 1L;


    /**
     * The version of the session.
     * <p>
     * Syntax: 3 digits (Lengths.LG_VERSION)
     *
     * @serial
     */
    protected String version;


    /**
     * Instanciates a new <code>SessionId</code> without doing any syntaxical
     * checks (only trimed and forced to uppercase).
     *
     * @param   name       the extended name of the session.
     * @param   id         the short name of the session.
     * @param   version    the version of the session
     *
     * @return  the corresponding session identifier
     */
    static public SessionId create(String name, String id, String version) {

        SessionId identifier = new SessionId();
        if (name != null) {
            identifier.name = name.trim();
        }

        if (id != null) {
            identifier.id = id.trim().toUpperCase();
        }
        identifier.version = version;
        return identifier;
    }

    /**
     * Instantiates a new <code>SessionId</code> without doing any syntaxical
     * checks (only trimed and forced to uppercase).
     *
     * @param   name       the extended name of the session.
     * @param   version    the version of the session
     *
     * @return  the corresponding session identifier
     */
    static public SessionId create(String name, String version) {

        return create(name, null, version);
    }

    /**
     * Empty constructor.
     */
    public SessionId() {}

    /**
     * Constructs a <code>SessionId</code> with the specified parameters.
     *
     * @param   name       the extended name of the session
     * @param   version    the version of the session
     *
     * @throws  SyntaxException  if a parameter is invalid
     */
    public SessionId(String name, String version) throws SyntaxException {

        setName(name);
        setVersion(version);
    }

    /**
     * Constructs a <code>SessionId</code> with the specified parameters.
     * The version is set to "000".
     *
     * @param   name       the name of the session
     *
     * @throws  SyntaxException  if a parameter is invalid
     */
    public SessionId(String name) throws SyntaxException {

        this(name, "000");
    }

    /**
     * Returns the version of the session.
     * The returned string can not be null (see setVersion).
     *
     * @return  the version of the session
     */
    public String getVersion() {

        return this.version;
    }

    /**
     * Sets the version of the session.
     * The <code>version</code> parameter must not be null (if null,
     * an exception is thrown).
     *
     * @param   version    the version of the session
     *
     * @throws  InvalidVersionException  if the version is invalid
     */
    public void setVersion(String version) throws InvalidVersionException {

        if (this.getSyntaxRules().checkVersion(version) == false) {
            throw new InvalidVersionException(version);
        }
        this.version = version;
    }

    /**
     * Returns a string representation of this <code>SessionId</code>.
     * The format of the returned string is "[name][version]".
     * <p>
     * The returned string might be empty but cannot be <code>null</code>.
     *
     * @return  a String representation of this <code>SessionId</code>
     */
    public String toString() {

        StringBuffer buff = new StringBuffer();
        buff.append('[');
        if (name != null) {
            buff.append(name);
        }
        buff.append("][");
        if (version != null) {
            buff.append(version);
        }
        buff.append(']');
        return buff.toString();
    }

    /**
     * Checks the syntax of this session identifier.
     * This method checks the syntax of both <code>name</code> and
     * <code>version</code> attribute.
     *
     * @throws SyntaxException  if this session identifier is invalid.
     */
    public void check() throws SyntaxException {

        if (this.getSyntaxRules().checkSessionName(this.name) == false) {
            throw new InvalidSessionException(this.name);
        }
        if (this.getSyntaxRules().checkVersion(this.version) == false) {
            throw new InvalidVersionException(this.version);
        }
    }

    /**
     * Checks the validity of <code>id</code> as a session internal
     * identifier.
     *
     * @param   id              the identifier code to check.
     *
     * @throws  InvalidSessionException  if <code>id</code> is invalid.
     */
    protected void checkId(String id) throws InvalidSessionException {

        if (this.getSyntaxRules().checkSession(id) == false) {
            throw new InvalidSessionException(id);
        }
    }

    /**
     * Checks the validity of <code>name</code> as a session extended name.
     *
     * @param   name              the identifier code to check.
     *
     * @throws  InvalidSessionException  if <code>name</code> is invalid.
     */
    protected void checkName(String name) throws InvalidSessionException {

        if (this.getSyntaxRules().checkSessionName(name) == false) {
            throw new InvalidSessionException(name);
        }
    }
    
    /** (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode () {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode ());
        result = prime * result
                + ((this.name == null) ? 0 : this.name.hashCode ());
        result = prime * result
        	+ ((this.version == null) ? 0 : this.version.hashCode ());
        return result;
    }
    
    /** (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals (Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass () != obj.getClass ()) {
            return false;
        }
        SessionId other = (SessionId) obj;
        if (this.id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!this.id.equals (other.id)) {
            return false;
        }
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals (other.name)) {
            return false;
        }
        if (this.version == null) {
            if (other.version != null) {
                return false;
            }
        } else if (!this.version.equals (other.version)) {
            return false;
        }
        return true;
    }
}
