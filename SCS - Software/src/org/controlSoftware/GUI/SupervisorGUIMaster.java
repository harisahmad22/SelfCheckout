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
import org.lsmr.selfcheckout.devices.SupervisionStation;

public class SupervisorGUIMaster {
	private SupervisionStation station;
	private AttendantData attendantData;
	
	private JFrame frame;
	
	private final int WIDTH = 1000;
	private final int HEIGHT = 600;
	
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
		case LOG_IN:
			loginScreen();
			break;
		default:
			break;
		}
		//frame.setVisible(true);
		station.screen.setVisible(true);
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
					attendantData.changeState(AttendantState.INACTIVE);//PLACEHOLDER
				}
			});
			
			b2.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					attendantData.changeState(AttendantState.INACTIVE);//PLACEHOLDER
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
}