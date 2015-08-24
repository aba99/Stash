package com.orsyp.api.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;
import java.util.List;


public class SessionModel {
	
	private TreeMap<String, List<String>> uprs = new TreeMap<String, List<String>>();
	
	public String label;  
	public String sessionName;
	public String header;
	
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
		
		List<String> list = uprs.get(upr);
		if (list==null) 
			list = new ArrayList<String>();
		
		String[] tks = children.split("\\|");
		for (String tk: tks) {
			String child = tk.replaceAll("\\(\\d+\\)", "").trim();
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


	

	
}
