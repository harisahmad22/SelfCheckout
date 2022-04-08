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

import org.driver.SelfCheckoutData;
import org.driver.SelfCheckoutData.State;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

public class SelfCheckoutGUIWelcome {
	private SelfCheckoutStation station;
	private SelfCheckoutData stationData;
	
	private JFrame frame;
	private int width;
	private int height;
	
	public SelfCheckoutGUIWelcome(SelfCheckoutStation newStation, SelfCheckoutData newData) {
		station = newStation;
		stationData = newData;
		
		frame = station.screen.getFrame();
		width = frame.getWidth();
		height = frame.getHeight();		
	}
	
	public void stateChanged() {
		frame.getContentPane().removeAll();
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
		
		switch (stationData.getState()) {
		case WELCOME:
			welcomeScreen();
			break;
		case ASK_MEMBERSHIP:
			askMembershipScreen();
			break;
		case SCAN_MEMBERSHIP:
			scanMembershipScreen();
			break;
		case TYPE_MEMBERSHIP:
			typeMembershipScreen();
			break;
		case ASK_BAGS:
			askBagsScreen();
			break;
		case ADDING_BAGS:
			addingBagsScreen();
			break;
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
	
	// Simple text and one button screen.
	private void welcomeScreen(){
		frame.setLayout(null);
		
		final JLabel l1 = new JLabel("WELCOME SCREEN TEXT");
		l1.setFont(new Font("Tahoma", Font.PLAIN, 40));
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setBounds(0, 0, 1000, 300);
		frame.getContentPane().add(l1);
		
		final JButton b1 = new JButton("BEGIN");
		b1.setFont(new Font("Tahoma", Font.PLAIN, 40));
		// originally 400,300,200,100
		b1.setBounds(275, 300, 200, 100);
		frame.getContentPane().add(b1);
		
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stationData.changeState(State.ASK_MEMBERSHIP);
			}
		});
		
		final JButton b2 = new JButton("TEMP");
		b2.setFont(new Font("Tahoma", Font.PLAIN, 40));
		// originally 400,300,200,100
		b2.setBounds(525, 300, 200, 100);
		frame.getContentPane().add(b2);
		
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stationData.changeState(State.PAY_CASH);
			}
		});
		
		

	}
	
	// Simple text and two button screen.
	private void askMembershipScreen(){
		frame.setLayout(null);
		
		JLabel l1 = new JLabel("Are you a member?");
		l1.setFont(new Font("Tahoma", Font.PLAIN, 40));
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setBounds(0, 0, 1000, 300);
		frame.getContentPane().add(l1);
		
		final JButton b1 = new JButton("YES");
		b1.setFont(new Font("Tahoma", Font.PLAIN, 40));
		b1.setBounds(275, 300, 200, 100);
		frame.getContentPane().add(b1);
		
		final JButton b2 = new JButton("NO");
		b2.setFont(new Font("Tahoma", Font.PLAIN, 40));
		b2.setBounds(525, 300, 200, 100);
		frame.getContentPane().add(b2);
		
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stationData.changeState(State.SCAN_MEMBERSHIP);
			}
		});
		
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stationData.changeState(State.ASK_BAGS);
			}
		});

	}
	
	// Two lines of text and then two buttons.
	private void scanMembershipScreen(){
		frame.setLayout(null);

		JLabel l1 = new JLabel("Scan your membership card now, or press");
		l1.setVerticalAlignment(SwingConstants.BOTTOM);
		l1.setFont(new Font("Tahoma", Font.PLAIN, 40));
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setBounds(0, 0, 1000, 150);
		frame.getContentPane().add(l1);
		
		JLabel l2 = new JLabel("the button to enter your number manually.");
		l2.setVerticalAlignment(SwingConstants.TOP);
		l2.setHorizontalAlignment(SwingConstants.CENTER);
		l2.setFont(new Font("Tahoma", Font.PLAIN, 40));
		l2.setBounds(0, 150, 1000, 150);
		frame.getContentPane().add(l2);
		
		final JButton b1 = new JButton("CANCEL");
		b1.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b1.setBounds(200, 300, 200, 100);
		frame.getContentPane().add(b1);
		
		final JButton b2 = new JButton("ENTER MANUALLY");
		b2.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b2.setBounds(450, 300, 350, 100);
		frame.getContentPane().add(b2);
		
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stationData.changeState(State.ASK_MEMBERSHIP);
			}
		});
		
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stationData.changeState(State.TYPE_MEMBERSHIP);
			}
		});

	}
	
	// Screen with keypad. Outputs value typed to SelfCheckoutData.guiBuffer
	private void typeMembershipScreen(){

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
				stationData.changeState(State.SCAN_MEMBERSHIP);
			}
		});
		
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stationData.changeState(State.TEST_MEMBERSHIP);
				stationData.setGuiBuffer(l1.getText());
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
	
	private void askBagsScreen(){
		frame.setLayout(null);
		
		JLabel l1 = new JLabel("Do you wish to use your own bags?");
		l1.setFont(new Font("Tahoma", Font.PLAIN, 40));
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setBounds(0, 0, 1000, 300);
		frame.getContentPane().add(l1);
		
		final JButton b1 = new JButton("YES");
		b1.setFont(new Font("Tahoma", Font.PLAIN, 40));
		b1.setBounds(275, 300, 200, 100);
		frame.getContentPane().add(b1);
		
		final JButton b2 = new JButton("NO");
		b2.setFont(new Font("Tahoma", Font.PLAIN, 40));
		b2.setBounds(525, 300, 200, 100);
		frame.getContentPane().add(b2);
		
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stationData.changeState(State.ADDING_BAGS);
			}
		});
		
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stationData.changeState(State.SCANNING);
			}
		});

	}
	
	private void addingBagsScreen(){
		frame.setLayout(null);

		JLabel l1 = new JLabel("Place your bags in the bagging");
		l1.setVerticalAlignment(SwingConstants.BOTTOM);
		l1.setFont(new Font("Tahoma", Font.PLAIN, 40));
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setBounds(0, 0, 1000, 150);
		frame.getContentPane().add(l1);
		
		JLabel l2 = new JLabel("area and press CONFIRM.");
		l2.setVerticalAlignment(SwingConstants.TOP);
		l2.setHorizontalAlignment(SwingConstants.CENTER);
		l2.setFont(new Font("Tahoma", Font.PLAIN, 40));
		l2.setBounds(0, 150, 1000, 150);
		frame.getContentPane().add(l2);
		
		final JButton b1 = new JButton("CANCEL");
		b1.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b1.setBounds(275, 300, 200, 100);
		frame.getContentPane().add(b1);
		
		final JButton b2 = new JButton("CONFIRM");
		b2.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b2.setBounds(525, 300, 200, 100);
		frame.getContentPane().add(b2);
		
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stationData.changeState(State.ASK_BAGS);
			}
		});
		
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stationData.changeState(State.ADDED_BAGS);
			}
		});
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
