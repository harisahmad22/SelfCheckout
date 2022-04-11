package org.driver.databases;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.lsmr.selfcheckout.Numeral;

public class AttendantDatabase {

	private ArrayList<String> database;
	
	public AttendantDatabase() {
		this.database = new ArrayList<String>();
		
		database.add("1");
		database.add("2");
		database.add("3");
		database.add("4");
	}

	public ArrayList<String> getDatabase() {
		return database;
	}
	
	public void addAttendantToDatabase(String attendantID) {
		database.add(attendantID);
	}
	
	public void removeAttendantFromDatabase(String attendantID) {
		database.remove(attendantID);
	}
}
