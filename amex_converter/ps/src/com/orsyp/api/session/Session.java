/*
 * Session.java
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
import java.util.ArrayList;
import java.util.List;

import com.orsyp.Area;
import com.orsyp.SyntaxException;
import com.orsyp.UniverseException;
import com.orsyp.api.Context;
import com.orsyp.api.DataMissingException;
import com.orsyp.api.IdentifierMissingException;
import com.orsyp.api.NoImplementationException;
import com.orsyp.api.Product;
import com.orsyp.api.Target;
import com.orsyp.api.UniObject;
import com.orsyp.api.security.Operation;
import com.orsyp.api.spec.SessionImpl;
import com.orsyp.util.Text;

/**
 * This class implements a Dollar Universe session.
 *
 * <p> A session is a tree structure comprising uprocs that are homogeneous
 * not necessarily from the functional viewpoint (they may contain uprocs
 * from several applications or different domains), but in terms of their
 * operations constraints. </p>
 *
 * <p> The session predefines an order in which the uprocs are launched, knowing
 * that at each phase, Dollar Universe will systematically check the execution
 * conditions of the uprocs that the session is telling it to launch. </p>
 *
 * @author jjt
 * @version $Revision$
 * @see com.orsyp.api.session.SessionTree
 */
public class Session extends UniObject<SessionId> {

    // don't change the value of this field, unless you are knowingly making
    // changes to the class which will render it incompatible with old
    // serialized objects.
    private static final long serialVersionUID = 1L;


    /**
     * The label of the session.
     * Syntax: 40 characters (Lengths.LG_SESSION_LABEL)
     *
     * @serial
     */
    protected String label;

    /**
     * The session tree.
     *
     * @serial
     */
    protected SessionTree tree;
    
    /** for session development (session expand) : indicates the direction child Ok */
    public static final char DIRECTION_OK = 'T';
    
    /** for session development (session expand) : indicates the direction child Ko (error child) */
    public static final char DIRECTION_KO = 'I';
    
    /** for session development (session expand) */
    protected List<PostSes> postSesList = new ArrayList<PostSes>();
    
    /** for session development (session expand) : the list of session developments */
    protected List<PostDev> postDevList = new ArrayList<PostDev>();
    
    /**
     * The session API implementation.
     */
    private transient SessionImpl impl;


    /*
     * IF YOU ADD FIELDS (having getter/setter), 
     * PLEASE FILL IN METHOD populate().
     */
    
    /**
     * Copies data from the input object to this object.
     * 
     * @param input
     *            the object from which we get data.
     * @param isSessionDevelop true if populate is used for session develop
     * @throws UniverseException
     *             if any error occurs.
     */
    public void populate(Session input, boolean isSessionDevelop) throws UniverseException {
        if (!isSessionDevelop) {
            this.setLabel(input.getLabel());
            this.setTree(input.getTree());
        }
        this.setPostDevList(input.getPostDevList());
        this.setPostSesList(input.getPostSesList());
    }
    
    //--- Construction -------------------------------------------------------

    /**
     * Constructs a <code>Session</code> with the specified context and
     * identifier.
     *
     * @param  context            the Dollar Universe context.
     * @param  identifier         the session identifier.
     */
    public Session(Context context, SessionId identifier) {

        super(context, identifier);
    }

    /**
     * Constructs a <code>Session</code> with the specified identifier.
     * This newly created <code>Session</code> has no context set.
     *
     * @param  identifier         the session identifier.
     */
    public Session(SessionId identifier) {

        super(identifier);
    }

    /**
     * Constructs a <code>Session</code> with the specified parameters.
     *
     * @param  name               the session name.
     * @param  version            the session version.
     *
     * @throws SyntaxException  if a parameter is invalid.
     */
    public Session(String name, String version) throws SyntaxException {

        super(new SessionId(name, version));
    }

    /**
     * Constructs a <code>Session</code> with the specified session item.
     *
     * @param  item               an element of a session items list.
     *
     * @throws UniverseException
     *
     * @see com.orsyp.api.session.Sessionitem
     * @see com.orsyp.api.session.SessionList
     */
    public Session(SessionItem item) throws UniverseException {

        super(null, null);

        setContext(item.getContext());
        setIdentifier(item.getIdentifier());
        setLabel(item.getLabel());
        setHeader(item.getHeader());
    }


    //--- Operations ---------------------------------------------------------

    /**
     * Extracts the attributes of this session.
     * The <code>context</code> and <code>identifier</code> must be supplied.
     *
     * @throws UniverseException  if an error occurs.
     */
    public void extract() throws UniverseException {

        checkIdentifier();

        setImpl();
        if (this.impl == null) {
            throw new NoImplementationException();
        }
        this.impl.init(this);
        this.impl.extract();
    }

    /**
     * Creates this session in Dollar Universe.
     * All attributes must be set. If the label is not set (has a
     * <code>null</code> value), this session is created with a blank label
     * (empty string). The session tree must contain al least one element.
     *
     * @throws UniverseException  if an error occurs.
     */
    public void create() throws UniverseException {

        check(false);

        setImpl();
        if (this.impl == null) {
            throw new NoImplementationException();
        }
        this.impl.init(this);
        this.impl.create();
    }
    
    /**
     * When creating the object in a different context than CREATE (DUPLICATE....)
     * @param operationContext the context of the CREATE operation
     * @throws UniverseException
     */
    public void create(Operation operationContext) throws UniverseException {

        check(false);

        setImpl();
        if (this.impl == null) {
            throw new NoImplementationException();
        }
        this.impl.init(this);
        this.impl.create(operationContext);
    }

    /**
     * Updates the attributes of this session in Dollar Universe.
     * All attributes must be set. If the label is not set (has a
     * <code>null</code> value), this session is created with a blank label
     * (empty string). The session tree must contain al least one element.
     *
     * @throws UniverseException  if an error occurs.
     */
    public void update() throws UniverseException {

        check(false);

        setImpl();
        if (this.impl == null) {
            throw new NoImplementationException();
        }
        this.impl.init(this);
        this.impl.update();
    }

    /**
     * Deletes this session in Dollar Universe.
     * The <code>context</code> and <code>identifier</code> must be supplied.
     *
     * @throws UniverseException  if an error occurs.
     */
    public void delete() throws UniverseException {

        checkIdentifier();

        setImpl();
        if (this.impl == null) {
            throw new NoImplementationException();
        }
        this.impl.init(this);
        this.impl.delete();
    }

    /**
     * Duplicates this session in Dollar Universe.
     * The duplicated session will have the same label as this session.
     *
     * @param  id                 the identifier of the new session.
     *
     * @throws UniverseException  if an error occurs.
     */
    public void duplicate(SessionId id) throws UniverseException {

        check(true);
        setImpl();
        if (this.impl == null) {
            throw new NoImplementationException();
        }
        this.impl.init(this);
        this.impl.duplicate(id, this.label);
    }

    /**
     * Duplicates this session in Dollar Universe.
     *
     * @param  id                 the identifier of the new session.
     * @param  label              the label of the new session.
     *
     * @throws UniverseException  if an error occurs.
     */
    public void duplicate(SessionId id, String label) throws UniverseException {

        check(true);
        setImpl();
        if (this.impl == null) {
            throw new NoImplementationException();
        }
        this.impl.init(this);
        this.impl.duplicate(id, label);
    }

    /**
     * Transfers this session into the specified area.
     * The <code>context</code> and <code>identifier</code> must be supplied.
     *
     * @param  area               the target area.
     *
     * @throws UniverseException  if an error occurs.
     */
    public void transfer(Area area) throws UniverseException {

        checkIdentifier();
        setImpl();
        if (this.impl == null) {
            throw new NoImplementationException();
        }
        this.impl.init(this);
        this.impl.transfer(area);
    }

    /**
     * Distributes this session to the specified targets.
     * The <code>context</code> and <code>identifier</code> must be supplied.
     *
     * @param  targets            the distibution targets.
     *
     * @throws UniverseException  if an error occurs.
     */
    public void distribute(Target[] targets) throws UniverseException {

        checkIdentifier();
        setImpl();
        if (this.impl == null) {
            throw new NoImplementationException();
        }
        this.impl.init(this);
        this.impl.distribute(targets);
    }

    /**
     * Unlocks this session.
     *
     * @throws UniverseException  if an error occurs.
     */
    public void unlock() throws UniverseException {

        checkIdentifier();
        setImpl();
        if (this.impl == null) {
            throw new NoImplementationException();
        }
        this.impl.init(this);
        this.impl.unlock();
    }
    
    /**
     * Develops (or expands) this session.
     * 
     * @param muCode
     *            the muId of the session.
     * @throws UniverseException
     *             if any error occurs.
     */
    public void develop(String muCode) throws UniverseException {

        checkIdentifier();
        setImpl();
        if (this.impl == null) {
            throw new NoImplementationException();
        }
        this.impl.init(this);
        this.impl.develop(muCode);
    }
    
    //--- Accessors ----------------------------------------------------------

    /**
     * Returns the short name of this session.
     * If this session has no identifier, this method returns <code>null</code>.
     *
     * @return the short name(internal identifier) of this session.
     */
    public String getId() {

        return (this.identifier == null) ? null : this.identifier.getId();
    }

    /**
     * Specifies the short name of this session.
     *
     * @param id                the session short name.
     *
     * @throws SyntaxException  if <code>id</code> is invalid.
     */
    public void setId(String id) throws SyntaxException {

        if (this.identifier == null) {
            this.identifier = new SessionId(id);
        } else {
            this.identifier.setId(id);
        }
    }

    /**
     * Returns the extended name of this session.
     * If this session has no identifier, this method returns <code>null</code>.
     *
     * @return the extended name of this session.
     */
    public String getName() {

        return (this.identifier == null) ? null : this.identifier.getName();
    }

    /**
     * Specifies the extended name of this session.
     *
     * @param name                the session extended name.
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
     * Returns the version of this session.
     * If this session has no identifier, this method returns <code>null</code>.
     *
     * @return the session version.
     */
    public String getVersion() {

        return (this.identifier == null) ? null : this.identifier.getVersion();
    }

    /**
     * Specifies the version of this session.
     *
     * @param version             the session version.
     *
     * @throws SyntaxException  if <code>version</code> is invalid.
     */
    public void setVersion(String version) throws SyntaxException {

        if (this.identifier != null) {
            this.identifier.setVersion(version);
        } else {
            // warning: this creates an invalid identifier
            // -> throws IllegalStateException ?
            // -> throws IdentifierMissingException ?
            this.identifier = SessionId.create(null, version);
        }
    }

    /**
     * Returns the label of this session.
     * The returnef string may be <code>null</code>.
     *
     * @return the session label.
     */
    public String getLabel() {

        return this.label;
    }

    /**
     * Specifies the label of this session.
     * The stored label is right-trimmed.
     * If the syntax check is disabled for this session (
     * {@link com.orsyp.api.UniObject#disableSyntaxCheck
     * <code>disableSyntaxCheck()</code>} method), this method doesn't check
     * label syntax and doesn't throw any exception.
     *
     * @param label               the session label, not <code>null</code>.
     *
     * @throws SyntaxException  if <code>label</code> is invalid.
     */
    public void setLabel(String label) throws SyntaxException {

        if (isCheckSyntax()) {
            this.label = checkLabel(label);
        } else {
            this.label = Text.trimRight(label);
        }
    }

    /**
     * Returns the session tree.
     *
     * @return the tree.
     */
    public SessionTree getTree() {

        return this.tree;
    }

    /**
     * Specifies the tree of this session.
     *
     * @param tree                the session tree.
     */
    public void setTree(SessionTree tree) {

        this.tree = tree;
    }

    /**
     * Returns the name of the uproc header of this session.
     * The uproc header is the root element of the session tree.
     * If this session ha no tree, this method returns <code>null</code>.
     *
     * @return the uproc header name.
     */
    public String getHeader() {

        if (this.tree == null) {
            return null;
        }
        /**@todo safe accessors */
        return this.tree.getRoot().getData().getUprocName();
    }

    /**
     * Specifies the name of the uproc header of this session.
     *
     * @param header              uproc header name.
     */
    public void setHeader(String header) {

        if (this.tree != null) {
            /**@todo safe accessors */
            this.tree.getRoot().getData().setUprocName(header);
        } else {
            /**@todo mu = null or mu = "" ? */
            this.tree = new SessionTree(
                    new SessionAtom(new SessionData(header)));
        }
    }

    /**
     * Specifies the API implementation for this session.
     *
     * @param impl                a session API implementation.
     */
    public void setImpl(SessionImpl impl) {

        // should we forbid if aleready set?
        this.impl = impl;
    }

    /**
     * Returns the names of the uprocs referenced into this session,
     * with no duplicate elements.
     *
     * @return  the uprocs names.
     */
    public String[] getUprocs() {

        return (this.tree != null) ? this.tree.getUprocs() : new String[0];
    }

    //--- Protected method ---------------------------------------------------

    /**
     * @return the sessDevList
     */
    public List<PostSes> getPostSesList() {
        return this.postSesList;
    }

    /**
     * @param sessDevList the sessDevList to set
     */
    public void setPostSesList(List<PostSes> sessDevList) {
        this.postSesList = sessDevList;
    }

    /**
     * @return the sessDev2List
     */
    public List<PostDev> getPostDevList() {
        return this.postDevList;
    }

    /**
     * @param sessDev2List the sessDev2List to set
     */
    public void setPostDevList(List<PostDev> sessDev2List) {
        this.postDevList = sessDev2List;
    }

    /**
     * Checks the identifier of this session.
     *
     * @throws UniverseException  if the identifier is missing or is invalid.
     */
    protected void checkIdentifier() throws UniverseException {

        if (this.identifier == null) {
            throw new IdentifierMissingException();
        }
        // If we assume that we cannot create invalid identifier, we have
        // nothing to do, but since we can instanciate invalid identifier
        // with SessionId.create, we must perform checks.
        this.identifier.check();
    }

    /**
     * Checks the syntax of the specified label as a session label.
     *
     * @param label               the label to check, not <code>null</code>.
     *
     * @return  the valid label.
     *
     * @throws SyntaxException  if <code>label</code> is invalid.
     */
    protected String checkLabel(String label) throws SyntaxException {

    	// Cannot be static anymore since we need the syntax rule
        String trim = Text.trimRight(label);
        if (trim.length() > getIdentifier().getSyntaxRules().getLgSessionLabel()) {
            throw new SyntaxException(label);
        }
        return trim;
    }

    /**
     * Checks the validity of this session's data. This method is called when
     * creating or updating this session.
     * 
     * @param isDuplicate
     *            true if it's duplicate operation.
     * @throws UniverseException
     *             if some data is missing or is invalid.
     */
    protected void check(boolean isDuplicateOperation) throws UniverseException {

        checkIdentifier();
        if (this.label != null) {
            // null label to be handled by impl
            checkLabel(this.label);
        }
        if (isDuplicateOperation && this.context.getProduct() != Product.OWLS) {
            if (this.tree == null) {
                throw new DataMissingException();
            }
            if (this.tree.getRoot() == null) {
                // don't allow empty tree
                throw new DataMissingException();
            }
        }
    }

    //--- Private method -----------------------------------------------------

    /**
     * Sets the default rule API implementation if none was specified.
     * The default implementation is the one using the IO server.
     *
     * *** THIS CODE DOES NOT WORK ***
     */
    private void setImpl() {

        if (this.impl != null) {
            return;
        }
        // use a fabric?
        String classname = null;
        if (this.implClassName != null) {
            classname = this.implClassName;
        } else {
            // nothing specified, try the default
            classname = "com.orsyp.std.SessionStdImpl";
        }
        try {
            // *** DOES NOT WORK ***
            // no empty c'tor for SessionStdImpl
            this.impl = (SessionImpl) Class.forName(classname).newInstance();
            return;
        } catch (ClassNotFoundException e) {
            error("cannot instanciate implementation - ", e);
        } catch (InstantiationException e) {
            error("cannot instanciate implementation - ", e);
        } catch (IllegalAccessException e) {
            error("cannot instanciate implementation - ", e);
        }
    }
    
    /**
     * Represents an element in session development (expand).
     * 
     * @author vch
     * @version $Revision$
     * @since UniViewer 2.0
     */
    public class PostDev implements Serializable
    {
        /**
         * don't change the value of this field, unless you are knowingly making
         * changes to the class which will render it incompatible with old
         * serialized objects.
         */
        private static final long serialVersionUID = 1L;
        
        /** the uproc id */
        public String codProc;

        /** the mu id */
        public String muId;
        
        /** the node code */
        public String nodeCode;   
        
        /** the level */
        public int level; 
        
        /** the way we follow: T -> ok  , I -> ko */
        public char directionOKorKO;  
        
        /** the number of the father : identifying the element */
        public int numFather;
        
        /** the order number of the session : identifying the element in the session */
        public int numOrderSession;
        
        /** the order number of the session */
        public String appliCode;

        /**
         * @return the codProc
         */
        public String getCodProc() {
            return this.codProc;
        }

        /**
         * @param codProc the codProc to set
         */
        public void setCodProc(String codProc) {
            this.codProc = codProc;
        }

        /**
         * @return the muId
         */
        public String getMuId() {
            return this.muId;
        }

        /**
         * @param muId the muId to set
         */
        public void setMuId(String muId) {
            this.muId = muId;
        }

        /**
         * @return the nodeCode
         */
        public String getNodeCode() {
            return this.nodeCode;
        }

        /**
         * @param nodeCode the nodeCode to set
         */
        public void setNodeCode(String nodeCode) {
            this.nodeCode = nodeCode;
        }

        /**
         * @return the level
         */
        public int getLevel() {
            return this.level;
        }

        /**
         * @param level the level to set
         */
        public void setLevel(int level) {
            this.level = level;
        }

        /**
         * @return the directionOKorKO
         */
        public char getDirectionOKorKO() {
            return this.directionOKorKO;
        }

        /**
         * @param directionOKorKO the directionOKorKO to set
         */
        public void setDirectionOKorKO(char directionOKorKO) {
            this.directionOKorKO = directionOKorKO;
        }

        /**
         * @return the numFather
         */
        public int getNumFather() {
            return this.numFather;
        }

        /**
         * @param numFather the numFather to set
         */
        public void setNumFather(int numFather) {
            this.numFather = numFather;
        }

        /**
         * @return the numOrderSession
         */
        public int getNumOrderSession() {
            return this.numOrderSession;
        }

        /**
         * @param numOrderSession the numOrderSession to set
         */
        public void setNumOrderSession(int numOrderSession) {
            this.numOrderSession = numOrderSession;
        }

        /**
         * @return the appliCode
         */
        public String getAppliCode() {
            return this.appliCode;
        }

        /**
         * @param appliCode the appliCode to set
         */
        public void setAppliCode(String appliCode) {
            this.appliCode = appliCode;
        }
        
        
    }
    
    /**
     * Represents an element in session development (expand).
     * 
     * @author vch
     * @version $Revision$
     * @since UniViewer 2.0
     */
    public class PostSes implements Serializable
    {
        /**
         * don't change the value of this field, unless you are knowingly making
         * changes to the class which will render it incompatible with old
         * serialized objects.
         */
        private static final long serialVersionUID = 1L;
        
        /** the article status code */
        public String artStatusCode;
        
        /** the ctrl mu */
        public String ctlnivug;
        
        /** the uproc id */
        public String codProc;
        
        /** the order number of the article */
        public String numOrder;
        
        /** the order number of the father */
        public String numOrderFather;
        
        /** the order number of the child OK */
        public String numOrderChildOk;
        
        /** the order number of the child KO */
        public String numOrderChildKo;
        
        /** the order number of the sibling */
        public String numOrderSibling;

        /** the NumOrdArt article */
        public String numOrdArt;
        
        /**
         * @return the artStatusCode
         */
        public String getArtStatusCode() {
            return this.artStatusCode;
        }

        /**
         * @param artStatusCode the artStatusCode to set
         */
        public void setArtStatusCode(String artStatusCode) {
            this.artStatusCode = artStatusCode;
        }

        /**
         * @return the ctlnivug
         */
        public String getCtlnivug() {
            return this.ctlnivug;
        }

        /**
         * @param ctlnivug the ctlnivug to set
         */
        public void setCtlnivug(String ctlnivug) {
            this.ctlnivug = ctlnivug;
        }

        /**
         * @return the codProc
         */
        public String getCodProc() {
            return this.codProc;
        }

        /**
         * @param codProc the codProc to set
         */
        public void setCodProc(String codProc) {
            this.codProc = codProc;
        }

        /**
         * @return the numOrder
         */
        public String getNumOrder() {
            return this.numOrder;
        }

        /**
         * @param numOrder the numOrder to set
         */
        public void setNumOrder(String numOrder) {
            this.numOrder = numOrder;
        }

        /**
         * @return the numFather
         */
        public String getNumOrderFather() {
            return this.numOrderFather;
        }

        /**
         * @param numFather the numFather to set
         */
        public void setNumOrderFather(String numFather) {
            this.numOrderFather = numFather;
        }

        /**
         * @return the numChildOk
         */
        public String getNumOrderChildOk() {
            return this.numOrderChildOk;
        }

        /**
         * @param numChildOk the numChildOk to set
         */
        public void setNumOrderChildOk(String numChildOk) {
            this.numOrderChildOk = numChildOk;
        }

        /**
         * @return the numChildKo
         */
        public String getNumOrderChildKo() {
            return this.numOrderChildKo;
        }

        /**
         * @param numChildKo the numChildKo to set
         */
        public void setNumOrderChildKo(String numChildKo) {
            this.numOrderChildKo = numChildKo;
        }

        /**
         * @return the numSibling
         */
        public String getNumOrderSibling() {
            return this.numOrderSibling;
        }

        /**
         * @param numSibling the numSibling to set
         */
        public void setNumOrderSibling(String numSibling) {
            this.numOrderSibling = numSibling;
        }

        /**
         * @return the numOrdArt
         */
        public String getNumOrdArt() {
            return this.numOrdArt;
        }

        /**
         * @param numOrdArt the numOrdArt to set
         */
        public void setNumOrdArt(String numOrdArt) {
            this.numOrdArt = numOrdArt;
        }
        
        
    }

    /**
     * TODO
     * @return the impl
     */
    public SessionImpl getImpl() {
        return this.impl;
    }
}
