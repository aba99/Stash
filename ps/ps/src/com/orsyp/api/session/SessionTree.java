/*
 * SessionTree.java
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

/**
 * This class implements the tree of a Dollar Universe session.
 *
 * ***** WORK IN PROGRESS *****
 *
 * @author jjt
 * @version $Revision$
 * @see com.orsyp.api.session.Session
 * @see com.orsyp.api.session.SessionAtom
 */
public class SessionTree implements Serializable {

    // don't change the value of this field, unless you are knowingly making
    // changes to the class which will render it incompatible with old
    // serialized objects.
    private static final long serialVersionUID = 1L;

    /**
     * The root element of this session tree.
     * @serial
     */
    protected SessionAtom root;

    //--- Constructors -------------------------------------------------------

    /**
     * Constructs an empty session tree.
     */
    public SessionTree() {} /**@todo disallow and make it private? */

    /**
     * Constructs a <code>SessionTree</code> with the specified root element.
     *
     * @param root                the root element of this session tree.
     */
    public SessionTree(SessionAtom root) {

        // test about null?
        this.root = root;
    }

    //--- Public methods -----------------------------------------------------

    /**
     * Returns the root element of this session tree.
     *
     * @return the root element.
     */
    public SessionAtom getRoot() {

        return this.root;
    }

    /**
     * Specifies the root element of this session tree.
     * If the root element was previously set (and also the whole tree), it's
     * simply overriden, and the tree previous structure lost.
     *
     * @param root                the root element.
     */
    public void setRoot(SessionAtom root) {

        this.root = root;
    }

    /**
     * Returns the names of the uprocs referenced into this session tree,
     * with no duplicate elements.
     *
     * @return  the uprocs names.
     */
    public String[] getUprocs() {

        if (this.root == null) {
            return new String[0];
        }

        final ArrayList<String> array = new ArrayList<String>();

        scan(new AtomVisitor() {
            public void handle(SessionAtom atom) {
                String uproc = atom.getData().getUprocName();
                if (array.contains(uproc) == false) {
                    array.add(uproc);
                }
            }
        });
        String[] result = new String[array.size()];
        return array.toArray(result);
    }
    
    public SessionAtom[] getTrailerUprocs(){
    	
    	if(this.root==null)
    	{
    		return new SessionAtom[0];
    	}
    	
    	 final ArrayList<SessionAtom> array = new ArrayList<SessionAtom>();

         scan_forTrailers(new AtomVisitor() {
             public void handle(SessionAtom atom) {
                 SessionAtom uproc = new SessionAtom (atom.getData());
                 uproc.setParent(atom.getParent());
                 
                 if (array.contains(uproc) == false) {
                     array.add(uproc);
                 }
             }
         });
         SessionAtom[] result = new SessionAtom[array.size()];
         return array.toArray(result);
     
    }
    
    public SessionAtom getSessionAtom(String uprocName)
    {
    	if(this.root==null)
    	{
    		return null;
    	}
    	
    	
    	 final ArrayList<SessionAtom> array = new ArrayList<SessionAtom>();
    	 final String uprName = uprocName;
    	
    	scan_forAtom(new AtomVisitor(){
             
    		public void handle(SessionAtom atom) 
    		{
                 SessionAtom uproc = new SessionAtom (atom.getData());
                if(uproc.getData().getUprocName().equalsIgnoreCase(uprName))
                {
                	uproc.setChildKo(atom.getChildKo());
                	uproc.setChildOk(atom.getChildOk());
                	uproc.setNextSibling(atom.getNextSibling());
                	
                	array.add(uproc);
                }
          
             
    		}
         });
         
    	SessionAtom[] result = new SessionAtom[array.size()];
        return array.toArray(result)[0];
    }
    
/*public String[] getTrailerUprocs(){
    	
    	if(this.root==null)
    	{
    		return new String[0];
    	}
    	
    	 final ArrayList<String> array = new ArrayList<String>();

         scan_forTrailers(new AtomVisitor() {
             public void handle(SessionAtom atom) {
                 String uproc = atom.getData().getUprocName();
                 if (array.contains(uproc) == false) {
                     array.add(uproc);
                 }
             }
         });
         String[] result = new String[array.size()];
         return array.toArray(result);
     
    }*/
    
      
    /**
     * Returns the names of the mus referenced into this session tree,
     * with no duplicate elements.
     *
     * @return  the mus names.
     */
    public String[] getMus() {

        if (this.root == null) {
            return new String[0];
        }

        final ArrayList<String> array = new ArrayList<String>();

        scan(new AtomVisitor() {
            public void handle(SessionAtom atom) {
            	ExecutionContext exeContext = atom.getData().getExecutionContext();
            	String muName = exeContext.getMuName();
            	if ((muName != null) && (muName.trim().length() > 0)){
	                if (array.contains(muName) == false) {
	                    array.add(muName);
	                }
            	}
            }
        });
        String[] result = new String[array.size()];
        return array.toArray(result);
    }
    
    

    /**
     * Scans this session tree. This method goes through all the atoms of
     * this tree and, for each atom, gives it to the specified visitor.
     * This method guarantees that each atom is read once and only once.
     *
     * @param visitor             the visitor that handles atoms.
     */
    public void scan(AtomVisitor visitor) {

        scan(this.root, visitor);
    }
    
    public void scan_forTrailers(AtomVisitor visitor){
    	scan_forTrailers(this.root,visitor);
    }
    public void scan_forAtom(AtomVisitor visitor){
    	scan_forAtom(this.root,visitor);
    }
    
   

    //--- Private methods ----------------------------------------------------

    /**
     * Scans this session tree, starting at the specifies atom.
     * This method goes through all the children and sibling atoms of
     * the given atom and, for each atom, gives it to the specified visitor.
     * This method guarantees that each atom is read once and only once.
     *
     * @param atom                the start atom for scanning this tree.
     * @param visitor             the visitor that handles atoms.
     */
    private void scan(SessionAtom atom, AtomVisitor visitor) {

        if ((atom == null) || (visitor == null)) {
            return;
        }

        visitor.handle(atom);

        if (atom.getChildOk() != null) {
            scan(atom.getChildOk(), visitor);
        }

        if (atom.getChildKo() != null) {
            scan(atom.getChildKo(), visitor);
        }

        if (atom.getNextSibling() != null) {
            scan(atom.getNextSibling(), visitor);
        }
    }
    
    private void scan_forAtom(SessionAtom atom, AtomVisitor visitor) {

        if ((atom == null) || (visitor == null)) {
            return;
        }

        visitor.handle(atom);

        if (atom.getChildOk() != null) {
        	scan_forAtom(atom.getChildOk(), visitor);
        }

        if (atom.getChildKo() != null) {
        	scan_forAtom(atom.getChildKo(), visitor);
        }

        if (atom.getNextSibling() != null) {
        	scan_forAtom(atom.getNextSibling(), visitor);
        }
    }
    
    public void scan_forTrailers(SessionAtom atom, AtomVisitor visitor)
    {
    	
    	//System.out.println("-- At atom : "+atom.getData().getUprocName());
    	
    	if (((atom.getChildKo()==null)&& (atom.getChildOk()==null)) || (atom == null) || (visitor == null)) {
    		visitor.handle(atom);
    		//return;
        }

        if (atom.getChildOk() != null) {
        	scan_forTrailers(atom.getChildOk(), visitor);

        	//System.out.println("Scanning starting at childOK of "+atom.getData().getUprocName()+" --> "+atom.getChildOk().getData().getUprocName());
        }

        if (atom.getChildKo() != null) {
        	scan_forTrailers(atom.getChildKo(), visitor);
        }

        if (atom.getNextSibling() != null) {
        	scan_forTrailers(atom.getNextSibling(), visitor);

        	//System.out.println("Scanning starting at sibling of "+atom.getData().getUprocName()+" --> "+atom.getNextSibling().getData().getUprocName());

        }
        
        //System.out.println(atom.getData().getUprocName()+" in session with root "+this.root.getData().getUprocName() );
    
    }
    
   
    

    //--- ScanVisitor interface ----------------------------------------------

    public interface AtomVisitor {
        void handle(SessionAtom data);
    }
   

    //protected interface DataVisitor {
    //    void handle(SessionData data);
    //}

}
