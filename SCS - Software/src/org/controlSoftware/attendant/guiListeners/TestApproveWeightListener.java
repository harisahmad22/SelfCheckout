package org.controlSoftware.attendant.guiListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.controlSoftware.attendant.AttendantSoftware;

//A basic test class for implementing a listener we can bind to something like a button
//On some event, actionPerformed() will call the AddendantSoftware method
//responsible for approving a weight issue for the specified station

//E.g GUI code creates a new window and this listener is attached to X number of buttons. 

public class TestApproveWeightListener implements ActionListener {

	private AttendantSoftware attendantSoftware;

	public TestApproveWeightListener(AttendantSoftware attendantSoftware)
	{
		this.attendantSoftware = attendantSoftware; 
	}
	
	
	//In order to determine which station we will have to override
	//Check the command of this action event with e.getActionCommand()
	//Assuming that the buttons this listener will be attached to, specify 
	//the station via their command
	
	//E.g The attendant presses a 'Correct Weight Issue' button that appears on
	//their touch screen when the supervisor station is in its normal running state.
	//Pressing this button opens a new window that creates and displays a button for every checkout station 
	//that is connected to the supervisor station. 
	//Since the SupervisorStation class's supervisedStations() method returns a list of checkout stations
	//we can treat the first station in the list as station #0, the second as station #1, and so on.		
	//So then for each station we create button and set its command using the setActionCommand(String) to
	//be the corresponding station number. Allowing us to determine which station was chosen for the override. 

	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		String cmd = e.getActionCommand();
		int stationIndex;
		try {
			stationIndex = Integer.parseInt(cmd);
		} catch (NumberFormatException numberFormatException) {
			System.out.println("Error! Malformed button command!");
			return;
		}
		
		attendantSoftware.overrideWeightIssue(stationIndex); 
		//Attendant software will get the list of all connected 
		
	}

}
