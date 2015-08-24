/*
 * SessionFilter.java
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

import com.orsyp.api.Filter;
import com.orsyp.api.ObjectFilter;
import com.orsyp.util.Text;
import com.orsyp.util.Wildcard;

/**
 * This class implements the filter for the list of sessions.
 * The filtering is done on the name of the session.
 * No filtering on the version.
 *
 * @author jjt
 * @author fda
 * @version 2.0
 * @see com.orsyp.api.session.SessionItem
 * @see com.orsyp.api.session.SessionList
 */
public class SessionFilter implements Filter<SessionItem>, ObjectFilter<Session> {

    // don't change the value of this field, unless you are knowingly making
    // changes to the class which will render it incompatible with old
    // serialized objects.
    private static final long serialVersionUID = 1L;


    /**
     * The internal identifier (short name) filter.
     *
     * @serial
     */
    protected String id;

    /**
     * The extended name filter.
     *
     * @serial
     */
    protected String name;

    /**
     * Since OWLS.
     * 
     * The version of the session.
     * <p>
     * Syntax: 3 digits (Lengths.LG_VERSION)
     *
     * @serial
     */
    protected String version;
    
    /**
     * Since OWLS.
     * 
     * The label of the session.
     * Syntax: 40 characters (Lengths.LG_SESSION_LABEL)
     *
     * @serial
     */
    protected String label;
    
    /**
     * Since OWLS.
     *      
     * The name of the head uproc.
     *
     * @serial
     */
    protected String header;
    
    
    /**
     * Constructs a default <code>SessionFilter</code>.
     * The filter is set to "*".
     */
    public SessionFilter() {

        super();
        setId("*");
        setName("*");
        setVersion("*");
        setLabel("*");
        setHeader("*");
    }

    /**
     * Constructs a <code>MuFilter</code> with the specified parameters
     *
     * @param id                  the filter on the internal session name.
     * @param name                the filter on the external session name.
     */
    public SessionFilter(String id, String name) {

        this.id = id;
        this.name = name;
    }
    /**
     * Since OWLS
     * 
     * Constructs a <code>MuFilter</code> with the specified parameters
     *
     * @param name                the filter on the external session name.
     * @param version                  the filter on the session version.
     * @param label                the filter on the session label.
     * @param header                  the filter on the uproc header name.
     */
    public SessionFilter(String name, String version, String label, String header) {

        this.name = name;
        this.version = version;
        this.label = label;
        this.header = header;
    }
    
    /**
     * Compares a <code>SessionItem</code> with this filter.
     *
     * @param   item              the session item to be checked.
     *
     * @return  <code>true</code> if the session item verifies the filter,
     *          <code>false</code> otherwise
     *
     * @see com.orsyp.util.Wildcard#compare(java.lang.String, java.lang.String)
     */
    @SuppressWarnings("deprecation")
	public boolean compare(SessionItem item) {

        if (id != null
                && Wildcard.compare(id, Text.trimRight(item.getId())) == false) {

            return false;
        }
        
        if (name != null
                && Wildcard.compare(name, Text.trimRight(item.getName())) == false) {
            return false;
        }

        return true;
    }

    /**
     * Compares a <code>Session</code> with this filter.
     *
     * @param   session           the session to be checked.
     *
     * @return  <code>true</code> if the session verifies the filter,
     *          <code>false</code> otherwise.
     */
    @SuppressWarnings("deprecation")
	public boolean compare(Session session) {

        if (id != null &&
            Wildcard.compare(id, Text.trimRight(session.getId())) == false) {
            return false;
        }
        if (name != null &&
            Wildcard.compare(name, Text.trimRight(session.getName())) == false) {
            return false;
        }
        if (version != null &&
                Wildcard.compare(version, Text.trimRight(session.getVersion())) == false) {
                return false;
            }
        if (label != null &&
                Wildcard.compare(label, Text.trimRight(session.getLabel())) == false) {
                return false;
            }
        if (header != null &&
                Wildcard.compare(header, Text.trimRight(session.getHeader())) == false) {
                return false;
            }
        return true;
    }

    /**
     * Returns the internal identifier filter.
     *
     * @return the internal id
     */
    public String getId() {
        return id;
    }

    /**
     * Specifies the internal identifier filter.
     *
     * @param id the id to specify.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the extended name filter.
     *
     * @return the extended name
     */
    public String getName() {
        return name;
    }

    /**
     * Specifies the extended name filter.
     *
     * @param name the name to specify.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the header
     */
    public String getHeader() {
        return this.header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(String header) {
        this.header = header;
    }
    
    

}
