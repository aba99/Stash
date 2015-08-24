package stateofde;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.List;


public class SessionModel {
	
	private TreeMap<String, List<String>> uprs = new TreeMap<String, List<String>>();
	private TreeMap<String, String> duplicates = new TreeMap<String, String>();
	private String header;
	private String trailerCrit;
	private String trailerNonCrit;
	private String firstUprocName;
	private String firstAddedUprFromCSV;
	
	public String label;  
	public String sessionName;
	
	@SuppressWarnings("unused")
	private SessionModel() {
	}
	
	public SessionModel(String name) {
		sessionName = name;
	}

	public Collection<String> getUprs() {
		return uprs.keySet();
	}

	public void addUproc(String upr, String children) {
		
		if(firstAddedUprFromCSV==null)
		{
			firstAddedUprFromCSV=upr;
		}
		
		if (firstUprocName==null && NamingUtils.isHeader(upr))
			firstUprocName=upr;
		
		List<String> list = uprs.get(upr);
		if (list==null) 
			list = new ArrayList<String>();
		
		String[] tks = children.split("\\|");
		for (String tk: tks) {
			String child = NamingUtils.getTruncatedUprName(tk.replaceAll("\\(\\d+\\)", "").trim());
			if (child.length()>0 && !child.equals("NULL"))
				list.add(child);
		}
		uprs.remove(upr);
		uprs.put(upr, list);
	}
	
	public Collection<String> getChildren(String uproc) {
		return uprs.get(uproc);
	}

	public List<String> getRootUprs() {
		List<String> roots = new ArrayList<String>();
		for (String u :uprs.keySet()) {
			boolean hasChildren = false;		
			for (List<String> children :uprs.values())
				for (String ch :children)
					if (ch.equals(u)) {
						hasChildren=true;
						break;
					} 
			if (!hasChildren)
				roots.add(u);
		}
		
		return roots;
	}

	public void setHeader(String headerName)
	{
		header=headerName;
	}
	public void addHeader() {
		String h = getHeaderName();
		header=h;
		uprs.put(h, getRootUprs());		
	}
	

	public void addTrailer(boolean critical) {
		String trailer = getTrailerName(critical);

		uprs.put(trailer, new ArrayList<String>());
		if (critical)
			trailerCrit=trailer;
		else
			trailerNonCrit=trailer;
		uprs.get(header).add(trailer);
	}
	
	public String getHeader() {
		return header;
	}
	public String getCritTrailer() {
		return trailerCrit;
	}
	public String getNonCritTrailer() {
		return trailerNonCrit;
	}
	public void setfirstUprocName(String upr)
	{
		firstUprocName=upr;
	}
	public String getFirstAddedUprocName()
	{
		return firstAddedUprFromCSV;
	}
	
	/**
	 * Rename all uprocs in the internal data structures
	 */
	public void renameUprocs() {		
		//need a temp set to perform the changes while cycling
		Set<String> keys = new HashSet<String>();
		keys.addAll(uprs.keySet());
		for (String key: keys) {
			//remove current uproc and it's children
			List<String> ch = uprs.remove(key);
			//create a new children linst
			List<String> newCh = new ArrayList<String>();
			//rename each child in the list
			for (String u: ch) 
				newCh.add(getNewUprocName(u));
			//rename the uproc and attach the renamed children list 
			uprs.put(getNewUprocName(key), newCh);
		}
	}
	
	public String getOriginalName(String renamedUprocName) {
		String ret = duplicates.get(renamedUprocName);
		if (ret!=null)
			return ret;
		else
			return renamedUprocName;
	}

	// naming -------------------
	
	private String getTrailerName(boolean critical) {
		if (critical)
			return NamingUtils.getSA_XX(firstUprocName)+ "T_" + sessionName;
		else
			return NamingUtils.getSA_XX(firstUprocName)+ "T_NC_" + sessionName;
	}
	
	
	private String getHeaderName() {
		if(firstUprocName.contains("_RD_")){firstUprocName=firstUprocName.replace("_RD_", "_RT_");}//added for first uprocs containing RD . No _RD_ header !
		return NamingUtils.getSA_XX(firstUprocName)+ "H_" + sessionName;
	}
	private String getNewUprocName(String oldUprocName) {
		String newName = null; 
		//ignore headers and trailers
		if (oldUprocName.equals(header) || oldUprocName.equals(trailerCrit) || oldUprocName.equals(trailerNonCrit) )
			{
				newName = oldUprocName;
				//System.out.println("OLD NAME : "+oldUprocName+" NEW NAME: "+newName);
			}
		else 
		{
				newName = NamingUtils.getTruncatedUprName(NamingUtils.getSA_XX(oldUprocName) + NamingUtils.getJOB(oldUprocName)+ "-" + sessionName);
				//System.out.println("OLD NAME : "+oldUprocName+" NEW NAME: "+newName);

		}
		
		//store in the duplicates map
		if (!oldUprocName.equals(newName)) 
			if (!duplicates.containsKey(newName))
				duplicates.put(newName, oldUprocName);
		
		return newName;
	}	
	
}
