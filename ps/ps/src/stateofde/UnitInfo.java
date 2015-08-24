package stateofde;

import java.util.ArrayList;

public class UnitInfo {

	public String name;
	public ArrayList<String> listOfFathers = new ArrayList<String>();
	public ArrayList<String> listOfRules = new ArrayList<String>();
	public boolean isESP = false;
	
	public UnitInfo()
	{
		listOfFathers = new ArrayList<String>();
		listOfRules = new ArrayList<String>();
		
	}
	
}
