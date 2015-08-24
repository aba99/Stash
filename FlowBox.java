package atco.tools;

import java.util.ArrayList;

public class FlowBox {

  private ArrayList<String> inbound ;
  private ArrayList<String> outbound;
  private String name;
  public boolean visited;
  
  public FlowBox()
  {
	  inbound = new ArrayList<String>();
	  outbound = new ArrayList<String>();
	  name = "default";
	  visited=false;
  }
  public FlowBox(FlowBox fb)
  {
	  inbound=fb.getInbound();
	  outbound=fb.getOutbound();
	  name=fb.getName();
	  visited=fb.isVisited();
  }
  public boolean isVisited()
  {
	  return visited;
  }
  public void setVisited(boolean a)
  {
	  visited=a;
  }
  public void setName(String n)
  {
	  name=n;
  }
  public String getName()
  {
	  return name;
  }
  public void setInbound(ArrayList<String> in)
  {
	  inbound=in;
  }
  public ArrayList<String> getInbound()
  {
	  return inbound;
  }
  public void setOutbound(ArrayList<String> out)
  {
	  outbound=out;
  }
  public ArrayList<String> getOutbound()
  {
	  return outbound;
  }
  public void appendOutbound(ArrayList<String> outappend)
  {
	  for(int i=0;i<outappend.size();i++)
	  {
		  this.outbound.add(outappend.get(i));
	  }
  }
  public boolean isDeadEnd()
  {
	  if(this.outbound.size()==0)
	  {
		  return true;
	  }
	  else
	  {
		  return false;
	  }
  }
  public boolean isStart()
  {
	  if(this.inbound.size()==0)
	  {
		  return true;
	  }
	  else
	  {
		  return false;
	  }
  }
  
	
}
