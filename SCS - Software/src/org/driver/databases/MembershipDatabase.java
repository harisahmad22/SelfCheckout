package org.driver.databases;

import java.util.HashMap;
import java.util.Map;



public class MembershipDatabase {
	
	private Map<String, Integer> membershipDB = new HashMap<String, Integer>();

	public MembershipDatabase()
	{
		addMember("1", 50);
		addMember("2", 1250); 
		addMember("3", 25000); 
		addMember("4", 0);
	}
	
	public void addMember(String memberID, Integer points)
	{		
		membershipDB.put(memberID, points);
	}
	
	public Map<String, Integer> getDatabase()
	{
		return membershipDB;
	}
	
}
