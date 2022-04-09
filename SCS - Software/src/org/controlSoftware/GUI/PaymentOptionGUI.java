package org.controlSoftware.GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.driver.*;
import org.driver.SelfCheckoutData.StationState;

/**
 * @author Harrison Drew
 * Creates and manages the Payment Option GUI
 */
public class PaymentOptionGUI {
	private SelfCheckoutStation scs;
	private SelfCheckoutData data;
	private JFrame frame;
	private JPanel panel;
	
	private int width;
	private int height;
	
	/**
	 * @param station
	 * @param data 
	 * Constructs the Payment Screen
	 */
	public PaymentOptionGUI(SelfCheckoutStation newStation, SelfCheckoutData newData) {
		scs = newStation;
		data = newData;
		
		frame = scs.screen.getFrame();
		width = frame.getWidth();
		height = frame.getHeight();		
	}
	
	public void stateChanged() {
		switch (data.getCurrentState()) {
		case CHECKOUT:
			checkoutScreen();
			break;
		default:
			break;
		}
		//frame.setVisible(true);
		scs.screen.setVisible(true);
	}
	
	private void checkoutScreen() {
		frame.setLayout(null);  
		panel = new JPanel();
		panel.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 10));
		panel.setBackground(Color.CYAN);
		panel.setBounds(10, 100, 980, 490);
		
		membershipCardButton();
		giftCardButton();
		debitCardButton();
		creditCardButton();
		cashButton();
		backButton();
		assistanceButton();
		frame.add(panel);
		JLabel title = new JLabel("Select Payment Type");  
	    title.setBounds(10,10,750,100);  
	    title.setFont(new Font("Calibri", Font.BOLD,75));
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
	 * Creates a Membership Card Button
	 */
	private void membershipCardButton() {
		Color color = new Color(255, 128, 255);
		JButton membership = new JButton();
		membership.setBounds(275,450,225,100);
		membership.setText("Scan Membership");
		membership.setFont(new Font("Calibri", Font.BOLD,25));
		membership.setBackground(color);
		
		membership.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				data.changeState(StationState.ASK_MEMBERSHIP);
			}  
		});
		
		frame.add(membership);
		membership.setVisible(true);
	}
	
	/**
	 * Creates a Gift Card Button
	 */
	private void giftCardButton() {
		Color color = new Color(255, 255, 255);
		JButton gift = new JButton();
		gift.setBounds(500,450,225,100);
		gift.setText("Gift Card");
		gift.setFont(new Font("Calibri", Font.BOLD,48));
		gift.setBackground(color);
		
		gift.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				//switch to gift card state
			}  
		});
		
		frame.add(gift);
		gift.setVisible(true);
	}
	
	/**
	 * Creates a Debit Card Button
	 */
	private void debitCardButton() {
		Color color = new Color(255, 255, 128);
		JButton debit = new JButton();
		debit.setBounds(50,150,300,200);
		debit.setText("Debit Card");
		debit.setFont(new Font("Calibri", Font.BOLD,48));
		debit.setBackground(color);
		
		debit.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  				
				data.changeState(StationState.PAY_DEBIT);
			}  
		});
		
		frame.add(debit);
		debit.setVisible(true);
		
	}
	
	/**
	 * Creates a Credit Card Button
	 */
	private void creditCardButton() {
		Color color = new Color(128, 128, 255);
		JButton credit = new JButton();
		credit.setBounds(350,150,300,200);
		credit.setText("Credit Card");
		credit.setFont(new Font("Calibri", Font.BOLD,48));
		credit.setBackground(color);
		
		credit.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				data.changeState(StationState.PAY_CREDIT);
			}  
		});
		
		frame.add(credit);
		credit.setVisible(true);
		
	}
	
	/**
	 * Creates a Cash Button
	 */
	private void cashButton() {
		Color color = new Color(128, 255, 128);
		JButton cash = new JButton();
		cash.setBounds(650,150,300,200);
		cash.setText("Cash");
		cash.setFont(new Font("Calibri", Font.BOLD,48));
		cash.setBackground(color);
		
		cash.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				data.changeState(StationState.PAY_CASH);
			}  
		});
		
		frame.add(cash);
		cash.setVisible(true);
		
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
				data.changeState(StationState.MAIN_SCAN);
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