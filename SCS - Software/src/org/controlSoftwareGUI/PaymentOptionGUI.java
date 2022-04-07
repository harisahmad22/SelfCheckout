package org.controlSoftwareGUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.driver.*;

/**
 * @author Harrison Drew
 * Creates and manages the Payment Option GUI
 */
public class PaymentOptionGUI {
	private SelfCheckoutStation scs;
	private JFrame frame;
	private JPanel panel;
	
	/**
	 * @param station
	 * Constructs the Payment Screen
	 */
	public PaymentOptionGUI(SelfCheckoutStation station) {
		scs = station;
		frame = scs.screen.getFrame();
		frame.setSize(1920, 1080);
		frame.setLayout(null);  
		panel = new JPanel();
		panel.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 10));
		panel.setBounds(25, 250, 1870, 805);
		
		membershipCardButton();
		giftCardButton();
		debitCardButton();
		creditCardButton();
		cashButton();
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
	 * Creates a Membership Card Button
	 */
	private void membershipCardButton() {
		Color color = new Color(255, 128, 255);
		JButton membership = new JButton();
		membership.setBounds(525,800,400,200);
		membership.setText("Scan Membership Card");
		membership.setFont(new Font("Calibri", Font.BOLD,36));
		membership.setBackground(color);
		
		membership.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				//membership card logic
			}  
		});
		
		frame.add(membership);
		membership.setVisible(true);
	}
	
	/**
	 * Creates a Membership Card Button
	 */
	private void giftCardButton() {
		Color color = new Color(255, 255, 255);
		JButton gift = new JButton();
		gift.setBounds(975,800,400,200);
		gift.setText("Gift Card");
		gift.setFont(new Font("Calibri", Font.BOLD,64));
		gift.setBackground(color);
		
		gift.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				//gift card logic
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
		debit.setBounds(50,300,600,400);
		debit.setText("Debit Card");
		debit.setFont(new Font("Calibri", Font.BOLD,64));
		debit.setBackground(color);
		
		debit.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  				
				//card options screen
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
		credit.setBounds(650,300,600,400);
		credit.setText("Credit Card");
		credit.setFont(new Font("Calibri", Font.BOLD,64));
		credit.setBackground(color);
		
		credit.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				//card options screen
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
		cash.setBounds(1250,300,600,400);
		cash.setText("Cash");
		cash.setFont(new Font("Calibri", Font.BOLD,64));
		cash.setBackground(color);
		
		cash.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				//cash payment GUI
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
