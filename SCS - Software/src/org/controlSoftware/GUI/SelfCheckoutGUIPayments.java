package org.controlSoftware.GUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import org.driver.SelfCheckoutData;
import org.driver.SelfCheckoutData.StationState;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

public class SelfCheckoutGUIPayments {
	
	private SelfCheckoutStation station;
	private SelfCheckoutData stationData;
	
	private JFrame frame;
	private int width;
	private int height;
	

	public SelfCheckoutGUIPayments (SelfCheckoutStation newStation, SelfCheckoutData newData) {
		station = newStation;
		stationData = newData;
		
		frame = station.screen.getFrame();
		width = frame.getWidth();
		height = frame.getHeight();		
	}
	
	
	public void stateChanged() {
		switch (stationData.getCurrentState()) {
		case PAY_CASH:
			payCashScreen();
			break;
		case PAY_CREDIT:
			payCreditScreen();
			break;
		case PAY_DEBIT:
			payDebitScreen();
			break;
		default:
			break;
		}
		//frame.setVisible(true);
		station.screen.setVisible(true);
	}
	
	private void payCashScreen(){
		frame.setLayout(null);
		
		JLabel l1 = new JLabel("Please insert your");
		l1.setVerticalAlignment(SwingConstants.BOTTOM);
		l1.setFont(new Font("Tahoma", Font.PLAIN, 40));
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setBounds(0, 0, 1000, 150);
		frame.getContentPane().add(l1);
		
		JLabel l2 = new JLabel("banknotes/coins");
		l2.setVerticalAlignment(SwingConstants.TOP);
		l2.setHorizontalAlignment(SwingConstants.CENTER);
		l2.setFont(new Font("Tahoma", Font.PLAIN, 40));
		l2.setBounds(0, 150, 1000, 150);
		frame.getContentPane().add(l2);
		
		debugBanknoteButton();
		debugCoinButton();
		
	}
	
	private void debugCoinButton() {
		Color color = new Color(128, 128, 255);
		JButton payCoin = new JButton();
		payCoin.setBounds(350,150,300,200);
		payCoin.setText("[DEBUG] Pay $2 Coin");
		payCoin.setFont(new Font("Calibri", Font.BOLD,32));
		payCoin.setBackground(color);
		
		payCoin.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				try 
				{ stationData.getStationHardware().coinSlot.accept(stationData.toonie); } 
				catch (DisabledException e1) { System.out.println("Error! Banknote Input Disabled!"); } 
				catch (OverloadException e1) 
				{ 
					System.out.println("Error! Banknote Input Overloaded!");
					stationData.getStationHardware().banknoteInput.removeDanglingBanknotes();
				}
			}  
		});
		
		frame.add(payCoin);
		payCoin.setVisible(true);
	}
	
	private void debugBanknoteButton() {
		Color color = new Color(128, 128, 255);
		JButton payBanknote = new JButton();
		payBanknote.setBounds(50,150,300,200);
		payBanknote.setText("[DEBUG] Pay $5 Banknote");
		payBanknote.setFont(new Font("Calibri", Font.BOLD,32));
		payBanknote.setBackground(color);
		
		payBanknote.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				try 
				{ stationData.getStationHardware().banknoteInput.accept(stationData.fiveDollarBanknote); } 
				catch (DisabledException e1) { System.out.println("Error! Banknote Input Disabled!"); } 
				catch (OverloadException e1) { System.out.println("Error! Banknote Input Overloaded!");}
			}  
		});
		
		frame.add(payBanknote);
		payBanknote.setVisible(true);
	}
	
	private void payCreditScreen(){
		frame.setLayout(null);
		
		JLabel l1 = new JLabel("Please tap/swipe/insert your");
		l1.setVerticalAlignment(SwingConstants.BOTTOM);
		l1.setFont(new Font("Tahoma", Font.PLAIN, 40));
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setBounds(0, 0, 1000, 150);
		frame.getContentPane().add(l1);
		
		JLabel l2 = new JLabel("credit card");
		l2.setVerticalAlignment(SwingConstants.TOP);
		l2.setHorizontalAlignment(SwingConstants.CENTER);
		l2.setFont(new Font("Tahoma", Font.PLAIN, 40));
		l2.setBounds(0, 150, 1000, 150);
		frame.getContentPane().add(l2);
	}
	
	private void payDebitScreen(){
		frame.setLayout(null);
		
		JLabel l1 = new JLabel("Please tap/swipe/insert your");
		l1.setVerticalAlignment(SwingConstants.BOTTOM);
		l1.setFont(new Font("Tahoma", Font.PLAIN, 40));
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setBounds(0, 0, 1000, 150);
		frame.getContentPane().add(l1);
		
		JLabel l2 = new JLabel("debit card");
		l2.setVerticalAlignment(SwingConstants.TOP);
		l2.setHorizontalAlignment(SwingConstants.CENTER);
		l2.setFont(new Font("Tahoma", Font.PLAIN, 40));
		l2.setBounds(0, 150, 1000, 150);
		frame.getContentPane().add(l2);
	}
}
