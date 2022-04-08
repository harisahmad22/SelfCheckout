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
import org.driver.SelfCheckoutData.State;
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
		
		switch (stationData.getState()) {
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
