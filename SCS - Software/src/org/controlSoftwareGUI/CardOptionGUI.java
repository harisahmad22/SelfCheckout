package org.controlSoftwareGUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.driver.*;

/**
 * @author Harrison Drew
 * Creates and manages the Card Option GUI
 */
public class CardOptionGUI {
	private SelfCheckoutStation scs;
	private JFrame frame;
	private JPanel panel;
	
	/**
	 * @param station
	 * Constructs the Payment Screen
	 */
	public CardOptionGUI(SelfCheckoutStation station) {
		scs = station;
		frame = scs.screen.getFrame();
		frame.setSize(1920, 1080);
		frame.setLayout(null);  
		panel = new JPanel();
		panel.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 10));
		panel.setBounds(25, 250, 1870, 805);
		
		insertButton();
		swipeButton();
		tapButton();
		backButton();
		assistanceButton();
		frame.add(panel);
		JLabel title = new JLabel("Select Payment Type");  
	    title.setBounds(50,50, 1500,200);  
	    title.setFont(new Font("Calibri", Font.BOLD,150));
	    frame.add(title);
	}

	/**
	 * Shows the Screen
	 */
	public void showPaymentOptionGUI() {
		frame.setVisible(true);
	}
	
	/**
	 * Hides the Screen
	 */
	public void hidePaymentOptionGUI() {
		frame.setVisible(false);
	}
	
	/**
	 * Creates a Insert Button
	 */
	private void insertButton() {
		Color color = new Color(255, 255, 128);
		JButton insert = new JButton();
		insert.setBounds(50,300,600,400);
		insert.setText("Insert Card");
		insert.setFont(new Font("Calibri", Font.BOLD,64));
		insert.setBackground(color);
		
		insert.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				//insert card
				
			}  
		});
		
		frame.add(insert);
		insert.setVisible(true);
		
	}
	
	/**
	 * Creates a Swipe Button
	 */
	private void swipeButton() {
		Color color = new Color(128, 128, 255);
		JButton swipe = new JButton();
		swipe.setBounds(650,300,600,400);
		swipe.setText("Swipe Card");
		swipe.setFont(new Font("Calibri", Font.BOLD,64));
		swipe.setBackground(color);
		
		swipe.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				//swipe card
			}  
		});
		
		frame.add(swipe);
		swipe.setVisible(true);
		
	}
	
	/**
	 * Creates a Tap Button
	 */
	private void tapButton() {
		Color color = new Color(128, 255, 128);
		JButton tap = new JButton();
		tap.setBounds(1250,300,600,400);
		tap.setText("Tap Card");
		tap.setFont(new Font("Calibri", Font.BOLD,64));
		tap.setBackground(color);
		
		tap.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				//tap card
			}  
		});
		
		frame.add(tap);
		tap.setVisible(true);
		
	}
	
	/**
	 * Creates a Back Button
	 */
	private void backButton() {
		Color color = new Color(255, 128, 128);
		JButton back = new JButton();
		back.setBounds(50,800,400,200);
		back.setText("Go Back");
		back.setFont(new Font("Calibri", Font.BOLD,64));
		back.setBackground(color);
		
		back.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				//back to scanning/cart GUI
			}  
		});
		
		frame.add(back);
		back.setVisible(true);
		
	}
	
	/**
	 * Creates a Assistance Button
	 */
	private void assistanceButton() {
		Color color = new Color(255, 64, 64);
		JButton assistance = new JButton();
		assistance.setBounds(1450,800,400,200);
		assistance.setText("Call Attendant");
		assistance.setFont(new Font("Calibri", Font.BOLD,48));
		assistance.setBackground(color);
		
		assistance.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				//assistance prompt
			}  
		});
		
		frame.add(assistance);
		assistance.setVisible(true);
		
	}
	
}
