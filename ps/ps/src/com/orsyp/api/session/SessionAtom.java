/*
 * SessionAtom.java
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

/**
 * A <code>SessionAtom</code> handles the tree-structure of a session.
 * The tree-structure is described by:
 * <ul>
 * <li>  the root element,
 * <li>  the direct parent element,
 * <li>  the direct child element in normal processing path,
 * <li>  the direct child element  in abnormal processing path,
 * <li>  the direct next sibling element,
 * <li>  the direct previous sibling element.
 * </ul>
 *
 * ***** WORK IN PROGRESS *****
 *
 * @author jjt
 * @version $Revision$
 */
public class SessionAtom implements Serializable {

    // don't change the value of this field, unless you are knowingly making
    // changes to the class which will render it incompatible with old
    // serialized objects.
    private static final long serialVersionUID = 1L;

    protected SessionAtom root;
    protected SessionAtom parent;
    protected SessionAtom childOk;
    protected SessionAtom childKo;
    protected SessionAtom nextSibling;
    protected SessionAtom previousSibling;

    protected SessionData data;

    //--- Constructors -------------------------------------------------------

    /**
     * Constructs a <code>SessionAtom</code> with the specified data.
     *
     * @param data                the data contained in this session atom.
     */
    public SessionAtom(SessionData data) {

        this.data = data;
        if (this.data != null) {
            this.data.atom = this;
        }
    }

    //--- Public methods -----------------------------------------------------

    public SessionAtom getRoot() {
        return this.root;
    }

    public SessionAtom getParent() {
        return this.parent;
    }

    public SessionAtom getChildOk() {
        return this.childOk;
    }

    public SessionAtom getChildKo() {
        return this.childKo;
    }

    public SessionAtom getNextSibling() {
        return this.nextSibling;
    }

    public SessionAtom getPreviousSibling() {
        return this.previousSibling;
    }

    public SessionData getData() {
        return this.data;
    }


    public void setRoot(SessionAtom root) {
        this.root = root;
    }

    public void setParent(SessionAtom parent) {
        this.parent = parent;
    }

    public void setChildOk(SessionAtom child) {

        this.childOk = child;
        if (child != null) {
            child.parent = this;
        }
    }

    public void setChildKo(SessionAtom child) {

        this.childKo = child;
        if (child != null) {
            child.parent = this;
        }
    }

    public void setNextSibling(SessionAtom next) {

        this.nextSibling = next;
        if (next != null) {
            next.previousSibling = this;
            next.parent = this.parent;
        }
    }

    public void setPreviousSibling(SessionAtom previousSibling) {
        this.previousSibling = previousSibling;
    }

    public void setData(SessionData data) {
        this.data = data;
    }

    // indique si l'uproc est ok ou ko
    public boolean isOk() {

        if (this.previousSibling == null) {
            if (this.parent == null) {
                return true; // root
            } else {
                return (this.parent.getChildOk() == this);
            }
        } else {
            return this.previousSibling.isOk();
        }
    }
}
