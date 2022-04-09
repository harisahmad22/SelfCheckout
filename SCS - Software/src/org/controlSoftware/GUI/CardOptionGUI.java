package org.controlSoftware.GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.driver.*;
import org.driver.SelfCheckoutData.StationState;

/**
 * @author Harrison Drew
 * Creates and manages the Card Option GUI
 */
public class CardOptionGUI {
	private SelfCheckoutStation scs;
	private SelfCheckoutData data;
	private JFrame frame;
	private JPanel panel;
	
	/**
	 * @param station
	 * @param data 
	 * Constructs the Card Screen
	 */
	public CardOptionGUI(SelfCheckoutStation station, SelfCheckoutData data) {
		scs = station;
		this.data = data;
		frame = scs.screen.getFrame();
		frame.setSize(1000, 600);
		frame.setPreferredSize(new Dimension(1920, 1080));
		frame.setLayout(null);  
		panel = new JPanel();
		panel.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 10));
		panel.setBackground(Color.CYAN);
		panel.setBounds(10, 100, 980, 490);
		
		insertCardButton();
		swipeCardButton();
		tapButton();
		backButton();
		assistanceButton();
		frame.add(panel);
		JLabel title = new JLabel("Select Payment Type");  
	    title.setBounds(10,10,750,100);  
	    title.setFont(new Font("Calibri", Font.BOLD,75));
	    frame.add(title);
	}

	/**
	 * Redraws upon state change
	 */
	public void stateChanged() {
		frame.getContentPane().removeAll();
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
	}
	
	/**
	 * Shows the Screen
	 */
	public void showCardOptionGUI() {
		frame.setVisible(true);
	}
	
	/**
	 * Hides the Screen
	 */
	public void hideCardOptionGUI() {
		frame.setVisible(false);
	}
	
	
	/**
	 * Creates an Insert Card Button
	 */
	private void insertCardButton() {
		Color color = new Color(255, 255, 128);
		JButton insert = new JButton();
		insert.setBounds(50,150,300,200);
		insert.setText("Insert Card");
		insert.setFont(new Font("Calibri", Font.BOLD,48));
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
	 * Creates a Swipe Card Button
	 */
	private void swipeCardButton() {
		Color color = new Color(128, 128, 255);
		JButton swipe = new JButton();
		swipe.setBounds(350,150,300,200);
		swipe.setText("Swipe Card");
		swipe.setFont(new Font("Calibri", Font.BOLD,48));
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
		tap.setBounds(650,150,300,200);
		tap.setText("Tap");
		tap.setFont(new Font("Calibri", Font.BOLD,48));
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
		back.setBounds(50,450,225,100);
		back.setText("Go Back");
		back.setFont(new Font("Calibri", Font.BOLD,48));
		back.setBackground(color);
		
		back.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				data.changeState(StationState.CHECKOUT);
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
		assistance.setBounds(725,450,225,100);
		assistance.setText("Call Attendant");
		assistance.setFont(new Font("Calibri", Font.BOLD,32));
		assistance.setBackground(color);
		
		assistance.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				//change state to attendant state
			}  
		});
		
		frame.add(assistance);
		assistance.setVisible(true);
		
	}
	
}