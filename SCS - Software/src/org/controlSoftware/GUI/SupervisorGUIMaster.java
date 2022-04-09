package org.controlSoftware.GUI;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.driver.AttendantData;
import org.driver.AttendantData.AttendantState;
import org.driver.SelfCheckoutData.StationState;
import org.driver.SelfCheckoutStationUnit;
import org.lsmr.selfcheckout.devices.SupervisionStation;

public class SupervisorGUIMaster {
	private SupervisionStation station;
	private AttendantData attendantData;
	
	private JFrame frame;
	
	private final int WIDTH = 1000;
	private final int HEIGHT = 600;
	
	// Selected station is stored here to be target of additional operations
	private SelfCheckoutStationUnit targetStation;
	
	public SupervisorGUIMaster(SupervisionStation newStation, AttendantData newData) {
		station = newStation;
		attendantData = newData;
		attendantData.registerGUI(this);
		
		frame = station.screen.getFrame();
		frame.setSize(WIDTH,HEIGHT);
		frame.setLayout(null);
	}
	
	public void stateChanged() {
		frame.getContentPane().removeAll();
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
		
		switch (attendantData.getCurrentState()) {
		case START:
			startScreen();
			break;
		case LOG_IN:
			loginScreen();
			break;
		case STATIONS:
			stationsScreen();
			break;
		case ACTIVE:
			optionsScreen();
		default:
			break;
		}
		//frame.setVisible(true);
		station.screen.setVisible(true);
	}

	private void startScreen() {
		frame.setLayout(null);
		
		final JLabel l1 = new JLabel("PLEASE LOG IN");
		l1.setFont(new Font("Tahoma", Font.PLAIN, 40));
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setBounds(0, 0, 1000, 300);
		frame.getContentPane().add(l1);
		
		final JButton b1 = new JButton("LOG IN");
		b1.setFont(new Font("Tahoma", Font.PLAIN, 40));
		b1.setBounds(400,300,200,100);
		frame.getContentPane().add(b1);
		
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.LOG_IN);
			}
		});
	}
	
	// Screen with keypad. Outputs value typed to SelfCheckoutData.guiBuffer
	private void loginScreen(){

		frame.setLayout(null);
		
		final JButton b1 = new JButton("CANCEL");
		b1.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b1.setBounds(275, 450, 200, 100);
		frame.getContentPane().add(b1);
		
		final JButton b2 = new JButton("CONFIRM");
		b2.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b2.setBounds(525, 450, 200, 100);
		frame.getContentPane().add(b2);
		
		final JLabel l1 = new JLabel("");
		l1.setFont(new Font("Tahoma", Font.PLAIN, 40));
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setBounds(0, 0, 1000, 100);
		frame.getContentPane().add(l1);
		
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.START);
			}
		});
		
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.STATIONS);//PLACEHOLDER FUNCTIONALITY MUST BE ADDED TO TEST INPUT LOGIN
				attendantData.setGuiBuffer(l1.getText());
			}
		});
		
		// Keypad
		
		final String numString = "";
		
		ActionListener keyList = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton src = (JButton) e.getSource();
				String val = src.getText();
				if (val == "DEL") {
					l1.setText(l1.getText().substring(0, l1.getText().length()-1));
				}
				else {
					if (l1.getText().length() < 12) {
						l1.setText(l1.getText() + val);
					}
				}
				
			}
		};
		
		JButton[] keypad = new JButton[9];
		for(Integer i = 0; i < keypad.length; i++) {
			keypad[i]  = new JButton(String.valueOf((i+1) % 10));
			keypad[i].setFont(new Font("Tahoma", Font.PLAIN, 36));
			keypad[i].setBounds(275 + (i % 3) * 150, 100 + (int) Math.floor(i.floatValue() / 3.0) * 75, 150, 75);
			keypad[i].addActionListener(keyList);
			
			frame.getContentPane().add(keypad[i]);
		}
		
		JButton b3 = new JButton("DEL");
		b3.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b3.setBounds(275, 325, 150, 75);
		b3.addActionListener(keyList);
		frame.getContentPane().add(b3);
		
		JButton b4 = new JButton("0");
		b4.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b4.setBounds(425, 325, 150, 75);
		b4.addActionListener(keyList);
		frame.getContentPane().add(b4);
	}
	
	private void stationsScreen() {
		final JButton b13 = new JButton("LOGOUT");
		b13.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b13.setBounds(400, 475, 200, 100);
		frame.getContentPane().add(b13);
		b13.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.START);
			}
		});
		
		final JLabel l1 = new JLabel("Select station to manage.");
		l1.setFont(new Font("Tahoma", Font.PLAIN, 40));
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setBounds(0, 0, 1000, 100);
		frame.getContentPane().add(l1);
		
		ActionListener buttList = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton src = (JButton) e.getSource();
				String val = src.getText();
				int stationIndex = Integer.parseInt(val) - 1;
				targetStation = attendantData.getUnitAt(stationIndex);
				attendantData.changeState(AttendantState.ACTIVE);
			}
		};
		
		JButton[] stationButtons = new JButton[12];
		for(Integer i = 0; i < stationButtons.length; i++) {
			stationButtons[i]  = new JButton(String.valueOf(i+1));
			stationButtons[i].setFont(new Font("Tahoma", Font.PLAIN, 36));
			stationButtons[i].setBounds(190 + (i % 3) * 210, 100 + (int) Math.floor(i.floatValue() / 3.0) * 90, 200, 80);
			stationButtons[i].addActionListener(buttList);
			// Only show buttons which have corresponding SCS unit
			if (attendantData.getUnitAt(i) != null) {
				frame.getContentPane().add(stationButtons[i]);
			}
		}
		
	}
	
	private void optionsScreen() {
		final JButton b1 = new JButton("BACK");
		b1.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b1.setBounds(275, 475, 200, 100);
		frame.getContentPane().add(b1);
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.STATIONS);
			}
		});
		
		final JButton b2 = new JButton("LOGOUT");
		b2.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b2.setBounds(525, 475, 200, 100);
		frame.getContentPane().add(b2);
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.START);
			}
		});
		
		final JLabel l1 = new JLabel("Select station to manage.");
		l1.setFont(new Font("Tahoma", Font.PLAIN, 40));
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setBounds(0, 0, 1000, 100);
		frame.getContentPane().add(l1);
		
		//Initialize list of buttons to appear on this screen. Always at least the enable/disable button.
		JButton[] optionButtons = new JButton[1];
		optionButtons[0]  = new JButton();
		optionButtons[0].setFont(new Font("Tahoma", Font.PLAIN, 36));
		// Text dependent on whether target station is enabled/disabled
		if (targetStation.getSelfCheckoutData().getCurrentState() == StationState.INACTIVE) {
			optionButtons[0].setText("START UP STATION");
		}
		else {
			optionButtons[0].setText("SHUT DOWN STATION");
		}
		optionButtons[0].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (targetStation.getSelfCheckoutData().getCurrentState() == StationState.INACTIVE) {
					targetStation.getSelfCheckoutData().changeState(StationState.WELCOME);
					attendantData.changeState(AttendantState.ACTIVE);
				}
				else {
					targetStation.getSelfCheckoutData().changeState(StationState.INACTIVE);
					attendantData.changeState(AttendantState.ACTIVE);
				}
			}
		});
		
		for(Integer i = 0; i < optionButtons.length; i++) {
			optionButtons[i].setBounds(75 + (i % 2) * 450, 100 + (int) Math.floor(i.floatValue() / 2.0) * 90, 400, 80);
			frame.getContentPane().add(optionButtons[i]);
		}
		
	}
}
