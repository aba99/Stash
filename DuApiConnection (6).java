package amex.tools;

import static java.lang.System.out;















import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;









import java.util.Vector;

import org.apache.commons.lang.SerializationUtils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.orsyp.Area;
import com.orsyp.Environment;
import com.orsyp.Identity;
import com.orsyp.SyntaxException;
import com.orsyp.UniverseException;
import com.orsyp.api.Client;
import com.orsyp.api.Context;
import com.orsyp.api.FunctionalPeriod;
import com.orsyp.api.ItemList;
import com.orsyp.api.ObjectNotFoundException;
import com.orsyp.api.Product;
import com.orsyp.api.Variable;
import com.orsyp.api.central.UniCentral;
import com.orsyp.api.dqm.DqmQueue;
import com.orsyp.api.dqm.DqmQueueFilter;
import com.orsyp.api.dqm.DqmQueueId;
import com.orsyp.api.dqm.DqmQueueList;
import com.orsyp.api.dqm.DqmQueueType;
import com.orsyp.api.event.JobEvent;
import com.orsyp.api.event.JobEventFilter;
import com.orsyp.api.event.JobEventItem;
import com.orsyp.api.event.JobEventList;
import com.orsyp.api.execution.ExecutionFilter;
import com.orsyp.api.execution.ExecutionItem;
import com.orsyp.api.execution.ExecutionList;
import com.orsyp.api.execution.ExecutionStatus;
import com.orsyp.api.launch.LaunchFilter;
import com.orsyp.api.launch.LaunchItem;
import com.orsyp.api.launch.LaunchList;
import com.orsyp.api.mu.Mu;
import com.orsyp.api.mu.MuFilter;
import com.orsyp.api.mu.MuList;
import com.orsyp.api.rule.KDayAuthorization;
import com.orsyp.api.rule.KDayAuthorization.KDayAuthorizationType;
import com.orsyp.api.rule.KmeleonPattern.KDayType;
import com.orsyp.api.rule.MonthAuthorization;
import com.orsyp.api.rule.MonthAuthorization.Direction;
import com.orsyp.api.rule.PositionsInPeriod;
import com.orsyp.api.rule.PositionsInPeriod.SubPeriodType;
import com.orsyp.api.rule.Rule;
import com.orsyp.api.rule.Rule.PeriodTypeEnum;
import com.orsyp.api.rule.RuleFilter;
import com.orsyp.api.rule.RuleId;
import com.orsyp.api.rule.RuleItem;
import com.orsyp.api.rule.RuleList;
import com.orsyp.api.rule.UniPattern;
import com.orsyp.api.rule.UniPattern.RunOverEnum;
import com.orsyp.api.rule.WeekAuthorization;
import com.orsyp.api.rule.YearAuthorization;
import com.orsyp.api.security.Operation;
import com.orsyp.api.session.ExecutionContext;
import com.orsyp.api.session.Session;
import com.orsyp.api.session.SessionAtom;
import com.orsyp.api.session.SessionData;
import com.orsyp.api.session.SessionFilter;
import com.orsyp.api.session.SessionId;
import com.orsyp.api.session.SessionItem;
import com.orsyp.api.session.SessionList;
import com.orsyp.api.session.SessionTree;
import com.orsyp.api.session.SessionTree.AtomVisitor;
import com.orsyp.api.syntaxerules.OwlsSyntaxRules;


import com.orsyp.api.task.LaunchHourPattern;
import com.orsyp.api.task.Task;
import com.orsyp.api.task.TaskFilter;
import com.orsyp.api.task.TaskId;
import com.orsyp.api.task.TaskImplicitData;
import com.orsyp.api.task.TaskItem;
import com.orsyp.api.task.TaskList;
import com.orsyp.api.task.TaskPlanifiedData;
import com.orsyp.api.task.TaskType;
import com.orsyp.api.uproc.Memorization;
import com.orsyp.api.uproc.MuControl;
import com.orsyp.api.uproc.MuControl.Type;
import com.orsyp.api.uproc.SessionControl;
import com.orsyp.api.uproc.DependencyCondition;
import com.orsyp.api.uproc.DependencyCondition.Status;
import com.orsyp.api.uproc.LaunchFormula;
import com.orsyp.api.uproc.Uproc;
import com.orsyp.api.uproc.UprocFilter;
import com.orsyp.api.uproc.UprocId;
import com.orsyp.api.uproc.UprocList;
import com.orsyp.api.uproc.UserControl;
import com.orsyp.api.uproc.cl.InternalScript;
import com.orsyp.central.jpa.jpo.NodeInfoEntity;
import com.orsyp.comm.client.ClientServiceLocator;
import com.orsyp.owls.impl.AbstractTest;
import com.orsyp.owls.impl.dqm.OwlsDqmQueueImpl;
import com.orsyp.owls.impl.dqm.OwlsDqmQueueListImpl;
import com.orsyp.owls.impl.event.OwlsJobEventImpl;
import com.orsyp.owls.impl.event.OwlsJobEventListImpl;
import com.orsyp.owls.impl.execution.OwlsExecutionListImpl;
import com.orsyp.owls.impl.launch.OwlsLaunchListImpl;
import com.orsyp.owls.impl.mu.OwlsMuImpl;
import com.orsyp.owls.impl.mu.OwlsMuListImpl;
import com.orsyp.owls.impl.rule.OwlsRuleImpl;
import com.orsyp.owls.impl.rule.OwlsRuleListImpl;
import com.orsyp.owls.impl.session.OwlsSessionImpl;
import com.orsyp.owls.impl.session.OwlsSessionListImpl;
import com.orsyp.owls.impl.task.OwlsTaskImpl;
import com.orsyp.owls.impl.task.OwlsTaskListImpl;
import com.orsyp.owls.impl.uproc.OwlsUprocImpl;
import com.orsyp.owls.impl.uproc.OwlsUprocListImpl;
import com.orsyp.owls.msg.TaskMsg.EOwlsTaskStatus;
import com.orsyp.owls.msg.TaskMsg.EOwlsTaskType;
import com.orsyp.std.ClientConnectionManager;
import com.orsyp.std.MultiCentralConnectionFactory;
import com.orsyp.std.central.UniCentralStdImpl;
import com.orsyp.util.DateTools;

import stateofde.SessionModel;


public class DuApiConnection extends AbstractTest{
	
	private String name;
	private Context context;
	
	static String fileName = new SimpleDateFormat("yyyyMMddhhmm").format(new Date());
    private static SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static SimpleDateFormat sdfHour = new SimpleDateFormat("HH:mm:ss");

    
	private String node;
	private String area;
	private String host;
	private int port ;
	private String user;
	private String password;

	private static String defaultVersion = "000";
	@SuppressWarnings("unused")
	private static String defaultSubmissionUser="user";
	public boolean isReference = false;
	private ArrayList<String> inconsistencies = new ArrayList<String>();
	
	private HashMap<String, Session> sess = new HashMap<String, Session>();
	private HashMap<String, Uproc> uprs = new HashMap<String, Uproc>();
	private HashMap<String,Task>tsks = new HashMap<String,Task>();
	private Multimap<String, Task> tsks_multimap = ArrayListMultimap.create();
	

	final static int TSKNAME_LIMIT = 63;
	final static String TASKNAMESPLITTER ="\\-";
	

	public DuApiConnection(String node, String area, String host, int port, String user, String password) {
		try {	
			
			
			getNodeConnection(node, area, host, port, user, password);
			this.node=node;
			this.area=area;
			this.host=host;
			this.port=port;
			this.user=user;
			this.password=password;
			
			this.name=node+"/"+area;
			
			sess = getSessionsHashMap("");
			uprs = getUprocHashMap("");
			tsks_multimap = getTaskMultiMap("");
			tsks = getTaskHashMap();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public DuApiConnection(PrintStream prtstm,String node, String area, String host, int port, String user, String password,boolean withUpr,String uprfilter,boolean withSes,String sesfilter,boolean withTsk,String tskfilter) {
		try {	
			
			
			getNodeConnection(node, area, host, port, user, password);
			this.node=node;
			this.area=area;
			this.host=host;
			this.port=port;
			this.user=user;
			this.password=password;
			
			this.name=node+"/"+area;
			
			System.out.println("Connecting to \""+node+"/"+area+"\" ...");
			prtstm.println("Connecting to \""+node+"/"+area+"\" ...");
			
			if(withUpr)
			{
				System.out.println("Extracting uprocs ...");
				prtstm.println("Extracting uprocs ...");
				uprs = getUprocHashMap(uprfilter);
			}
			
			if(withSes)
			{
				System.out.println("Extracting sessions ...");
				prtstm.println("Extracting sessions ...");
				sess = getSessionsHashMap(sesfilter);
			}
			
		
			
			if(withTsk)
			{
				System.out.println("Extracting tasks ...");
				prtstm.println("Extracting tasks ...");


				tsks_multimap= getTaskMultiMap(tskfilter);
				tsks = getTaskHashMap();

			}
			
			System.out.println("--> Connected !");
			prtstm.println("--> Connected !");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public DuApiConnection(String node, String area, String host, int port, String user, String password,boolean withUpr,boolean withSes,boolean withTsk) {
		try {	
			
			
			getNodeConnection(node, area, host, port, user, password);
			this.node=node;
			this.area=area;
			this.host=host;
			this.port=port;
			this.user=user;
			this.password=password;
			
			this.name=node+"/"+area;
			
			System.out.println("Connecting to \""+node+"/"+area+"\" :");
			
			if(withSes)
			{
				System.out.println("Extracting sessions ...");
				sess = getSessionsHashMap("");
			}
			
			if(withUpr)
			{
				System.out.println("Extracting uprocs ...");
				uprs = getUprocHashMap("");
			}
			
			if(withTsk)
			{
				System.out.println("Extracting tasks ...");

				tsks_multimap = getTaskMultiMap("");
				tsks = getTaskHashMap();

			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String getNode()
	{
		return node;
	}
	public String getUser()
	{
		return user;
	}
	public String getPassword()
	{
		return password;
	}
	public int getPort()
	{
		return port;
	}
	public String getArea()
	{
		return area;
	}
	public String getHost()
	{
		return host;
	}
	public String getConnName()
	{
		return this.name;
	}
	public String[] getDefaultMUs()
	{
		return null;
	}
	private void getNodeConnection(String node, String area, String host, int port, String user, String password) throws Exception{
		
		UniCentral central = getUVMSConnection(host, port, user, password);			
		NodeInfoEntity[] nnes = ClientServiceLocator.getNodeInfoService().getAllNodeInfoFromCache(-1, null);
		String company = null;
		
		for (NodeInfoEntity nne : nnes)
			if (nne.getProductCode().equals("DUN"))
				if (nne.getNodeName().equalsIgnoreCase(node)) {
					company = nne.getCompany();
					break;
				}
		
		if (company==null)
			throw new Exception("Node not found");
				
		if (Arrays.asList("A", "APP", "I", "INT").contains(area.toUpperCase())) 
			defaultVersion = "001";//001
		else
			defaultVersion = "000";
		
		Area a = Area.Exploitation;
		if (Arrays.asList("A", "APP").contains(area.toUpperCase()))
			a = Area.Application;
		else
		if (Arrays.asList("I", "INT").contains(area.toUpperCase()))
			a = Area.Integration;
		else
		if (Arrays.asList("S", "SIM").contains(area.toUpperCase()))
			a = Area.Simulation;
		
		context = makeContext(node, company, central, user, a);
	}
	
	private UniCentral getUVMSConnection(String host, int port, String user, String password) throws SyntaxException {		
		UniCentral cent = new UniCentral(host, port);
		cent.setImplementation(new UniCentralStdImpl(cent));
		
		Context ctx = new Context(new Environment("UJCENT",host), new Client(new Identity(user, password, host, "")));
		ctx.setProduct(com.orsyp.api.Product.UNICENTRAL);
		ctx.setUnijobCentral(cent);
		ClientServiceLocator.setContext(ctx);

		try {
			cent.login(user, password);
			if (ClientConnectionManager.getDefaultFactory() == null) {
	            ClientConnectionManager.setDefaultFactory(MultiCentralConnectionFactory.getInstance());
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return cent;
	}
	
	private Context makeContext(String node, String company, UniCentral central, String user, Area area) throws SyntaxException {
		Context ctx = null;
		Client client = new Client(new Identity(user, "", node, ""));
		ctx = new Context(new Environment(company, node, area), client, central);
		ctx.setProduct(Product.OWLS);
		return ctx;
	}
	


	
	//-------------------------------------------
		
	public Context getContext() {
		return context;
	}
	
	//-----------------------------------------
	
	public void duplicateUproc (String sourceUproc, String targetName,String uprlbl,int uprseverity) throws Exception{
		Uproc uproc = getUproc(sourceUproc);

		uproc.setDefaultSeverity(uprseverity);
		uproc.setLabel(uprlbl);
		uproc.update();
		uproc.extract();
		
        UprocId newDuplicatedUprocId = new UprocId(targetName, defaultVersion);
		newDuplicatedUprocId.setId(targetName);  
        
        uproc.setImpl(new OwlsUprocImpl());
        uproc.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
        uproc.duplicate(newDuplicatedUprocId, uproc.getLabel());
	}
	
	public void duplicateUproc (String sourceUproc, String targetName) throws Exception{
		
		
		
		if(uprocAlreadyExists(targetName))
		{
			Uproc toDelete=getUproc(targetName);
			toDelete.delete();
			uprs.remove(targetName);
		}
		Uproc uproc = getUproc(sourceUproc);
	
		
        UprocId newDuplicatedUprocId = new UprocId(targetName, defaultVersion);
        newDuplicatedUprocId.setId(targetName);  
        
        uproc.setImpl(new OwlsUprocImpl());
        uproc.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
        uproc.duplicate(newDuplicatedUprocId, uproc.getLabel());
        

        System.out.println("Uproc "+targetName+" created");
        
        if(!uprs.containsKey(targetName))
        {
        	Uproc obj = new Uproc(getContext(), newDuplicatedUprocId);
        	obj.setImpl(new OwlsUprocImpl());
        	obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
        	obj.extract();
        
        	uprs.put(targetName, obj);
        
        }
	
	}
	
	public Uproc getUproc(String name) throws Exception{
	
		
		UprocId uprocId = new UprocId(name, defaultVersion);
		Uproc obj = new Uproc(getContext(), uprocId);
        
        obj.setImpl(new OwlsUprocImpl());
        obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());

        obj.extract();
		return obj;
	}
	
	
	public Session getSession(String name) throws Exception{
		
		Session obj=null;
		
		
		    	SessionId sessionId = new SessionId(name, defaultVersion);
	            obj = new Session(getContext(), sessionId);
	            obj.setImpl(new OwlsSessionImpl());
	            obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
	            obj.extract();

		return obj;
	}
	
	public boolean ruleAlreadyExists(String rulename) throws UniverseException
	{
	   
	        /* pattern for rule name  */
	        String name = rulename.toUpperCase();
	        String label = "*";
	        
	        RuleFilter filter = new RuleFilter(name,label);

	        RuleList list = new RuleList(getContext(), filter);
	        list.setImpl(new OwlsRuleListImpl());
	        list.extract();

	        if(list.getCount()!=0)
	        {
	        	return true;
	        }
	        else
	        {
	        	return false;
	        }
	    
	}
/*	public boolean isConsistent() throws UniverseException, Exception
	{
		HashMap<String,Uproc> uprocs = this.getUprocHashMap();
		HashMap<String,Session>sessions=this.getSessionsHashMap();
		HashMap<String,Task> tasks=this.getTaskHashMap();
		
		for(String session:sessions.keySet())
		{
			for(int upr=0;upr<sessions.get(session).getUprocs().length;upr++)
			{
				if(!uprocs.containsKey((sessions.get(session).getUprocs()[upr])))
				{
					
					inconsistencies.add("SES ["+session+"] contains inexistant UPR ["+sessions.get(session).getUprocs()[upr]+"] on "+this.getConnName());
					
				}
			}
		}
		
		for(String task:tasks.keySet())
		{
			if(tasks.get(task).getTaskType().equals(TaskType.Scheduled))
			{
				if(!sessions.containsKey(tasks.get(task).getSessionName()))
				{
					inconsistencies.add("TSK ["+task+"] contains inexistant SES ["+tasks.get(task).getSessionName()+"] on "+this.getConnName());

					
				}
				
				
			}
			
			if(tasks.get(task).getTaskType().equals(TaskType.Optional))
			{
				if(!uprocs.containsKey(tasks.get(task).getUprocName()))
				{
					inconsistencies.add("TSK ["+task+"] contains inexistant UPR ["+tasks.get(task).getUprocName()+"] on "+this.getConnName());

					
				}
				
			}
		}
		
		if(inconsistencies.size()==0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}*/
	
	public ArrayList<String> getInconsistencyList()
	{
		return this.inconsistencies;
	}
	public void dumpSessions_to_CSV(HashMap<String,String>sesList, PrintStream prtstm)
	{
		prtstm.println("Session,Uproc,Rule,LW,MU");
		for(String sesKey:sesList.keySet())
		{
			if(sess.containsKey(sesKey))
			{
				for(int u=0;u<sess.get(sesKey).getUprocs().length;u++)
				{
					prtstm.println(sesKey+","+sess.get(sesKey).getUprocs()[u]+","+getRULE_LW_MU_for_CSV(sess.get(sesKey).getUprocs()[u]));
				}
			}
		}
	}
	public void deleteTasksForSession(String sesName) throws UniverseException
	{
		for(String tskKey:tsks.keySet())
		{
			
		/*	if(tsks.get(tskKey).getSessionName().equalsIgnoreCase(sesName))
			{
				tsks.get(tskKey).delete();
			}*/
			if(tsks.get(tskKey).getSessionName().equalsIgnoreCase(sesName))
			{
				tsks.get(tskKey).delete();
			}
		}
	}
	
	
	
    int countChild(SessionAtom atom) {
        int childCount = 0; // le nb de child au level+1
        if (atom.getChildOk() != null) {
            childCount++;
            SessionAtom a = atom.getChildOk();
            while (a.getNextSibling() != null) {
                childCount++;
                a = a.getNextSibling();
            }
        }
        if (atom.getChildKo() != null) {
            childCount++;
            SessionAtom a = atom.getChildKo();
            while (a.getNextSibling() != null) {
                childCount++;
                a = a.getNextSibling();
            }
        }
        return childCount;
    }


    public static void print(Session session) {

        out.println();
        out.println(" name:    " + session.getName());
        out.println(" id:      " + session.getId());
        out.println(" version: " + session.getVersion());
        out.println(" label:   " + session.getLabel());
        out.println();
        new PrintableTree(session.getTree()).print();
    }


    static class PrintableTree extends SessionTree {

        private static final long serialVersionUID = 1L;

        PrintableTree(SessionTree tree) {
            super(tree.getRoot());
        }

        void print() {

            print(root, 0, "");
        }


        java.util.Hashtable<String, Integer> map =
                new java.util.Hashtable<String, Integer>();

        //
        // Beeerk!! mais bon, c'est du test....
        //
        // il faut prefix.lenght = 4
        void print(SessionAtom atom, int level, String prefix) {

            StringBuffer buff = new StringBuffer("   ");

            if (level > 0) {
            for (int i = 0; i < level-1; i++) {
                   int count = getCount(i);
                   if (count == 0) {
                       buff.append(' ');
                   } else {
                       //buff.append((char) 179); //'\u2502'
                       buff.append('|');
                   }
                   for (int j = 1; j < (3+5); j++) {
                       buff.append(' ');
                   }
               }

                if (atom.getNextSibling() != null) {
                    //buff.append((char) 195); // '\u251C'
                    buff.append('+');
                } else {
                    if (getCount(level - 1) > 1) {
                        //buff.append((char) 195); // '\u251C'
                        buff.append('+');
                    } else {
                        //buff.append((char) 192); // '\u2514'
                        buff.append('\\');
                    }
                }
                //buff.append((char) 196); // '\u2500'
                //buff.append((char) 196);
                buff.append('-');
                buff.append('-');
                buff.append(prefix);
                buff.append(' '); //((char) 196);
            }
            
            if(atom.getData() != null)
            {
             // print data
                System.out.print(buff.toString() + atom.getData().getUprocName());
                ExecutionContext context = atom.getData().getExecutionContext();
                if (context != null) {
                    switch (context.getType()) {
                    case SAME:
                        break;
                    case MU:
                        System.out.print(" / " + context.getMuName());
                        break;
                    case HDP:
                        System.out.print(" / " + context.getHDP());
                        break;
                    }
                }
            }
            
            System.out.println();

            if (level > 0) {
                decrement(level - 1);
            }

            int childCount = countChild(atom); // le nb de child au level+1
            map.put(Integer.toString(level), new Integer(childCount));

            StringBuffer b = new StringBuffer("   ");
            for (int i = 0; i < level; i++) {
                int count = getCount(i);
                if (count > 0) {
                    //b.append((char) 179); //'\u2502'
                    b.append('|');
                } else {
                    b.append(' ');
                }
                for (int j = 1; j < (3 + 5); j++) {
                    b.append(' ');
                }
            }
            if (childCount > 0) {
                //b.append((char) 179); //'\u2502'
                b.append('|');
            }
            System.out.println(b);

            //fils ok
            if (atom.getChildOk() != null) {
                print(atom.getChildOk(), level + 1, "(ok)");
            }
            //fils ko
            if (atom.getChildKo() != null) {
                print(atom.getChildKo(), level + 1, "(ko)");
            }
            //fr�re
            if (atom.getNextSibling() != null) {
                print(atom.getNextSibling(), level,
                      (atom.getNextSibling().isOk()) ? "(ok)" : "(ko)");
            }
        }


        int countChild(SessionAtom atom) {
            int childCount = 0; // le nb de child au level+1
            if (atom.getChildOk() != null) {
                childCount++;
                SessionAtom a = atom.getChildOk();
                while (a.getNextSibling() != null) {
                    childCount++;
                    a = a.getNextSibling();
                }
            }
            if (atom.getChildKo() != null) {
                childCount++;
                SessionAtom a = atom.getChildKo();
                while (a.getNextSibling() != null) {
                    childCount++;
                    a = a.getNextSibling();
                }
            }
            return childCount;
        }


        int getCount(int level) {
            Integer count = (Integer) map.get(Integer.toString(level));
            if (count != null) {
                return count.intValue();
            } else {
                return 0;
            }
        }

        void decrement(int level) {
            Integer count = (Integer) map.get(Integer.toString(level));
            if (count != null) {
                int i = count.intValue() - 1;
                map.put(Integer.toString(level), new Integer(i));
            }
        }

    }


	
	public boolean uprocAlreadyExists(String uprname) throws UniverseException
	{
		
			return uprs.containsKey(uprname);
					
	}

	    
	
	
	public ItemList<LaunchItem> getLaunchList() throws UniverseException {
	        /* pattern for task name  */
	        LaunchFilter filter = new LaunchFilter();
	        filter.setSessionId("*");
	        filter.setUprocId("*");
	        filter.setMuId("*");
	        filter.setSessionName("*");
	        filter.setUprocName("*");
	        filter.setMuName("*");
	        filter.selectAllStatus();
	        filter.setUserName("*");
	        filter.setUserId("*");
	        filter.setBeginDate("*");  /**@todo use Date ?? */
	        filter.setBeginHour("*");  /**@todo use Date ?? */
	        filter.setEndDate("*");  /**@todo use Date ?? */
	        filter.setEndHour("*");  /**@todo use Date ?? */
	        filter.setProcessingDate("*");
	        filter.setNumlancMin("0000000");
	        filter.setNumlancMax("9999999");
	        filter.setNumsessMin("0000000");
	        filter.setNumsessMax("9999999");
	        filter.setNumprocMin("0000000");
	        filter.setNumprocMax("9999999");
	        
	        LaunchList list = new LaunchList(getContext(), filter);
	        list.setImpl(new OwlsLaunchListImpl());
	        list.extract();
	        return list;
	    }
	
	
	public void createDQM(String dqmname,int joblimit) {

        try {
            String queueName = dqmname;
            DqmQueue obj = new DqmQueue(getContext(),DqmQueueId.create(queueName));
            
            String connectionName = this.getConnName();
            String nodeName = connectionName.substring(0,connectionName.indexOf("/"));
            
            obj.setType(DqmQueueType.PHYSICAL);
            obj.setNodeName(nodeName);

            /* we set default values */
            obj.setJobLimit(joblimit);
            obj.setPrioDefaultSub(1);
            obj.setPrioMax(999);

            obj.setImpl(new OwlsDqmQueueImpl());
            obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance ());
            obj.create();
            obj.start();
           obj.update();
           // printf("Dqm queue [%s] created.\n", obj.getIdentifier().getName());
        } catch (SyntaxException e) {
            e.printStackTrace(System.out);
        } catch (UniverseException e) {
            e.printStackTrace(System.out);
        }
    }
	
	
	public HashMap<String,DqmQueue> getDqmsHashMap() throws UniverseException
	{
		
			HashMap<String,DqmQueue> result = new HashMap<String,DqmQueue>();
	        DqmQueueFilter filter = new DqmQueueFilter();

	        filter.setQueueName("*");
	        filter.setQueueStatus('*');
	        filter.setQueueType('*');
	                
	        /**
	         * DEPRECATED IN OWLS => to extract the related physical queues, run either
	         * {@link com.orsyp.api.dqm.DqmQueue#extract()} and consider field
	         * {@link com.orsyp.api.dqm.DqmQueue#relatedPhysicalQueueList}
	         * or run
	         * {@link com.orsyp.api.dqm.DqmQueueList#extract()} and consider field
	         * {@link com.orsyp.api.dqm.DqmQueueItem#relatedPhysicalQueueList}
	         */
	        final boolean  isRelatedQueueList = false;

	        /**
	         * DEPRECATED IN OWLS => to extract the related physical queues, run either
	         * {@link com.orsyp.api.dqm.DqmQueue#extract()} and consider field
	         * {@link com.orsyp.api.dqm.DqmQueue#relatedPhysicalQueueList}
	         * or run
	         * {@link com.orsyp.api.dqm.DqmQueueList#extract()} and consider field
	         * {@link com.orsyp.api.dqm.DqmQueueItem#relatedPhysicalQueueList}
	         */
	        final String queueNameRelated = null;
	        
	        DqmQueueList list = new DqmQueueList(getContext(), filter, isRelatedQueueList, queueNameRelated);
	        list.setImpl(new OwlsDqmQueueListImpl());
	        list.extract();
	        
	        for(int d=0;d<list.getCount();d++)
	        {
	        	 DqmQueue obj = new DqmQueue(getContext(),list.get(d).getIdentifier());
	             
	             obj.setImpl(new OwlsDqmQueueImpl());
	             obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance ());
	             obj.extract();
	             result.put(obj.getIdentifier().getName(),obj );
	        }
	    return result;
	}
	
	
	public boolean tskAlreadyExists(String tskname) throws Exception
	{
		Task foundTask_template = this.getTaskByName(tskname,true);
	    Task foundTask_nontemplate=this.getTaskByName(tskname,false);
	        
			if(foundTask_template==null && foundTask_nontemplate==null)
			{
				return false;
			}
			else
			{
				return true;
			}
			
	}
	
	public boolean taskAlreadyExists(String tskname) throws Exception
	{
		
			
				return tsks.containsKey(tskname);
			
			
	}
	public boolean sesAlreadyExists(String sesname) throws Exception
	{
		
				return sess.containsKey(sesname);
		
			
	}
	

	public void createTempUproc(String upr_temp_name,int upr_temp_severity)
	{

        try {
            String uprocName = upr_temp_name;
            String uprocVersion = "000";

            UprocId uprocId = new UprocId(uprocName, uprocVersion);
            Uproc obj = new Uproc(getContext(), uprocId);
            obj.setLabel("Template");
            obj.setApplication("U_");
            obj.setDomain("I");
            obj.setType("CL_INT");
            obj.setDefaultInformation("");
            obj.setDefaultSeverity(upr_temp_severity);
            obj.setFunctionalPeriod(FunctionalPeriod.Day);
           
            Memorization memo = new Memorization(Memorization.Type.ONE);
            memo.setNumber(1);
            obj.setMemorization(memo);
         
            obj.setImpl(new OwlsUprocImpl());
            obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
            obj.create();
           

            if ("CL_INT".equals(obj.getType())) {
                /* once Uproc is created, we save the associated specific data  */
                createInternalScript(obj);   
            }
           /* if ("CMD".equals(obj.getType())) {

                CmdData cmdData = new CmdData();
                cmdData.setCommandLine("%uxexe%\\uxsleep 6600");//put your command here
                obj.setSpecificData(cmdData);
            }*/

        } catch (SyntaxException e) {
            e.printStackTrace(System.out);
        } catch (UniverseException e) {
            e.printStackTrace(System.out);
        }
    }
    private void createInternalScript(Uproc obj) throws UniverseException {
        InternalScript data = new InternalScript(obj);
        
        if(obj.getName().equalsIgnoreCase("C2_TEMPLATE_ACISSUB")){
        data.setLines(new String[] {"ACISsub"});}// put your script here
        
        else if(obj.getName().equalsIgnoreCase("C2_TEMPLATE_HEADER")){
            data.setLines(new String[] {"set resexe=0"});}
        
        else if(obj.getName().equalsIgnoreCase("C2_TEMPLATE_TRAILER")){
            data.setLines(new String[] {"set resexe=0"});}
        
        else if(obj.getName().equalsIgnoreCase("C2_TEMPLATE_NC_TRAILER")){
            data.setLines(new String[] {"set resexe=0"});}
        
        else if(obj.getName().equalsIgnoreCase("C2_TEMPLATE_MKT_SPC")){
        	data.setLines(new String[] {"REM call e:\\data\\%c2_ENV%\\scripts\\CallMKTGet.bat","REM set resexe=%errorlevel%"
        			,"set resexe=1"});}
        
        else if(obj.getName().equalsIgnoreCase("C2_TEMPLATE_MKT_SENDFILES")){
            data.setLines(new String[] {"call e:\\data\\%c2_ENV%\\scripts\\CallSendFiles.bat","set resexe=%errorlevel%"});}
        
        else if(obj.getName().equalsIgnoreCase("C2_TEMPLATE_MKT_PRETXN")){
            data.setLines(new String[] {"call e:\\data\\%c2_ENV%\\scripts\\CallPreTXN.bat","set resexe=%errorlevel%"});}

        else if(obj.getName().equalsIgnoreCase("C2_TEMPLATE_MKT_PUT")){
            data.setLines(new String[] {"call e:\\data\\%c2_ENV%\\scripts\\CallMKTPut.bat","set resexe=%errorlevel%"});}
        
        else if(obj.getName().equalsIgnoreCase("C2_TEMPLATE_MKT_GET")){
            data.setLines(new String[] {"call e:\\data\\%c2_ENV%\\scripts\\CallMKTGet.bat","set resexe=%errorlevel%"});}
        
        else if(obj.getName().equalsIgnoreCase("C2_TEMPLATE_MKT_MOVEFILES")){
            data.setLines(new String[] {"call e:\\data\\%c2_ENV%\\scripts\\CallMoveFiles.bat","set resexe=%errorlevel%"});}
        
        else if(obj.getName().equalsIgnoreCase("C2_TEMPLATE_MKT_DELCSV")){
            data.setLines(new String[] {"call e:\\data\\%c2_ENV%\\scripts\\CallMKTDelCSV.bat","set resexe=%errorlevel%"});
            }
        
        else 
        {
            data.setLines(new String[] {"set resexe=0"});
        }
        
        obj.setInternalScript(data);
        obj.setSpecificData(data);
        data.save();
       // obj.update();
       
    }
    
    

	public void deleteSession(String sessionName) {				
		try {
			SessionId sessionId = new SessionId(sessionName, defaultVersion);
			Session sess = new Session(getContext(), sessionId);

			sess.setImpl(new OwlsSessionImpl());
			sess.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
			sess.delete();
			System.out.println("SESSION : "+sess.getName()+" deleted");
		} catch (ObjectNotFoundException e) {
			//ignore
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteUproc(String uprname)
	{
		try {
			UprocId uprId = new UprocId(uprname, defaultVersion);
			Uproc upr = new Uproc(getContext(), uprId);

			upr.setImpl(new OwlsUprocImpl());
			upr.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
			upr.delete();
			
		} catch (ObjectNotFoundException e) {
			//ignore
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void createSession(String sessionName, SessionModel sm, String []templateUproc) throws Exception {
		
		SessionId sessionId = new SessionId(sessionName, defaultVersion);
		Session sess = new Session(getContext(), sessionId);		
		sess.setImpl(new OwlsSessionImpl());
		sess.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
		
		if (sm.label!=null)
			sess.setLabel(sm.label);
		
		String header="HEADER_UPROC";
		
		SessionAtom root = new SessionAtom(new SessionData(header));
		
		ArrayList<String> processed = new ArrayList<String>();		
		addSessionChildren(sessionName, header, root, sm, processed);
		
		sess.setTree(new SessionTree(root));
		
		

		sess.create();
		System.out.println("SESSION : "+sess.getName()+" created");
		
	}
public void createSession(String sessionName, HashMap<String,ArrayList<String>> father_sons) throws Exception {
		
		SessionId sessionId = new SessionId(sessionName, defaultVersion);
		Session sess = new Session(getContext(), sessionId);		
		sess.setImpl(new OwlsSessionImpl());
		sess.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
		
		
		HashMap<String,SessionAtom> atoms = new HashMap<String,SessionAtom>();//to store all atoms of the session
		
		for(String key:father_sons.keySet())
		{
			SessionData nodeData = new SessionData(key);
			SessionAtom atom     = new SessionAtom(nodeData);
			
			if(!atoms.containsKey(key))
			{
				atoms.put(key,atom);
			}
			
			ArrayList<String>currentChildren = new ArrayList<String>(father_sons.get(key));
			
			//System.out.println(key+" has children "+currentChildren);
			for(int c=0;c<currentChildren.size();c++)
			{
				if(!currentChildren.get(c).equalsIgnoreCase("NULL"))
				{
					SessionData childNodeData = new SessionData(currentChildren.get(c));
					SessionAtom childAtom = new SessionAtom(childNodeData);
					
					if(!atoms.containsKey(currentChildren.get(c)))
					{
						atoms.put(currentChildren.get(c), childAtom);
					}
				}
			}
		
		}// we have declared all sessionAtoms of the session	
		
	


		for(String fatherKey:father_sons.keySet())
		{
			SessionAtom atom = atoms.get(fatherKey);
			
			ArrayList<String>currentChildren = new ArrayList<String>(father_sons.get(fatherKey));
			
			SessionAtom previousSibling=null;

			
			for(int c=0;c<currentChildren.size();c++)
			{
				if(!currentChildren.get(c).equalsIgnoreCase("NULL"))
				{
					SessionAtom childAtom = atoms.get(currentChildren.get(c));
					
					if(previousSibling==null)
					{
						if(atom.getChildOk()==null && childAtom.getParent()==null)
						{
							atom.setChildOk(childAtom);
							childAtom.setParent(atom);

						}
					}
					
					else
					{
						if(childAtom.getPreviousSibling()==null && previousSibling.getNextSibling()==null)
						{
							childAtom.setPreviousSibling(previousSibling);
							previousSibling.setNextSibling(childAtom);

						}
						
					}
					
					previousSibling=childAtom;
				}
			}
		}
		
	/*	for(String k:atoms.keySet())
		{
			if(atoms.get(k).getChildOk()!=null)
			{
				System.out.println(k+" has child "+atoms.get(k).getChildOk().getData().getUprocName());
			}
			else
			{
				System.out.println(k+" has no child ");

			}
		}*/
		
		String header="HEADER_UPROC";
		
		SessionAtom root = new SessionAtom(new SessionData(header));
		
		SessionAtom lastFatherLess=null;
		
		for(String fatherKey:father_sons.keySet())
		{
			SessionAtom father = atoms.get(fatherKey);
			
			if(father.getParent()==null)
			{

				if(lastFatherLess== null)
				{
					if(root.getChildOk()==null && father.getParent()==null) 
					{
						root.setChildOk(father);
						father.setParent(root);

					}
					
				}
				else
				{
					if(lastFatherLess.getPreviousSibling()==null && father.getNextSibling()==null)
					{
						lastFatherLess.setPreviousSibling(father);
						father.setNextSibling(lastFatherLess);

					}
					
				}
				lastFatherLess=father;


			}

		
		}
		
		sess.setTree(new SessionTree(root));
		
	
	

		sess.create();
		System.out.println("SESSION : "+sess.getName()+" created");
		
	}
	
	
	
//////////////////////////////////////////////////////////////////////////////////////////////////	
   @SuppressWarnings("unused")
private MuControl getMuControl_branch (ArrayList<SessionAtom> trailer_to_header_branch)
   {//this method returns the last execution context in a branch
	   
	   MuControl default_MuControl = new MuControl();// default 
	   default_MuControl.setType (Type.SAME_MU);
	   default_MuControl.setOneEnough (Boolean.TRUE);
	   
	   for(int y=0;y<trailer_to_header_branch.size();y++)
		{
		   if(!trailer_to_header_branch.get(y).getData().getExecutionContext().getType().equals(ExecutionContext.Type.SAME))
		   {
			   if(trailer_to_header_branch.get(y).getData().getExecutionContext().getType().equals(ExecutionContext.Type.HDP))
			   {
				   
				   if(!trailer_to_header_branch.get(y).getData().getExecutionContext().getHDP().toString().equals("{ C}"))
				   {
					   
					   default_MuControl.setType(Type.HDP);
				   default_MuControl.setHdp(trailer_to_header_branch.get(y).getData().getExecutionContext().getHDP().toString().replace("{", "").replace("}", ""));
				   default_MuControl.setOneEnough (Boolean.TRUE);
				   
				   return default_MuControl;
				   }
				   else
				   {
					     return default_MuControl;
					   
				   }
				   
				   
			   }
			   
			   if(trailer_to_header_branch.get(y).getData().getExecutionContext().getType().equals(ExecutionContext.Type.MU))
			   {
				   default_MuControl.setType(Type.SPECIFIC_MU);
				   default_MuControl.setMu(trailer_to_header_branch.get(y).getData().getExecutionContext().getMuName());
				   default_MuControl.setOneEnough (Boolean.TRUE);

				   return default_MuControl;
			   }
		   }
		   else continue;
		}
	   
	   return default_MuControl;
   }
   //////////////////////////////////////////////////////////////////////////////////////
   
   public void updateSessionAtom(String sessname,String currentUprName , String newUprName)
   {
   	try {					    		   		
   	            Session obj = this.getSession(sessname);

   	    		SessionTree tree = obj.getTree();
   	    		
   	    		final String curUpr = currentUprName;
   	    		final String newUpr = newUprName;
   	    			 
   	    		tree.scan(new AtomVisitor() 
   	    			{
   	    				public void handle(SessionAtom atom) 
   	    				{
   	    					updateName(atom,curUpr,newUpr);
   	    				}
   	    			});
   	    			
   	    		if(this.uprocAlreadyExists(currentUprName))
   	    		{
   	    			if(!this.uprocAlreadyExists(newUprName))
   	    			{
   	    				this.duplicateUproc(currentUprName, newUprName);
   	    			}
   	    			
   	   	    		this.deleteUproc(currentUprName);
   	    		}
   	    
   	    		obj.update();
   	    	   	    		
   	    		System.out.println("RENAMING FROM ["+currentUprName+"] to ["+newUprName+"] in ["+sessname+"] DONE --> OK");     

   	} catch (Exception e) {
   		e.printStackTrace();
   	}	
   	
   }
   
   
   public void createMissingOptionalTask (String mtskName,String uprocName,String rule) throws Exception {
       try {
       	
       	String musnippet;
    	
   		String oTskName = uprocName+"_OPT_TASK";

		if (oTskName.length() > TSKNAME_LIMIT) 
		{

			oTskName=oTskName.substring(TSKNAME_LIMIT);
		}

       	if(!tsks.containsKey(mtskName))
       	{
				System.out.println("Skipping [OPT TSK] "
								+ oTskName
								+ " on TARGET ["
								+ this.getConnName()
								+ "] : [MAIN TSK]"+mtskName+" does not exist");
				return;
       	}
       	else
       	{
       		
				
       		musnippet=tsks.get(mtskName).getMuName();
       	
			
       	}// limit the label length to 64 characters

		if (this.taskAlreadyExists(oTskName)) {
				return;
			}

			if (this.taskAlreadyExists(mtskName)
					&& NamingUtils.doesTaskGoOnNode(musnippet, this)) {
				
				Task main = tsks.get(mtskName);

							
				if(main.getTaskType().equals(TaskType.Provoked))
				{
					System.out.println("[MAIN TSK] " + mtskName
							+ " is PROVOKED. Skipping [OPT TSK] "
							+ oTskName + " on " + this.getConnName()
							+". [MAIN TSK] "+mtskName+" needs to be SCHEDULED with RULE ["+rule+"]****");

					return;
				}

				Task obj = new Task(getContext(), TaskId.createWithName(
						oTskName, main.getIdentifier().getVersion(),
						musnippet, main.isTemplate()));
				obj.getIdentifier().setSyntaxRules(
						OwlsSyntaxRules.getInstance());

				/* optional task */
				TaskPlanifiedData tpd = new TaskPlanifiedData();
				tpd.setGenerateEvent(true);
				tpd.setOptional(true);

				TaskPlanifiedData main_tpd = main.getPlanifiedData();

				if (main_tpd == null) {
					System.out.println("[MAIN TSK] " + mtskName
							+ " has WRONG scheduling data. Skipping [OPT TSK] "
							+ oTskName + " on " + this.getConnName());

					return;
				}

				tpd.setLaunchHourPatterns(main_tpd.getLaunchHourPatterns());

				Rule rule1;

				if (!this.ruleAlreadyExists(rule)) {
					this.createRule(rule);
				}

				
				
				
				
				rule1 = this.getRule(rule);


				//////
				
				TaskImplicitData tid1 = new TaskImplicitData (rule1.getIdentifier ());
		        
		        tid1.setFunctionalVersion(rule1.getFunctionalVersion());
		        tid1.setLabel(rule1.getLabel());
		        tid1.setMonthAuthorization(rule1.getMonthAuthorization());
		        tid1.setWeekAuthorization(rule1.getWeekAuthorization());  
		        
		        tid1.setPeriodType (rule1.getPeriodType ());
		        tid1.setPeriodNumber(rule1.getPeriodNumber());
		        tid1.setPattern (rule1.getPattern ());
		        tid1.setAuthorized (true);
		        final Date date1 = DateTools.toDate ("20140101");
		        tid1.setReferenceDate(DateTools.getYYYYMMDD(date1));
		        Calendar calendar1 = DateTools.getCalendarInstance();
		        calendar1.setTime(date1);
		        Integer weekNumber1 = calendar1.get(Calendar.WEEK_OF_YEAR);
		        tid1.setApplicationWeek(weekNumber1.toString());
		        
		        tid1.setLabel(rule1.getLabel());
		        tid1.setInternal(true);
		      
		        TaskImplicitData[] implicitDataArray = new TaskImplicitData[] {tid1};
		        tpd.setImplicitData (implicitDataArray);
		        
		        obj.setSpecificData(tpd);
				//////

				Uproc currentUpr=null;
				
				if(uprs.containsKey(uprocName)){

					currentUpr = uprs.get(uprocName);
					
				}
				else
				{
					System.out.println("Error : [OPT TSK] can not be created for "+uprocName);
					return;
				}
				
				
				
				// uproc info
				//obj.setUprocId(currentUpr.getIdentifier().toString());
				obj.setUprocName(currentUpr.getIdentifier().getName());
				obj.getIdentifier().setUprocVersion(currentUpr.getIdentifier().getVersion());

				// MU info
				// obj.setMuId(currentMainTask.getMuId());
				obj.setMuName(musnippet);

				// Session info
				// obj.setSessionId(currentSession.getId());
				obj.setSessionName(main.getSessionName());
				obj.getIdentifier().setSessionVersion(main.getSessionVersion());

				// Task info
				obj.setTaskType(TaskType.Optional);
				obj.setAutoRestart(false);
				obj.setEndExecutionForced(false);
				obj.setCentral(main.isCentral());
				obj.setActive(main.isActive());
				obj.setUserName(main.getUserName());
				obj.setLabel("Optional task");
				obj.setPriority(main.getPriority());
				obj.setQueue(main.getQueue());
				obj.setFunctionalPeriod(main.getFunctionalPeriod());
				// obj.setUserId(currentMainTask.getUserId());
				obj.setParallelLaunch(false);

				obj.setTypeDayOffset(main.getTypeDayOffset());
				obj.setPrinter(main.getPrinter());

				obj.setVariables(main.getVariables());
				obj.setPrinter(main.getPrinter());

				obj.setDayOffset(main.getDayOffset());
				obj.setUnitOffset(main.getUnitOffset());
				obj.setSimulated(main.isSimulated());
				obj.setValidFrom(main.getValidFrom());
				obj.setValidTo(main.getValidTo());

				obj.setDeploy(false);
				obj.setUpdate(false);
				obj.setInteractiveFlag(main.getInteractive());
				obj.setDeployDate(main.getDeployDate());
				obj.setDuration(main.getDuration());
				obj.setStatInfo(main.getStatInfo());
				obj.setAutoPurgeLevels(main.getAutoPurgeLevels());
				obj.setLastRun(main.getLastRun());

				obj.setUprocHeader(false);
				// obj.setOriginNode(currentMainTask.getOriginNode());
				obj.setFlagAdvance(main.isFlagAdvance());
				obj.setAdvanceDays(main.getAdvanceDays());
				obj.setAdvanceHours(main.getAdvanceHours());
				obj.setAdvanceMinutes(main.getAdvanceMinutes());
				obj.setMuTZOffset(main.getMuTZOffset());

				obj.setParentTaskMu(main.getMuName());
				obj.setParentTaskName(main.getIdentifier().getName());
				obj.setParentTaskVersion(main.getParentTaskVersion());
				obj.setParentTaskMuNode(main.getParentTaskMuNode());
				obj.setTimeLimit(main.getTimeLimit());

				obj.setImpl(new OwlsTaskImpl());
				System.out.print("[OPT TSK] " + obj.getIdentifier().getName()
						+ " about to be created on [" + this.getConnName()
						+ "] : ");
				obj.create();
				System.out.println("[OPT TSK]  [" + oTskName
						+ "] ON NODE=[" + this.getConnName() + "] ---> OK");

			} else {

				System.out.println();
				System.out
						.println("Skipping [OPT TSK] "
								+ oTskName
								+ " on TARGET ["
								+ this.getConnName()
								+ "] : Missing [MAIN TSK] and/or Naming Convention not respected");

			}
       	
       	
       } catch (SyntaxException e) {
           e.printStackTrace (System.out);
       } catch (UniverseException e) {
           e.printStackTrace (System.out);
       }
   }
   
   
   protected  void insertTechUproc(SessionAtom atom,String currentUprName,String nameOfTechUproc)
   {
	   	if (atom.getData()!=null ) 
	   	{
	           
	   				if (atom.getData().getUprocName().equalsIgnoreCase(currentUprName))
	   				{
	   					try {
	   						 
	   							SessionAtom techUproc = new SessionAtom(new SessionData(nameOfTechUproc));
	   							SessionAtom currentParent = atom.getParent();
	   							SessionAtom currentNextSibling = atom.getNextSibling();
	   							SessionAtom currentPreviousSibling=atom.getPreviousSibling();
	   							atom.setParent(techUproc);
	   							currentParent.setChildOk(techUproc);
	   							techUproc.setChildOk(atom);
	   							techUproc.setNextSibling(atom.getNextSibling());//techUproc.setNextSibling(atom.getNextSibling());
	   							techUproc.setPreviousSibling(atom.getPreviousSibling());
	   							if(currentPreviousSibling !=null)
	   							{
	   								currentPreviousSibling.setNextSibling(techUproc);
	   							}
	   							if(currentNextSibling!=null)
	   							{
		   							currentNextSibling.setPreviousSibling(techUproc);

	   							}
	   							atom.setNextSibling(null);
	   							atom.setPreviousSibling(null);
	   							
	   						}
	   					catch (Exception e) {
	   						e.printStackTrace();
	   					}
	   				}
	   	}
   }
   protected  void updateName(SessionAtom atom,String currentUprName,String newUprName)
   {
	   	if (atom.getData()!=null && !currentUprName.trim().isEmpty() && !newUprName.trim().isEmpty()) 
	   	{
	           
	   				if (atom.getData().getUprocName().equalsIgnoreCase(currentUprName))
	   				{
	   					try {
	   						 
	   							
	   							atom.getData().setUprocName(newUprName);
	   							
	   						}
	   					catch (Exception e) {
	   						e.printStackTrace();
	   					}
	   				}
	   	}
   }
   protected  void updateSessionAtomName(SessionAtom atom,HashMap<String,String>old_new)
   {//developed for StateOfDE
	   	if (atom.getData()!=null)
	   	{
	           
	   				if (old_new.containsKey(atom.getData().getUprocName()))
	   				{
	   					try {
	   						 
	   							
	   							atom.getData().setUprocName(old_new.get(atom.getData().getUprocName()));
	   							
	   						}
	   					catch (Exception e) {
	   						e.printStackTrace();
	   					}
	   				}
	   				
	   				
	   	}
   }
   public  String getHeaderNameFromUprName(String uprName) throws Exception
   {
	   if(uprName.contains("-"))
	   {
		   Session mySes = this.getSession(uprName.substring(uprName.indexOf("-")+1));
		 
		   return mySes.getHeader();
	   }
	   else return null;
 		   
   }
   public String getMainTaskNameFromOptTsk(String optionaltaskName) throws Exception
   {
	String[] tks = optionaltaskName.split(TASKNAMESPLITTER);
		
		if (tks.length==3)//optional task case
        {
	       	   return this.getHeaderNameFromUprName(tks[0]+"-"+tks[1])+"-"+tks[2].replace("MKT", "CIS");//needed because MKT specific tasks are linked to CIS main tasks
        }
        else 
        {
     	   	  return null;
	        	
        }
   }
   
	////////////////////////////////	
	
	private void addSessionChildren(String sessionName, String parentName, SessionAtom parentAtom, SessionModel sm, ArrayList<String> processed) throws Exception {
		SessionAtom lastAtomOk = null;
		
		Collection<String> list = sm.getChildren(parentName);
		if (list!=null)
			for (String child : list) {			
				if (processed.contains(child))
					continue;
				else
					processed.add(child);
				SessionAtom atom = null;
				if (!sm.getUprs().contains(child)) {
					System.out.println("WARNING: skipping - uproc " + parentName + " child " + child + " not found in session");
					continue;
				}

				
				atom = new SessionAtom(new SessionData(child));
	
				if (lastAtomOk == null)
					parentAtom.setChildOk(atom);
				else
					lastAtomOk.setNextSibling(atom);
				lastAtomOk = atom;
	
				addSessionChildren(sessionName, child, atom, sm, processed);
			}
	}
	
	
	
    protected ItemList<TaskItem> initList(String TaskNameFilter1,String SessionNameFilter,String muNameFilter) {
        /* pattern for task name  */

        String name =TaskNameFilter1;
        String muName =muNameFilter;
        String userName = "*";
        String label = "*";
        String uprocName = "*"; 
        String sessionName =SessionNameFilter;
        String queue = "*";
        String version = "*";
        Boolean template = null;
        TaskType type = null;
        
        TaskFilter filter = new TaskFilter(name, muName, userName, label,uprocName, sessionName, queue);
        //System.out.println("Pulling list of TASKS with NAME="+name+" MU="+muName+" USERNAME="+userName+" LABEL="+label+" UPROCNAME="+uprocName+" SESSION="+sessionName+" QUEUE="+queue);
        //System.out.println();
        filter.setVersion(version);
        filter.setTemplate(template);
        filter.setType(type);
        
        filter.setParentTaskMu("*");
        filter.setParentTask("*");
        TaskList list;
        try {
        list = new TaskList(getContext(), filter);
        list.setContext(getContext());
        list.setImpl(new OwlsTaskListImpl());
        list.extract();
        return list;
        
    } catch (SyntaxException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (UniverseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

    return null;
    }

    public static EOwlsTaskType convertToMsgType(TaskType type) {
        EOwlsTaskType returnEOwlsTaskType = null;

        if (type != null) {
            if (TaskType.Scheduled.getCode() == type.getCode()) {
                returnEOwlsTaskType = EOwlsTaskType.TASK_TYPE_SCHEDULED;
            } else if (TaskType.Provoked.getCode() == type.getCode()) {
                returnEOwlsTaskType = EOwlsTaskType.TASK_TYPE_PROVOKED;
            } else if (TaskType.Optional.getCode() == type.getCode()) {
                returnEOwlsTaskType = EOwlsTaskType.TASK_TYPE_OPTIONAL;
            } else if (TaskType.Cyclic.getCode() == type.getCode()) {
                returnEOwlsTaskType = EOwlsTaskType.TASK_TYPE_CYCLICAL;
            }
        }

        return returnEOwlsTaskType;
    }
    public static EOwlsTaskStatus retrieveTaskStatus(TaskItem obj) {
        EOwlsTaskStatus returnEOwlsTaskType = null;

        if (obj != null) {
            if (obj.isSimulated()) {
                returnEOwlsTaskType = EOwlsTaskStatus.TASK_STATUS_SIMULATED;
            } else if (obj.isActive()) {
                returnEOwlsTaskType = EOwlsTaskStatus.TASK_STATUS_ACTIVE;
            } else {
                returnEOwlsTaskType = EOwlsTaskStatus.TASK_STATUS_DISABLED;
            }
        }

        return returnEOwlsTaskType;
    }
    
    public void deploy_to_MU(Task obj) throws UniverseException, Exception
    {
    	if(obj.isTemplate() && (this.getTaskByName(obj.getIdentifier().getName(),true) != null))
    	{
		    	if(this.getTaskByName(obj.getIdentifier().getName(),false)==null)
		    	{// if non template version does not exists , create it
			        Task obj_todeploy = new Task (getContext (), obj.getIdentifier());
			        obj_todeploy.getIdentifier ().setSyntaxRules (OwlsSyntaxRules.getInstance ());
			        obj_todeploy.populate(obj);
			        obj_todeploy.setActive(true);
			        obj_todeploy.getIdentifier().setTemplate(false);
			        obj_todeploy.create();
	    			System.out.println("TASK = ["+obj.getIdentifier().getName()+"] has been deployed to MU = ["+obj.getIdentifier().getMuName()+" on [TARGET] "+this.name+" ---> OK");
		    	} 
		    	else
		    	{
		    		
		    			System.out.println("TASK = ["+obj.getIdentifier().getName()+"] already exists on TARGET= ["+this.name+"] in NON-TEMPLATE format ...Skipping");
		
		    		
		    	}
    	}
    	else
    	{
    		System.out.println("[TASK] "+obj.getIdentifier().getName()+" does not exist on TARGET ["+this.name+"] ...Skipping");
    		
    				
    	}
    }
    public void createTask(Task obj) throws Exception
    {
    	if(!this.tskAlreadyExists(obj.getIdentifier().getName()))
    	{
        Task obj_tocreate = new Task (getContext (), obj.getIdentifier());
        obj_tocreate.getIdentifier ().setSyntaxRules (OwlsSyntaxRules.getInstance ());
        obj_tocreate.populate(obj);
        obj_tocreate.create();
    	}
    	else
    	{
    		System.out.println("[TASK] "+obj.getIdentifier().getName()+" already exists on TARGET  ["+this.name+"]...Skipping");
    	}
    	
    }
    
    
    

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public TaskList getTaskList() throws Exception {
		return getTaskListWithFilter(new TaskFilter());
	}
    public ArrayList<Task> getTaskArrayList() throws Exception {
  		TaskList list = getTaskListWithFilter(new TaskFilter());
  		ArrayList<Task>listTasks= new ArrayList<Task>();
  		
  		for(int i=0;i<list.getCount();i++)
  		{
  			TaskId id = list.get(i).getIdentifier();
    		Task obj = new Task(getContext(),id);
    		obj.setImpl(new OwlsTaskImpl());
			obj.extract();
			listTasks.add(obj);
  		}
  		return listTasks;
  	}
    public HashMap<String,Task> getTaskHashMap() throws Exception {
  		TaskList list = getTaskListWithFilter(new TaskFilter());
  		HashMap<String,Task>listTasks= new HashMap<String,Task>();
  		
  		for(int i=0;i<list.getCount();i++)
  		{
  			TaskId id = list.get(i).getIdentifier();
    		Task obj = new Task(getContext(),id);
    		obj.setImpl(new OwlsTaskImpl());
			obj.extract();
			listTasks.put(obj.getIdentifier().getName(),obj);
  		}
  		return listTasks;
  	}
    public Multimap<String, Task> getTaskMultiMap(String tskFilter) throws Exception {
  		
    	
    	 String name = "*";
        
    	 if(!tskFilter.isEmpty())
    	 {
    		 name="*"+tskFilter+"*";
    	 }
    	 String muName = "*";
         String userName = "*";
         String label = "*";
         String uprocName = "*"; 
         String sessionName = "*";
         String queue = "*";
         String version = "*";
         Boolean template = null;
         TaskType type = null;
         
         TaskFilter filter = new TaskFilter(name, muName, userName, label,
                 uprocName, sessionName, queue);
         filter.setVersion(version);
         filter.setTemplate(template);
         filter.setType(type);
         
         filter.setParentTaskMu("*");
         filter.setParentTask("*");
    	
    	
    	TaskList list = getTaskListWithFilter(filter);

  		Multimap<String, Task> listTasks = ArrayListMultimap.create();
  		
  		for(int i=0;i<list.getCount();i++)
  		{
  			TaskId id = list.get(i).getIdentifier();
    		Task obj = new Task(getContext(),id);
    		obj.setImpl(new OwlsTaskImpl());
			obj.extract();
			
			
			listTasks.put(obj.getIdentifier().getName(),obj);
  		}
  		return listTasks;
  	}
    
    public Multimap<String,Task> getTaskMultiMap_from_outside() throws Exception {
  		return tsks_multimap;
  	}
    public HashMap<String,Task> getTaskHashMap_from_outside() throws Exception {
  		return tsks;
  	}
    public TaskList getTaskListWithFilter(TaskFilter tf) throws Exception {
		TaskList list = new TaskList(getContext(), tf);
		list.setImpl(new OwlsTaskListImpl());
		list.extract();
		return list;
	}
    
      public Task getTaskByName(String name,boolean isTemplate) throws Exception {
	
    	TaskList list = getTaskList();
		for(int i=0;i<list.getCount();i++){
			TaskItem ent = list.get(i);
			ent.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
			if(isTemplate){
				if(ent.getIdentifier().getName().equalsIgnoreCase(name) && ent.isTemplate()){
					TaskId id = ent.getIdentifier();
					Task u = new Task(getContext(), id);
					u.setImpl(new OwlsTaskImpl());
					u.extract();
					return u;
				}
			}else{
				if(ent.getIdentifier().getName().equalsIgnoreCase(name) && !ent.isTemplate()){
					TaskId id = ent.getIdentifier();
					Task u = new Task(getContext(), id);
					u.setImpl(new OwlsTaskImpl());
					u.extract();
					return u;
			}
		}
		}
		return null;
	}
    
    public void deleteTask(String taskname,String taskversion,String taskmu,boolean isTemplate) {

        try {
            String name = taskname;
            String version = taskversion;
            String muName = taskmu;

            
            Task obj = new Task(getContext(), TaskId.createWithName(name, version, muName, isTemplate));
            obj.getIdentifier ().setSyntaxRules (OwlsSyntaxRules.getInstance ());
            
            obj.setImpl(new OwlsTaskImpl());
            obj.delete();
            
        } catch (SyntaxException e) {
            e.printStackTrace(System.out);
        } catch (UniverseException e) {
            e.printStackTrace(System.out);
        }
    }
    
    
   
    
    
    public void transferTask(Task obj,Area destArea) {

        try {

            obj.getIdentifier ().setSyntaxRules (OwlsSyntaxRules.getInstance ());
            obj.setImpl(new OwlsTaskImpl());
            obj.transfer(destArea);
            System.out.println("[TASK] "+obj.getIdentifier().getName()+" has been transferred to "+destArea.toString());
            
        } catch (SyntaxException e) {
            e.printStackTrace(System.out);
        } catch (UniverseException e) {
            e.printStackTrace(System.out);
        }
    }
    
    
    public ItemList<RuleItem> getRuleList(String rulename) throws UniverseException
    {
    	String name ;
        String label = "*";
    	 
    	        /* pattern for rule name  */
    	        if(rulename==null || rulename.trim().isEmpty() || rulename.trim().equals("") || rulename.trim().equals("*"))
    	        {
    	        	name = "*";
    	        	
    	        }
    	        else
    	        {
    	        	name="*"+rulename+"*";
    	        }
    	        
    	        RuleFilter filter = new RuleFilter(name,label);

    	        RuleList list = new RuleList(getContext(), filter);
    	        list.setImpl(new OwlsRuleListImpl());
    	        list.extract();
    	        return list;
    	    
    }
    public Rule getRule(String rulename) throws UniverseException
    {
    	
    	Rule obj = new Rule(getContext(),rulename);
        obj.setImpl(new OwlsRuleImpl());
        obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance ());
        obj.extract();
        return obj;
    }

    public void updateRuleOnOptionalTask(Task obj,String rule) throws UniverseException{
    	
    	String oldRule="";
    	Task currentTaskObj = new Task(getContext(), obj.getIdentifier());
    	currentTaskObj.setImpl(new OwlsTaskImpl());
        
    	currentTaskObj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance ());
    	currentTaskObj.extract();

    	if(this.getRuleList(rule).getCount() == 0)
    	{
    		System.out.println("RULE = ["+rule+"] does not exists on "+this.getConnName()+" ... Creating it first...");
    		this.createRule(rule);
    	}
    	
    	Rule currentRuleObj= this.getRule(rule);

    	TaskPlanifiedData taskPlanifiedData =(TaskPlanifiedData) currentTaskObj.getSpecificData();
    	taskPlanifiedData.setGenerateEvent(true);
    	
        if (taskPlanifiedData.getImplicitData() != null && currentTaskObj.getTaskType().equals(TaskType.Optional)) 
        {
            
            for (int i = 0; i < taskPlanifiedData.getImplicitData().length; i++) 
            {
            	if(i==0)
            	{
            		oldRule=taskPlanifiedData.getImplicitData()[i].getName();
            		
            		if(!oldRule.equals(currentRuleObj.getName()))
            		{
            			taskPlanifiedData.getImplicitData()[i].setName(currentRuleObj.getName());
            			System.out.println("RULE updated on TASK =["+currentTaskObj.getIdentifier().getName()+"] from OLD VALUE ["+oldRule+"] to NEW VALUE ["+rule+"]");
            		}
            		else
            		{
            			continue;
            		}
	            	
	            	//taskPlanifiedData.getImplicitData()[i].update();
	                
            	}
            	else
            	{
            		taskPlanifiedData.getImplicitData()[i].delete();
            	}
              
            
            }
            currentTaskObj.setLabel("Optional task");
            currentTaskObj.setSpecificData (taskPlanifiedData);
            currentTaskObj.setImpl (new OwlsTaskImpl ());
            currentTaskObj.update ();
			System.out.println("TASK =["+currentTaskObj.getIdentifier().getName()+"] has been updated ---> [OK]");

        }
        else
        {
        	System.out.println("TASK = ["+currentTaskObj.getIdentifier().getName()+"] is being skipped : No rule or non-optional task...");
        }

    }
    public void cleanUp_Task(HashMap<String,String>onDemandDisabledSession) throws Exception
    {
    	TaskList list = this.getTaskList();
    	
    	for(int i=0;i<list.getCount();i++)
    	{
		    	Task currentObjTobeDisabled = new Task(getContext(), list.get(i).getIdentifier());
		           
		    	
		    		currentObjTobeDisabled.setImpl(new OwlsTaskImpl());
			    	currentObjTobeDisabled.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance ());
			    	currentObjTobeDisabled.extract();
			    	
			    	if(onDemandDisabledSession.containsKey(currentObjTobeDisabled.getSessionName()))
			    	{	
			    		currentObjTobeDisabled.setActive(false);
			    		currentObjTobeDisabled.update();
		    			
			    	}
		    	
		 }
	    	
    }
    
    
    public  void createRule(String rulename) throws UniverseException
    {
        
        String name = rulename;
        Rule obj = new Rule(getContext(), RuleId.create(name));
        
        String label = "THIS RULE NEEDS TO BE REVISED";
        obj.setLabel(label);

        obj.setPeriodNumber(1);
        obj.setPeriodType(PeriodTypeEnum.DAY);
        
        UniPattern pattern = new UniPattern();
        
        PositionsInPeriod positionsInPeriod = new PositionsInPeriod();
        positionsInPeriod.setPositionsPattern("1");
        positionsInPeriod.setForward(true);
        positionsInPeriod.setType(SubPeriodType.DAY);
        
        pattern.setPositionsInPeriod(positionsInPeriod);
        pattern.setRunOver(RunOverEnum.NO);//flag exit 'N'
        
        
        Hashtable<KDayType, KDayAuthorization> dayAuthorizations = new Hashtable<KDayType, KDayAuthorization>();

        KDayAuthorization openDay = new KDayAuthorization(KDayAuthorizationType.AUTHORIZED);
        KDayAuthorization closeDay = new KDayAuthorization(KDayAuthorizationType.AUTHORIZED);
        KDayAuthorization holiday = new KDayAuthorization(KDayAuthorizationType.AUTHORIZED);
        
        dayAuthorizations.put(KDayType.OPEN, openDay);
        dayAuthorizations.put(KDayType.CLOSED, closeDay);
        dayAuthorizations.put(KDayType.HOLIDAY, holiday);
        
        pattern.setDayAuthorizations(dayAuthorizations);
        
        MonthAuthorization monthAuthorization = new MonthAuthorization();
        monthAuthorization.setDirection(Direction.FROM_BEGINNING);
        for (int i = 0; i < 31; i++) {
            monthAuthorization.setAuthorization(i, true);
        }
        
        obj.setMonthAuthorization(monthAuthorization);
        
        YearAuthorization yearAuthorization = new YearAuthorization();
        for (int i = 0; i < 12; i++) {
            yearAuthorization.setAuthorization(i, true);
        }
        obj.setYearAuthorization(yearAuthorization);
        
        WeekAuthorization weekAuthorization = new WeekAuthorization();
        for (int i = 0; i < 7; i++) {
            weekAuthorization.setBlankDay(i, true);
        }
        for (int i = 0; i < 7; i++) {
            weekAuthorization.setClosedDay(i, true);
        }
        for (int i = 0; i < 7; i++) {
            weekAuthorization.setWorkedDay(i, true);
        }
        
        obj.setWeekAuthorization(weekAuthorization);
        
        com.orsyp.api.rule.Offset offset = new com.orsyp.api.rule.Offset();
        pattern.setOffset(offset);
       
        
        obj.setPattern(pattern);
        
        obj.setImpl(new OwlsRuleImpl());
       // obj.update();
        obj.create();
    }
    
    public String getRuleFromTask(Task obj)
    {
    	if(!obj.getTaskType().equals(TaskType.Provoked)){
    	
             TaskPlanifiedData taskPlanifiedData =(TaskPlanifiedData) obj.getSpecificData();
    
                
            if (taskPlanifiedData.getImplicitData() != null && taskPlanifiedData.getImplicitData().length>=1) 
            {
            
            	TaskImplicitData taskImplicitData = taskPlanifiedData.getImplicitData()[0];
                      
            	return taskImplicitData.getName().toUpperCase();
                      
            }
    	
            return null;
            
    	}
    	else
    	{
    		return "PROVOKED";
    	}
    	

    	
    }
    
    public String getLWFromTask(Task obj) throws UniverseException
    {
    	
                 TaskPlanifiedData taskPlanifiedData =(TaskPlanifiedData) obj.getSpecificData();
                
                 if (taskPlanifiedData.getLaunchHourPatterns() != null && taskPlanifiedData.getLaunchHourPatterns().length>=1) 
                 {
                	 	LaunchHourPattern curLp = taskPlanifiedData.getLaunchHourPatterns()[0];

                	 	String freq=Integer.toString(curLp.getFrequency());
                	 	String durH=Integer.toString(curLp.getDurationHour());
                	 	String durM=Integer.toString(curLp.getDurationMinute());
                	 	
                	 	String endTime=curLp.getEndTime();
                	 	String startTime= curLp.getStartTime().trim().substring(0,(curLp.getStartTime().trim().length()-2));
                	 	
                	 	if(curLp.getFrequency()<=99 && curLp.getFrequency()>=10 )
                	 	{
                	 		freq="0"+freq;
                	 	}
                	 	if(curLp.getFrequency()<10)
                	 	{
                	 		freq="00"+freq;
                	 	}
                	 	
                	 	if(curLp.getDurationHour()<=99 && curLp.getDurationHour()>=10)
                	 	{
                	 		durH="0"+durH;
                	 		
                	 	}
                	 	if(curLp.getDurationHour()<10)
                	 	{
                	 		durH="00"+durH;
                	 	}
                	 	
                	 
                	 	if(curLp.getDurationMinute()<10)
                	 	{
                	 		durM="0"+durM;
                	 	}
                	 	
                	 	
                	 	if(endTime==null)
                	 	{
                	 		endTime="0000";
                	 	}
                	 	if(startTime==null)
                	 	{
                	 		startTime="0000";
                	 	}
                	 	
                	 	return startTime+";"+endTime.substring(0,4)+";"+freq+";"+durH+";"+durM;
          
                 } 
                 else return null;
    	}
    	
    public ItemList<JobEventItem> getJobEventList() throws UniverseException {

        JobEventFilter filter = new JobEventFilter();

        JobEventList list = new JobEventList(getContext(), filter);
        list.setImpl(new OwlsJobEventListImpl());
        list.extract();
        return list;
    }
    public ArrayList<JobEvent> getJobEventsArrayList() throws UniverseException {

    	ArrayList<JobEvent> result = new ArrayList<JobEvent>();
        JobEventFilter filter = new JobEventFilter();

        JobEventList list = new JobEventList(getContext(), filter);
        list.setImpl(new OwlsJobEventListImpl());
        list.extract();
    	
        for(int i=0;i<list.getCount();i++)
    	{
        	JobEventItem obj_fromList = list.get(i);
        	JobEvent obj = new JobEvent(getContext(),obj_fromList.getIdentifier());

        	obj.setImpl(new OwlsJobEventImpl());
        	obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());

        	obj.extract();

        	result.add(obj);
    	}
    	
    	return result;
    }
    public void createJobEvents_fromList(ItemList<JobEventItem> jobeventList) throws UniverseException
    {
    	for(int i=0;i<jobeventList.getCount();i++)
    	{
    		JobEvent obj_fromList = new JobEvent(getContext(),jobeventList.get(i).getIdentifier());
    		obj_fromList.extract();
    		
    		
                
    		obj_fromList.setImpl(new OwlsJobEventImpl());
    		obj_fromList.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance ());
    		obj_fromList.create();
    	}
    	
    }
    
public UprocList getUprocList() throws Exception {  
		
        UprocFilter filter = new UprocFilter("*");
        UprocList list = new UprocList(getContext(), filter);
        
        list.setSyntaxRules(OwlsSyntaxRules.getInstance());
        OwlsUprocListImpl impl = new OwlsUprocListImpl();
        impl.init(list, Operation.DISPLAYLIST);
        list.setImpl(impl);
       // list.setImpl(new OwlsUprocListImpl());
        list.extract();
        return list;
	}

public ArrayList<Uproc> getUprocArrayList() throws Exception {  
	
    UprocFilter filter = new UprocFilter("*");
    UprocList list = new UprocList(getContext(), filter);
    
    list.setSyntaxRules(OwlsSyntaxRules.getInstance());
    OwlsUprocListImpl impl = new OwlsUprocListImpl();
    impl.init(list, Operation.DISPLAYLIST);
    list.setImpl(impl);
   // list.setImpl(new OwlsUprocListImpl());
    list.extract();
  
    
 ArrayList<Uproc> uprocs= new ArrayList<Uproc>();
    
    for(int i=0;i<list.getCount();i++)
    {
    	UprocId id = list.get(i).getIdentifier();
    	Uproc obj = new Uproc(getContext(),id);
    	obj.setImpl(new OwlsUprocImpl());
    	obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
        obj.extract();
        uprocs.add(obj);
    	
    }
    return uprocs;
}
public HashMap<String,Uproc> getUprocHashMap(String uprfilter) throws Exception {  
	
    UprocFilter filter ;
    if(!uprfilter.isEmpty())
    {
    	filter = new UprocFilter("*"+uprfilter+"*");
    }
    else
    {
    	filter = new UprocFilter("*");
    }
    UprocList list = new UprocList(getContext(), filter);
    
    list.setSyntaxRules(OwlsSyntaxRules.getInstance());
    OwlsUprocListImpl impl = new OwlsUprocListImpl();
    impl.init(list, Operation.DISPLAYLIST);
    list.setImpl(impl);
   // list.setImpl(new OwlsUprocListImpl());
    list.extract();
  
    
 HashMap<String,Uproc> uprocs= new HashMap<String,Uproc>();
    
    for(int i=0;i<list.getCount();i++)
    {
    	UprocId id = list.get(i).getIdentifier();//list.get(i).getId()
    	Uproc obj = new Uproc(getContext(),id);
    	obj.setImpl(new OwlsUprocImpl());
    	obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
        obj.extract();
        if(!uprocs.containsKey(obj.getName())){
        uprocs.put(obj.getName(),obj);}
    	
    }
    return uprocs;
}

public HashMap<String,Uproc> getUprocHashMap_from_outside() throws Exception {  
	
   
    return uprs;
}


public void createInternalScript(Uproc obj,String[]lines) throws UniverseException {
    InternalScript data = new InternalScript(obj);
    
    data.setLines(lines);// put your script here

    obj.setInternalScript(data);
    obj.setSpecificData(data);
    data.save();
    //printf("Uproc [%s] => specific data created.\n", obj.getIdentifier()
     //       .getName());
}
public  String[] extractInternalScript(Uproc u) throws UniverseException {
    
    InternalScript script = new InternalScript(u);
    
    script.extractContent();
    //printf("Internal script extraction for Uproc [%s] :\n", u.getName());

    String[] lines = script.getLines();
    if (lines != null) {
        return lines;
        }
    else
    {
    	return new String[]{"Error copying script"};
    }
}
public void updateScript(Uproc obj,String a,String b) throws Exception
{
	if(getUprocHashMap_from_outside().containsKey(obj.getName()))
	{
		Uproc uproc = getUprocHashMap_from_outside().get(obj.getName());
		
		if(uproc.getType().equalsIgnoreCase("CL_INT")){
		
	
		String[] currentScriptLines =extractInternalScript(uproc);
	
		for(int j=0;j<currentScriptLines.length;j++)
	
		{
		
			//System.out.println( (j+1)+") Before : \""+currentScriptLines[j]+"\"");
			
		if(currentScriptLines[j].contains(a))
		{
			currentScriptLines[j]=currentScriptLines[j].replace(a, b);
			System.out.println("UPR ["+uproc.getName()+"] : Replacing "+"\""+a+"\" with \""+b+"\"");

		}
		
		//System.out.println((j+1)+") After : \""+currentScriptLines[j]+"\"");

			
		
	}
	createInternalScript(uproc, currentScriptLines);
}
	}
}

public void renameTskNames(Task obj,String a,String b) throws Exception
{
	if(obj.getIdentifier().getName().contains(a))
	{
		
	String newname= (obj.getIdentifier().getName().replace(a, b)+"_"+obj.getMuName()).replace("(", "").replace(")","");
	System.out.println(" Renaming TASK ["+obj.getIdentifier().getName()+"] to ["+newname+"]");

	if(!tskAlreadyExists(newname))
	{
		
	Task newobj  = new Task (getContext (), TaskId.createWithName (newname, obj.getIdentifier().getVersion(), obj.getMuName(), obj.isTemplate()));
	newobj.populate(obj);
	
	newobj.setImpl(new OwlsTaskImpl());
	newobj.create();
	//tsks.put(newobj.getIdentifier().getName(),newobj);
	obj.delete();
	}
	}
	
}

public UprocList getHeaderUprocList() throws Exception {  
	
    UprocFilter filter = new UprocFilter("*_H_*");
    UprocList list = new UprocList(getContext(), filter);
    
    list.setSyntaxRules(OwlsSyntaxRules.getInstance());
    OwlsUprocListImpl impl = new OwlsUprocListImpl();
    impl.init(list, Operation.DISPLAYLIST);
    list.setImpl(impl);
    list.extract();
    return list;
}

public ArrayList<Session> getSessionsArrayList() throws Exception {  
    SessionFilter filter = new SessionFilter("*","*");
    SessionList list = new SessionList(getContext(), filter);
    list.setImpl(new OwlsSessionListImpl());
    list.extract();
    
    ArrayList<Session> sessions= new ArrayList<Session>();
    
    for(int i=0;i<list.getCount();i++)
    {
    	SessionId id = list.get(i).getIdentifier();
    	Session obj = new Session(getContext(),id);
    	obj.setImpl(new OwlsSessionImpl());
    	obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
        obj.extract();
        sessions.add(obj);
    	
    }
    return sessions;
}
public HashMap<String,Session> getSessionsHashMap(String sesfilter) throws Exception {  
    SessionFilter filter ;
    if(!sesfilter.isEmpty())
    {
    	filter =  new SessionFilter("*","*"+sesfilter.toUpperCase()+"*");
    }
    else
    {
    	filter = new SessionFilter("*","*");
    }
    SessionList list = new SessionList(getContext(), filter);
    list.setImpl(new OwlsSessionListImpl());
    list.extract();
    
    HashMap<String,Session> sessions= new HashMap<String,Session>();
    
    for(int i=0;i<list.getCount();i++)
    {
    	SessionId id = list.get(i).getIdentifier();
    	Session obj = new Session(getContext(),id);
    	obj.setImpl(new OwlsSessionImpl());
    	obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
        obj.extract();
        if(!sessions.containsKey(obj.getName())){
        sessions.put(obj.getName(),obj);}
    	
    }
    return sessions;
}

public HashMap<String,Session> getSessionsHashMap_from_outside() throws Exception {  
    
    return sess;
}



public Session getSession(SessionItem item) throws Exception{  
	SessionId id = item.getIdentifier();
	Session obj = new Session(getContext(),id);
	obj.setImpl(new OwlsSessionImpl());
	obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
    obj.extract();
    return obj;
}

public HashMap<String,Mu> getMUsHashMap()
{
	HashMap<String,Mu> result = new HashMap<String,Mu>();
	 try {
	 MuFilter filter = new MuFilter("*", "*");  // filter on ID useless
     filter.setRequestOffset(false);
     MuList list = new MuList(getContext(), filter);
     OwlsMuListImpl impl = new OwlsMuListImpl();
     list.setImpl(impl);
    
		list.extract();
	
	for(int i=0;i<list.getCount();i++)
	{

         Mu obj = new Mu(getContext(), list.get(i).getIdentifier());
         obj.setImpl(new OwlsMuImpl());
         obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance ());
         obj.extract();
         
         if(!result.containsKey(obj.getName()))
         {
        	 result.put(obj.getName(), obj);
         }
         
     } 

	 }catch (SyntaxException e) {
         e.printStackTrace(System.out);
     } catch (UniverseException e) {
         e.printStackTrace(System.out);
     }
	 
		return result;
	
}

public void delExistingOpt(String upr,String mu) throws UniverseException
{
	Set<String> taskKeys = new HashSet<String>(tsks.keySet());
	
	for(String tskKey:taskKeys)
	{
		if(tsks.get(tskKey).getTaskType().equals(TaskType.Optional) 
				&& tsks.get(tskKey).getUprocName().equalsIgnoreCase(upr)
				&& tsks.get(tskKey).getMuName().equalsIgnoreCase(mu))
		{
			tsks.get(tskKey).delete();
			tsks.remove(tskKey);
		}
	}
}

public ArrayList<String> getSessionsUprBelongsTo(String uprName)
{
	ArrayList<String> result = new ArrayList<String>();

	if(!uprs.containsKey(uprName))
	{
		result.add("ERROR_UPR_NOT_FOUND");
		return result;
	}
	
	
	for(String sesKey:sess.keySet())
	{
		for(int u=0;u<sess.get(sesKey).getUprocs().length;u++)
		{
			if(sess.get(sesKey).getUprocs()[u].equalsIgnoreCase(uprName))
			{
				result.add(sesKey);
			}
		}
	}
	
	return result;
	
}
public void refreshRuleOnOptTsk(HashMap<String,String> sessionNames) throws UniverseException
{
	for(String tskKey:tsks.keySet())
	{
		if(tsks.get(tskKey).getTaskType().equals(TaskType.Optional) && sessionNames.containsKey(tsks.get(tskKey).getSessionName()))
		{
			updateRule(tsks.get(tskKey),getRule(tsks.get(tskKey)));
		}
	}
}
public void globalRuleRefreshOnTask() throws UniverseException
{
	for(String tskKey:tsks.keySet())
	{
		updateRule(tsks.get(tskKey),getRule(tsks.get(tskKey)));

	}
}
private  void updateRule(Task t,String rule) throws UniverseException
{
	
	if(t.getTaskType().equals(TaskType.Provoked))
	{
		System.out.println("Skipping rule update on [PROVOKED TSK] "+t.getIdentifier().getName());
		return;
	}
	
	String oldRule = getRule(t);
	
	TaskPlanifiedData tpd  =(TaskPlanifiedData) t.getSpecificData();
	

    Rule rule1;
    
    if(!this.ruleAlreadyExists(rule))
    { 
    	this.createRule(rule);
    }
   
    
    rule1=this.getRule(rule);

   
        TaskImplicitData tid1 = new TaskImplicitData (rule1.getIdentifier ());
        
        tid1.setFunctionalVersion(rule1.getFunctionalVersion());
        tid1.setLabel(rule1.getLabel());
        tid1.setMonthAuthorization(rule1.getMonthAuthorization());
        tid1.setWeekAuthorization(rule1.getWeekAuthorization());  
        
        tid1.setPeriodType (rule1.getPeriodType ());
        tid1.setPeriodNumber(rule1.getPeriodNumber());
        tid1.setPattern (rule1.getPattern ());
        tid1.setAuthorized (true);
        final Date date1 = DateTools.toDate ("20140101");
        tid1.setReferenceDate(DateTools.getYYYYMMDD(date1));
        Calendar calendar1 = DateTools.getCalendarInstance();
        calendar1.setTime(date1);
        Integer weekNumber1 = calendar1.get(Calendar.WEEK_OF_YEAR);
        tid1.setApplicationWeek(weekNumber1.toString());
        
        tid1.setLabel(rule1.getLabel());
        tid1.setInternal(true);
      
        TaskImplicitData[] implicitDataArray = new TaskImplicitData[] {tid1};
        tpd.setImplicitData (implicitDataArray);
        
        t.setSpecificData(tpd);
        t.update();
        
	System.out.println("Updated Rule [OPT TSK] "+t.getIdentifier().getName()+": "+oldRule+" --> "+getRule(t));

}

private  String getRule(Task tsk) {
	if (tsk.getTaskType().equals(TaskType.Provoked)) {
		return "Provoked";
	} else {
		TaskPlanifiedData taskPlanifiedData = (TaskPlanifiedData) tsk
				.getSpecificData();

		if (taskPlanifiedData.getImplicitData() != null) {
			if (taskPlanifiedData.getImplicitData().length > 0) {
				TaskImplicitData taskImplicitData = taskPlanifiedData
						.getImplicitData()[0];
				return taskImplicitData.getName();
			} else {
				return "RULE_READ_ERROR";

			}

		} else {

			return "RULE_READ_ERROR";

		}
	}
}
private  String getLaunchInfo(Task tsk) {
	if (tsk.getTaskType().equals(TaskType.Provoked)) {
		return "Provoked";
	} else {
		
		
       TaskPlanifiedData tpd = new TaskPlanifiedData ();
       
       
       tpd=(TaskPlanifiedData)tsk.getSpecificData();
       
        LaunchHourPattern[] launchHourPatterns =tpd.getLaunchHourPatterns() ;         
        //Start time;end time;every mmm;launch window in hours;launch window in minutes
    	//Hhmm;hhmm;mmm;hhh;mm
        //0715;1515;060;000;59
	       if(launchHourPatterns.length>0)
	       {
	    	   String start;
	    	   String end;
	    	   String freq;
	    	   String LW_hour;
	    	   String LW_min;
	    	   
	    	   start = launchHourPatterns[0].getStartTime().substring(0,4);
	    	   
	    	   if(launchHourPatterns[0].getEndTime()==null)
	    	   {
	    		   end="0000";
	    	   }
	    	   else
	    	   {
	    		   end=launchHourPatterns[0].getEndTime();
	    	   }
	    	   
	    	   if(launchHourPatterns[0].getFrequency()>99)
	    	   {
	    		   freq=Integer.toString(launchHourPatterns[0].getFrequency());
	    	   }
	    	   else if(launchHourPatterns[0].getFrequency()>9)
	    	   {
	    		   freq="0"+Integer.toString(launchHourPatterns[0].getFrequency());
	    	   }
	    	   else
	    	   {
	    		   freq="00"+Integer.toString(launchHourPatterns[0].getFrequency());
	    	   }
	    	   
	    	   if(launchHourPatterns[0].getDurationHour()>99)
	    	   {
	    		   LW_hour=Integer.toString(launchHourPatterns[0].getDurationHour());
	    	   }
	    	   else if(launchHourPatterns[0].getDurationHour()>9)
	    	   {
	    		   LW_hour="0"+Integer.toString(launchHourPatterns[0].getDurationHour());
	    	   }
	    	   else
	    	   {
	    		   LW_hour="00"+Integer.toString(launchHourPatterns[0].getDurationHour());
	    	   }
	    	   
	    	   
	    	   
	    	   
	    	   if(launchHourPatterns[0].getDurationMinute()>9)
	    	   {
	    		   LW_min=Integer.toString(launchHourPatterns[0].getDurationMinute());
	    	   }
	    	   else
	    	   {
	    		   LW_min="0"+Integer.toString(launchHourPatterns[0].getDurationMinute());
	    	   }
	    	  
	    	   
	    		  return  start
	    				  +";"+end
	    				  +";"+freq
	    				  +";"+LW_hour
	    				  +";"+LW_min;
	  
	       }
	       else
	       {
	    	   return "";
	       }
	}
}

public String getRULE_LW_MU_for_CSV(String upr)
{
	String rule="";
	String lw="";
	String mu="";
	
	for(String tskKey:tsks.keySet())
	{
		if(tsks.get(tskKey).getUprocName().equalsIgnoreCase(upr))
		{
			rule=getRule(tsks.get(tskKey));
			lw = getLaunchInfo(tsks.get(tskKey));
			mu+=tsks.get(tskKey).getMuName()+"|";
			
		}
	}
	
	return rule+","+lw+","+mu;
}

public ArrayList<ExecutionItem> getExecutionList() throws Exception{        
        ExecutionFilter filter = new ExecutionFilter();
        
        ExecutionList list = new ExecutionList(getContext(), filter);
        list.setImpl(new OwlsExecutionListImpl());
        list.extract();
        ArrayList<ExecutionItem> arrayList = new ArrayList<ExecutionItem>();
        for(int i=0;i<list.getCount();i++)
        {
        	ExecutionItem item=list.get(i);
        	arrayList.add(item);
        }
        
        return arrayList;
    }
public ArrayList<ExecutionItem> getExecutionList(ExecutionStatus[] status_array) throws Exception{        
    
	HashMap <String,ExecutionItem> table = new HashMap<String,ExecutionItem>();
	
	ExecutionFilter filter = new ExecutionFilter();
  
    filter.setStatuses(status_array);
    ExecutionList list = new ExecutionList(getContext(), filter);
    list.setImpl(new OwlsExecutionListImpl());
    list.extract();
    
    ArrayList<ExecutionItem> arrayList = new ArrayList<ExecutionItem>();
    ArrayList<ExecutionItem> aborted_timeoverrun = new ArrayList<ExecutionItem>();

    
    for(int i=0;i<list.getCount();i++)
    {
    	ExecutionItem item=list.get(i);
    	
    	if(table.containsKey(item.getUprocName()))
    	{
    		if(item.getEndDate()!=null && table.get(item.getUprocName())!=null)
    		{
    			if(item.getEndDate().after(table.get(item.getUprocName()).getEndDate()))
    			{
        			        				
    					table.remove(item.getUprocName());
        				table.put(item.getUprocName(),item);
    			}//if the current execution is newer than the one we already have in the table, replace the old with the new 
    				
        			
    			
    		}
    	}
    	else
    	{
    		table.put(item.getUprocName(), item);
    	}
    	
    	
    }
    
    for(String key:table.keySet())
    {
    	arrayList.add(table.get(key));
    	if(table.get(key).getStatus().equals(ExecutionStatus.Aborted))//||table.get(key).getStatus().equals(ExecutionStatus.TimeOverrun))
    	{
    		aborted_timeoverrun.add(table.get(key));
    	}
    }
    
    
 /*   out.println("   End date   : " +
        	(execution.getEndDate() == null? ""
        		: sdfDate.format(execution.getEndDate())));
        out.println("   End hour   : " +
        	(execution.getEndDate() == null? ""
        		: sdfHour.format(execution.getEndDate())));*/
    
    
    //return arrayList;
    return aborted_timeoverrun;
}

@SuppressWarnings("unused")
private void printExecution(ExecutionItem execution,PrintStream prtstm) {

    out.println();
    out.println(" ExecutionId");
    out.println("   Session : " + execution.getSessionName());
    out.println("   Uproc   : " + execution.getUprocName());
    out.println("   MU      : " + execution.getMuName());
    out.println("   Numsess : " + execution.getNumsess());
    out.println("   Numproc : " + execution.getNumproc());
    out.println();
    out.println(" Data");
    out.println("   Numlanc : " + execution.getNumlanc());
    out.println("   Status     : " +
                getStatus(execution.getStatus()));
    out.println("   Step       : " + execution.getStep());
    out.println("   Relaunched : " + execution.isRelaunched());
    out.println("   Begin date : " +
            sdfDate.format(execution.getBeginDate()));
    out.println("   Begin hour : " +
            sdfHour.format(execution.getBeginDate()));
    out.println("   End date   : " +
    	(execution.getEndDate() == null? ""
    		: sdfDate.format(execution.getEndDate())));
    out.println("   End hour   : " +
    	(execution.getEndDate() == null? ""
    		: sdfHour.format(execution.getEndDate())));
    out.println("   Begin date : " +
            sdfDate.format(execution.getBeginDate()));
    if (execution.getProcessingDate() != null 
            && !execution.getProcessingDate().equals("00000000")
            && !execution.getProcessingDate().equals("")) {
        out.println("   Proc. date : "
                + execution.getProcessingDate());
    } else {
        out.println("   NO Proc. date .");
    }

    out.println("   User       : " + execution.getUserName());
    out.println("   Author     : " + execution.getAuthorCode());

    out.println("   Queue      : " + execution.getQueue());
    out.println("   Priority   : " + execution.getPriority());
    out.println("   Num Entry  : " + execution.getEntry());
    out.println("   Uproc ver  : " + execution.getUprocVersion());
    out.println("   Sess. ver  : " + execution.getSessionVersion());
    out.println("   Info.      : " + execution.getInfo());
    out.println("   Severity   : " + execution.getSeverity());
    out.println("   Appli.     : " + execution.getApplication());

    out.println("   Sess. rank : " + execution.getRankInSession());
    out.println("   from task  : " + execution.isTaskOrigin());
    out.println("   task       : " + execution.getTaskName());
    out.println("   Task vers. : " + execution.getTaskVersion());
    out.println("   Domain   . : " + execution.getDomain());
    
    out.println("-------------------------------------------------------");
}

public static ArrayList<Session> getSessionsUprBelongsTo(HashMap<String, Session> sessions, String upr) {
	ArrayList<Session> matches = new ArrayList<Session>();

	for (String s : sessions.keySet()) {
		for (int u = 0; u < sessions.get(s).getUprocs().length; u++) {
			if (sessions.get(s).getUprocs()[u].equalsIgnoreCase(upr)) {
				matches.add(sessions.get(s));
			}
		}
	}

	return matches;
}
public  ArrayList<Task> getTasksUprBelongsTo(HashMap<String, Task> tasks, String upr) {
	ArrayList<Task> matches = new ArrayList<Task>();

	for (String t : tasks.keySet()) {
		{
			if(tasks.get(t).getTaskType().equals(TaskType.Optional))
			{
		
				if (tasks.get(t).getUprocName().equalsIgnoreCase(upr))
				{
					matches.add(tasks.get(t));
				}
			}
		
		}
		
	}
	
	if(matches.size()==0)
	{
		ArrayList<String> sessionsUprBelongsTo = getSessionsUprBelongsTo(upr);
		
		
		if(sessionsUprBelongsTo.size()==1)
		{
			String headerUprocOnWhichMainTask=sess.get(sessionsUprBelongsTo.get(0)).getHeader();

			for(String t:tasks.keySet())
			{
				if (tasks.get(t).getSessionName().equalsIgnoreCase(sessionsUprBelongsTo.get(0)) && tasks.get(t).getUprocName().equalsIgnoreCase(headerUprocOnWhichMainTask))
					{
						matches.add(tasks.get(t));
	
					}
			}
		}
	}
	
	return matches;
}

public static String getStatus(ExecutionStatus status) {

    if (status == ExecutionStatus.Pending) {
        return "Pending";
    } else if (status == ExecutionStatus.Started) {
        return "Started";
    } else if (status == ExecutionStatus.Running) {
        return "Running";
    } else if (status == ExecutionStatus.CompletionInProgress) {
        return "Completion in progress";
    } else if (status == ExecutionStatus.Aborted) {
        return "Aborted";
    } else if (status == ExecutionStatus.TimeOverrun) {
        return "Time overrun";
    } else if (status == ExecutionStatus.Refused) {
        return "Refused";
    } else if (status == ExecutionStatus.Completed) {
        return "Completed";
    } else if (status == ExecutionStatus.EventWait) {
        return "Event wait";
    } else if (status == ExecutionStatus.Launching) {
        return "Launching";
    } else if (status == ExecutionStatus.Held) {
        return "Held";
    } else {
        return "???";
    }
}

public void readSession(Session session) throws UniverseException
{
	out.println();
	out.println(" name:    " + session.getName());
	out.println(" id:      " + session.getId());
	out.println(" version: " + session.getVersion());
	out.println(" label:   " + session.getLabel());
	out.println();
	new PrintableTree(session.getTree()).print();
}


    static class PrintableTree1 extends SessionTree {

        private static final long serialVersionUID = 1L;

        PrintableTree1(SessionTree tree) {
            super(tree.getRoot());
        }

        void print() {

            print(root, 0, "");
        }


        java.util.Hashtable<String, Integer> map =
                new java.util.Hashtable<String, Integer>();

        //
        // Beeerk!! mais bon, c'est du test....
        //
        // il faut prefix.lenght = 4
        void print(SessionAtom atom, int level, String prefix) {

            StringBuffer buff = new StringBuffer("   ");

            if (level > 0) {
            for (int i = 0; i < level-1; i++) {
                   int count = getCount(i);
                   if (count == 0) {
                       buff.append(' ');
                   } else {
                       //buff.append((char) 179); //'\u2502'
                       buff.append('|');
                   }
                   for (int j = 1; j < (3+5); j++) {
                       buff.append(' ');
                   }
               }

                if (atom.getNextSibling() != null) {
                    //buff.append((char) 195); // '\u251C'
                    buff.append('+');
                } else {
                    if (getCount(level - 1) > 1) {
                        //buff.append((char) 195); // '\u251C'
                        buff.append('+');
                    } else {
                        //buff.append((char) 192); // '\u2514'
                        buff.append('\\');
                    }
                }
                //buff.append((char) 196); // '\u2500'
                //buff.append((char) 196);
                buff.append('-');
                buff.append('-');
                buff.append(prefix);
                buff.append(' '); //((char) 196);
            }
            
            if(atom.getData() != null)
            {
             // print data
                System.out.print(buff.toString() + atom.getData().getUprocName());
                ExecutionContext context = atom.getData().getExecutionContext();
                if (context != null) {
                    switch (context.getType()) {
                    case SAME:
                        break;
                    case MU:
                        System.out.print(" / " + context.getMuName());
                        break;
                    case HDP:
                        System.out.print(" / " + context.getHDP());
                        break;
                    }
                }
            }
            
            System.out.println();

            if (level > 0) {
                decrement(level - 1);
            }

            int childCount = countChild(atom); // le nb de child au level+1
            map.put(Integer.toString(level), new Integer(childCount));

            StringBuffer b = new StringBuffer("   ");
            for (int i = 0; i < level; i++) {
                int count = getCount(i);
                if (count > 0) {
                    //b.append((char) 179); //'\u2502'
                    b.append('|');
                } else {
                    b.append(' ');
                }
                for (int j = 1; j < (3 + 5); j++) {
                    b.append(' ');
                }
            }
            if (childCount > 0) {
                //b.append((char) 179); //'\u2502'
                b.append('|');
            }
            System.out.println(b);

            //fils ok
            if (atom.getChildOk() != null) {
                print(atom.getChildOk(), level + 1, "(ok)");
            }
            //fils ko
            if (atom.getChildKo() != null) {
                print(atom.getChildKo(), level + 1, "(ko)");
            }
            //fr�re
            if (atom.getNextSibling() != null) {
                print(atom.getNextSibling(), level,
                      (atom.getNextSibling().isOk()) ? "(ok)" : "(ko)");
            }
        }


        int countChild(SessionAtom atom) {
            int childCount = 0; // le nb de child au level+1
            if (atom.getChildOk() != null) {
                childCount++;
                SessionAtom a = atom.getChildOk();
                while (a.getNextSibling() != null) {
                    childCount++;
                    a = a.getNextSibling();
                }
            }
            if (atom.getChildKo() != null) {
                childCount++;
                SessionAtom a = atom.getChildKo();
                while (a.getNextSibling() != null) {
                    childCount++;
                    a = a.getNextSibling();
                }
            }
            return childCount;
        }


        int getCount(int level) {
            Integer count = (Integer) map.get(Integer.toString(level));
            if (count != null) {
                return count.intValue();
            } else {
                return 0;
            }
        }

        void decrement(int level) {
            Integer count = (Integer) map.get(Integer.toString(level));
            if (count != null) {
                int i = count.intValue() - 1;
                map.put(Integer.toString(level), new Integer(i));
            }
        }

    }
    
    
    public void insertSessionAtom(Session sessname,String currentUprName,final String nameOfTechUproc)
    {
    	try {					    		   		
    	            Session obj = sessname;

    	    		SessionTree tree = obj.getTree();
    	    		
    	    		final String curUpr = currentUprName;
    	    			 
    	    		tree.scan(new AtomVisitor() 
    	    			{
    	    				public void handle(SessionAtom atom) 
    	    				{
    	    					insertTechUproc(atom,curUpr,nameOfTechUproc);
    	    				}
    	    			});
    	    			
    	    
    	    		obj.update();
    	    		
    	    		System.out.println(nameOfTechUproc+" has been inserted as a PARENT to \""+currentUprName+"\" in SESSION \""+sessname.getName()+"\"");
    	    	   	    		

    	} catch (Exception e) {
    		e.printStackTrace();
    	}	
    	
    }
    
    public String getFatherUproc(String sessname,String currentUprName)
    {
    					
    			if(sess.containsKey(sessname)){
    				try {	
    				
    				 final ArrayList<String> result= new ArrayList<String>();
    	            Session obj = sess.get(sessname);

    	    		SessionTree tree = obj.getTree();
    	    		
    	    		final String curUpr = currentUprName;
    	    			 
    	    		tree.scan(new AtomVisitor() 
    	    			{
    	    				public void handle(SessionAtom atom) 
    	    				{
    	    					if(atom.getData()!=null)
    	    					{
    	    						if(atom.getData().getUprocName().equalsIgnoreCase(curUpr))
    	    						{
    	    							if(atom.getParent()!=null)
    	    							{
    	    								result.add(atom.getParent().getData().getUprocName());
    	    								
    	    							}
    	    						}
    	    					}
    	    				}
    	    			});
    	    			
    	    
    	    		if(result.size()>0)
    	    		{//System.out.println(currentUprName+" has father uproc  \""+result.get(0)+"\" in SESSION \""+sessname+"\"");
    	    		return result.get(0);
    	    		}
    	    		else
    	    		{
    	    			return "N/A";
    	    		}
		

    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    			}
		return "N/A";
    	
    }
    
    public String getUprocDepsAndVariablesToString(String uproc,HashMap<String,String>dollaru_esp_uprocname_map)
   	{//this was developed for the state of DE
       	if(uprs.containsKey(uproc))
       	{
       		Uproc obj = uprs.get(uproc);
       		
       		Vector<DependencyCondition> deps = new Vector<DependencyCondition>(obj.getDependencyConditions());
   	
   	
   			String depcons = "";
   			
   			for(int i=0;i<deps.size();i++)
   			{
   				String mappedESPName="TBD";
   				if(dollaru_esp_uprocname_map.containsKey(deps.get(i).getUproc()))
   				{
   					mappedESPName=dollaru_esp_uprocname_map.get(deps.get(i).getUproc());
   				}
   				depcons+=deps.get(i).getNum()+":"+deps.get(i).getUproc()+"/"+mappedESPName+"|";
   				
   			}
   		
   			Vector<Variable> varia=obj.getVariables();
   			String variables = "";
   			
   			for(int v=0;v<varia.size();v++)
   			{
   				if(varia.get(v).getName().equalsIgnoreCase("COMMAND"))
   				{
   					variables+=varia.get(v).getName()+"="+varia.get(v).getValue()+"|";
   				}
   			}
   		    
   			return ","+depcons+","+variables;
   			
   		 }
       	
       	else
       	{
       		return ",UPR "+uproc+" not found on"+this.getConnName()+
       				",UPR "+uproc+" not found on"+this.getConnName();
       	}
   	
   	}
    
    
    
    public void updateDepConsName(String curUpr,HashMap<String,String> old_new) throws UniverseException
    {//developed for state of DE , takes into a mapping of old_new uproc names,
    	//checks if depcon is part of that hashmap and replaces it with its value (the new name)
    	if(uprs.containsKey(curUpr))
    	{
    		Uproc atUpr = uprs.get(curUpr);
    		
       		Vector<DependencyCondition> deps = new Vector<DependencyCondition>(atUpr.getDependencyConditions());

       		for(int i=0;i<deps.size();i++)
   			{
   				if(old_new.containsKey(deps.get(i).getUproc()))
   				{
   					deps.get(i).setUproc(old_new.get(deps.get(i).getUproc()).toUpperCase());
   				}
   			}
       		
       		atUpr.setDependencyConditions(deps);
       		atUpr.update();
       		
    	}
    }
    
    public void addDepConsToUproc(String curUpr,ArrayList<String>missingDeps) throws UniverseException
    {//developed for state of DE , takes into a mapping of old_new uproc names,
    	//checks if depcon is part of that hashmap and replaces it with its value (the new name)
    	if(uprs.containsKey(curUpr))
    	{
    		Uproc atUpr = uprs.get(curUpr);
   			LaunchFormula formula = atUpr.getFormula();
   			String text;
       		Vector<DependencyCondition> deps = new Vector<DependencyCondition>(atUpr.getDependencyConditions());
       		
       		int counter = deps.size();
       		
       		for(int i=0;i<missingDeps.size();i++)
   			{
       			if(uprs.containsKey(missingDeps.get(i)))
       			{	
       				SessionControl sessionControl = new SessionControl();
       				sessionControl.setType(SessionControl.Type.SAME_SESSION_AND_EXECUTION);
       				       				
       				MuControl muControl= new MuControl();			     				
       				muControl.setType (Type.SAME_MU);
       					
       				DependencyCondition dc = new DependencyCondition();
       				dc.setExpected(true);//expected is chosen
       				dc.setFatal(false);//fatal box is NOT checked
       				dc.setUserControl(UserControl.ANY);//user is any
       				dc.setFunctionalPeriod(FunctionalPeriod.Day);
       				dc.setNum(counter+1);//condition number corresponds to the ncd iterator
       				
      				
       				if(i != (missingDeps.size()-1))
       				{
						if((counter+1)<10){
							text = " =C0"+(counter+1)+" AND";// OK
						}
						else 
						{
							text = " =C"+(counter+1)+" AND";
						}
					}
					
       				else
					{
						if(counter+1<10){
							text = " =C0"+(counter+1);// OK
							}
							else 
							{
								text = " =C"+(counter+1);
							}
					}
       				
       				
       				dc.setUproc(missingDeps.get(i));
       				dc.setSessionControl(sessionControl);
       				dc.setStatus(Status.COMPLETED); 
       			    dc.setMuControl (muControl);
       			     
       			    
       			    deps.add(dc);
       			   
       			    formula.appendText(text);

       			    counter++;
       					
       			}
   			}
       				atUpr.setFormula(formula);
       				atUpr.setDependencyConditions(deps);
       				
       				
       				try {
       					atUpr.update();
       				} catch (UniverseException e) {
       					e.printStackTrace();
       				}

       				System.out.println("DEP added to UPROC : "+atUpr.getName()+" ---> OK");
       				System.out.println();
       			
       			}
       			
       		}
       		
    public void removeDepConsFromUproc(String curUpr) throws UniverseException
    {//developed for state of DE , takes into a mapping of old_new uproc names,
    	//checks if depcon is part of that hashmap and replaces it with its value (the new name)
    	if(uprs.containsKey(curUpr))
    	{
    		Uproc atUpr = uprs.get(curUpr);
   			LaunchFormula formula = new LaunchFormula();
       		Vector<DependencyCondition> deps = new Vector<DependencyCondition>();
       
   			
       				atUpr.setFormula(formula);
       				atUpr.setDependencyConditions(deps);
       				
       				
       				try {
       					atUpr.update();
       				} catch (UniverseException e) {
       					e.printStackTrace();
       				}

       				System.out.println("Cleared dep cons on UPROC : "+atUpr.getName()+" ---> OK");
       				System.out.println();
       			
       			}
       			
       	}	
       		
       		
    	
    
    public void updateTaskWithNewUprocName(String taskName,HashMap<String,String> old_new) throws UniverseException
    {//developed for state of DE , takes into a mapping of old_new uproc names,
    	//checks if tasks is defined on one of the old uproc names, if found changes that uproc setting to
    	//new uproc at the task level . 
    	
    	if(tsks.containsKey(taskName))
    	{
    		Task atTask = tsks.get(taskName);
    		String atUpr=atTask.getUprocName();
       	

    	
    		if(old_new.containsKey(atUpr))
       		{
       			atTask.setUprocName(old_new.get(atUpr));
        		
       			/*String optTaskName= "OPT_"+old_new.get(atUpr);
        		if(optTaskName.length()>64)
        		{
        			optTaskName=optTaskName.substring(0, 63);
        		}
        		
           		atTask.getIdentifier().setName(optTaskName);*/


       			
       		}
    	
       		atTask.update();
       		
    	}
    }
    public void updateOPT_TaskNameAdhoc() throws Exception
    {//developed for state of DE , makes sure the opt Tasks follow <OPT_TSK><UPROCNAME>. 
    	
    	for(String tskKey:tsks.keySet())
    	{
    		Task atTask = tsks.get(tskKey);
    		
    		if(atTask.getTaskType().equals(TaskType.Optional))
    		{
        		String atUpr = atTask.getUprocName();
        		if(!uprs.containsKey(atUpr))
        		{
        			continue;
        		}
        		String optTaskName= (("OPT_"+atUpr).replace("DECSS_", "")).toUpperCase();
        		if(optTaskName.length()>64)
        		{
        			optTaskName=optTaskName.substring(0, 63);
        		}
        		        	  			
    	
    			
    		String newname= optTaskName;
    		System.out.println(" Renaming TASK ["+atTask.getIdentifier().getName()+"] to ["+newname+"]");

    		copyTask(atTask,newname);
        		
        		
    		}	


       			
       	}
    	
       	
    }
    
    public void copyTask(Task task,String newName)  throws Exception{
    	System.out.println(newName);
    	Object O = SerializationUtils.clone(task);
    	if (O instanceof Task)
    	{
    	Task t = (Task)O;
        t.setContext(getContext());
        t.setImpl(new OwlsTaskImpl());           
        TaskId tId = TaskId.createWithName(newName, task.getIdentifier().getVersion(), task.getIdentifier().getMuName(), task.getIdentifier().isTemplate());
        tId.setSyntaxRules(OwlsSyntaxRules.getInstance());
        tId.setSessionName(task.getSessionName());
        tId.setSessionVersion(task.getSessionVersion());
        tId.setUprocName(task.getUprocName());
        tId.setUprocVersion(task.getUprocVersion());
        task.delete();
        t.setIdentifier(tId);
        t.create();
        }
  }


    
    public void updateUprocNameInSession(String sessname,HashMap<String,String>old_new)
    {//developed for state of DE, needs to update the uprocname in a session
    	try {					    		   		
    	            Session obj = this.getSession(sessname);

    	    		SessionTree tree = obj.getTree();
    	    		
    	    		final HashMap<String,String> oldUprocs_newUprocs = old_new;
    	    			 
    	    		tree.scan(new AtomVisitor() 
    	    			{
    	    				public void handle(SessionAtom atom) 
    	    				{
    	    					updateSessionAtomName(atom,oldUprocs_newUprocs);
    	    				}
    	    			});
    	    			
    	    
    	    
    	    		obj.update();
    	    	   	    		

    	} catch (Exception e) {
    		e.printStackTrace();
    	}	
    	
    }
    
    public ArrayList<String> getListOfFathersForUproc(String uproc)
   	{//this was developed for the state of DE
    	
    	ArrayList<String> resultList = new ArrayList<String>();
    	
       	if(uprs.containsKey(uproc))
       	{
       		Uproc obj = uprs.get(uproc);
       		
       		Vector<DependencyCondition> deps = new Vector<DependencyCondition>(obj.getDependencyConditions());
   	
   			
   			for(int i=0;i<deps.size();i++)
   			{
   				resultList.add(deps.get(i).getUproc());
   			}
   		
   		
   			for(int j=0;j<this.getSessionsUprBelongsTo(uproc).size();j++)
   			{
   				resultList.add(getFatherUproc(getSessionsUprBelongsTo(uproc).get(j), uproc));
   			}
   		    
   			return resultList;
   			
   		 }
       	
       	else
       	{
       		return null;
       	}
    
    
   	}
    private static ArrayList<String> getAllRulesFromTask(Task tsk) {
    	ArrayList<String> result=new ArrayList<String>();
    	if (tsk.getTaskType().equals(TaskType.Provoked)) {
    		result.add("Provoked");
    	} else {
    		TaskPlanifiedData taskPlanifiedData = (TaskPlanifiedData) tsk
    				.getSpecificData();

    		if (taskPlanifiedData.getImplicitData() != null) {
    			if (taskPlanifiedData.getImplicitData().length > 0) {
    				
    				for(int r=0;r<taskPlanifiedData.getImplicitData().length;r++)
    				{
    				TaskImplicitData taskImplicitData = taskPlanifiedData
    						.getImplicitData()[r];
    				result.add(taskImplicitData.getName());
    				}
    				
    			} else {
    				result.add("RULE_READ_ERROR");

    			}

    		} else {

    			result.add("RULE_READ_ERROR");


    		}
    	}
    	
    	return result;
    }
    public ArrayList<String> getListOfRulesForUproc(String uproc)
    {//this was developed for the state of DE
    	ArrayList<String> resultList = new ArrayList<String>();
    	
    	ArrayList<Task>matchedTasksToUpr = getTasksUprBelongsTo(tsks,uproc);
    	
    	for(int m=0;m<matchedTasksToUpr.size();m++)
    	{
    		for(int r=0;r<getAllRulesFromTask(matchedTasksToUpr.get(m)).size();r++)
    		{
    			resultList.add(getAllRulesFromTask(matchedTasksToUpr.get(m)).get(r));
    		}
    	}
    	
    	return resultList;
    	
    }
	
    
    
}



















	
	

