/*
 * SessionItem.java
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
import com.orsyp.api.Item;
import com.orsyp.util.Text;

/**
 * This class implements an item of the list of sessions.
 *
 * @author jjt
 * @version $Revision$
 * @see com.orsyp.api.session.SessionList
 */
public class SessionItem extends Item<SessionId> {

    // don't change the value of this field, unless you are knowingly making
    // changes to the class which will render it incompatible with old
    // serialized objects.
    private static final long serialVersionUID = 1L;


    /**
     * The label of the session.
     *
     * @serial
     */
    protected String label;

    /**
     * The name of the head uproc.
     *
     * @serial
     */
    protected String header;


    //--- Constructors -------------------------------------------------------

    /**
     * Instanciates a new <code>SessionItem</code> without doing any
     * syntaxical checks.
     *
     * @param  name               the session extended name.
     * @param  id                 the session short name
     * @param  version            the session version
     * @param  label              the session label
     * @param  header             the name of the head uproc
     *
     * @return  the corresponding session item
     */
    public static SessionItem create(String name, String id, String version,
                                     String label, String header) {

        SessionItem item = new SessionItem();
        item.setIdentifier(SessionId.create(name, id, version));
        item.init(label, header);
        return item;
     }

    /**
     * Empty constructor used internally only.
     */
    private SessionItem() {}


    /**
     * Constructs a <code>SessionItem</code> with the specified parameters.
     *
     * @param  name               the session name
     * @param  version            the session version
     * @param  label              the session label
     * @param  header             the name of the head uproc
     *
     * @throws SyntaxException  if a parameter is invalid
     */
    public SessionItem(String name, String version, String label,
                       String header) throws SyntaxException {

        setIdentifier(new SessionId(name, version));
        /**@todo vérifier la syntaxe/longueur ? */
        init(label, header);
    }

    /**
     * Since OWLS
     * 
     * Constructs a <code>SessionItem</code> with the specified parameters.
     *
     * @param  name               the session name
     * @param  version            the session version
     *
     * @throws SyntaxException  if a parameter is invalid
     */
    public SessionItem(String name, String version) throws SyntaxException {

        setIdentifier(new SessionId(name, version));
    }
    
    //--- Accessors ----------------------------------------------------------

    /**
     * Returns the extended name of the session.
     *
     * @return  the session extended name.
     */
    public String getName() {

        if (this.identifier == null) {
            return null;
        }
        return this.identifier.getName();
    }

    /**
     * Specifies the extended name of the session.
     *
     * @param name                the extended name.
     *
     * @throws SyntaxException  if <code>name</code> is invalid.
     */
    public void setName(String name) throws SyntaxException {

        if (this.identifier == null) {
            this.identifier = new SessionId(name);
        } else {
            this.identifier.setName(name);
        }
    }

    /**
     * Returns the short name of the session.
     *
     * @return  the session short name.
     */
    public String getId() {

        if (this.identifier == null) {
            return null;
        }
        return this.identifier.getId();
    }

    /**
     * Returns the version of the session.
     *
     * @return  the session version
     */
    public String getVersion() {

        if (this.identifier == null) {
            return null;
        }
        return this.identifier.getVersion();
    }

    /**
     * Returns the label of the session.
     *
     * @return  the session label
     */
    public String getLabel() {

        return this.label;
    }

    /**
     * Returns the name of the head uproc.
     *
     * @return  the uproc name
     */
    public String getHeader() {

        return this.header;
    }

    /**
     * Initializes some instance members.
     *
     * @param  label              the session label
     * @param  header             the name of the head uproc
     */
    protected void init(String label, String header) {

        this.label = Text.trimRight(label);
        this.header = Text.trimRight(header);
    }
}
