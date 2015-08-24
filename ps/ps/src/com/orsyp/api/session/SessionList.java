/*
 * SessionList.java
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

import com.orsyp.UniverseException;
import com.orsyp.api.Context;
import com.orsyp.api.Filter;
import com.orsyp.api.ItemList;
import com.orsyp.api.NoImplementationException;
import com.orsyp.api.security.Operation;
import com.orsyp.api.spec.SessionListImpl;

/**
 * This class implements the Dollar Universe session list.
 * A <code>SessionList</code> is a collection of <code>SessionItem</code>
 *
 * @author jjt
 * @version 2.0
 *
 * @see com.orsyp.api.session.SessionFilter
 * @see com.orsyp.api.session.SessionItem
 * @see com.orsyp.api.ItemList
 */
public class SessionList extends ItemList<SessionItem> {

    // don't change the value of this field, unless you are knowingly making
    // changes to the class which will render it incompatible with old
    // serialized objects.
    private static final long serialVersionUID = 1L;


    /**
     * The implementation of the session list API.
     */
    private transient SessionListImpl impl;


    /**
     * Constructs a <code>SessionList</code> with the specified parameters.
     *
     * @param  context     the Dollar Universe context of the list
     * @param  filter      the filter
     */
    public SessionList(Context context, Filter<SessionItem> filter) {

        super(context, filter);
    }

    /**
     * Constructs a <code>SessionList</code> with the specified parameters.
     *
     * @param  context     the Dollar Universe context of the list
     */
    public SessionList(Context context) {

        super(context);
    }

    /**
     * Returns the default filter.
     * The default filter is used if no filter was supplied.
     * The returned filter is a <code>SessionFilter</code>.
     *
     * @return  the default filter
     *
     * @see com.orsyp.api.session.SessionFilter
     */
    public Filter<SessionItem> getDefaultFilter() {

        return new SessionFilter();
    }

    /**
     * Extracts this list for the specified <code>operation</code> purpose.
     * The number of items extracted can be retrieved by calling
     * the {@link com.orsyp.api.ItemList#getCount} method.
     * An element of the list can be retrieved by calling the
     * {@link com.orsyp.api.ItemList#get} method.
     * <p>
     * For example, to get all elements of the list:
     * <blockquote><pre>
     *     for (int i = 0; i < list.getCount(); i++) {
     *         SessionItem session = (SessionItem) list.get(i);
     *         // do something with session
     *     }
     * </pre></blockquote>
     *
     * @param  operation          the operation  for security check
     *
     * @throws  UniverseException  if an error occured
     */
    public void extract(Operation operation) throws UniverseException {

        setDefaultImpl();
        if (impl == null) {
            throw new NoImplementationException();
        }
        impl.init(this, operation);
        impl.extract();
    }

    /**
     * Sets the API implementation for the list.
     *
     * @param  impl        a session list implementation
     */
    public void setImpl(SessionListImpl impl) {

        this.impl = impl;
    }

    /**
     * Sets the default API implementation for the list, if none was specified.
     * The default implementation is the one using the IO server.
     */
    private void setDefaultImpl() {

        if (impl != null) {
            return;
        }
        // utiliser une fabrique ?
        String classname = null;
        if (implClassName != null) {
            classname = implClassName;
        } else {
            // on essaye l'implémentation standard (par serveur d'io)
            classname = "com.orsyp.std.SessionListStdImpl";
        }
        try {
            impl = (SessionListImpl) Class.forName(classname).newInstance();
            return;
        } catch (ClassNotFoundException e) {
            error("cannot instanciate implementation", e);
        } catch (InstantiationException e) {
            error("cannot instanciate implementation", e);
        } catch (IllegalAccessException e) {
            error("cannot instanciate implementation", e);
        }
        // l'instanciation a échoué, on assure la nullité
        impl = null;
    }
}
