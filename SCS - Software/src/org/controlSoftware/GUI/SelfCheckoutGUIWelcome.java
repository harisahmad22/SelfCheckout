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
		switch (stationData.getCurrentState()) {
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
		case ADD_BAGS_PROMPT:
			askBagsScreen();
			break;
		case ADDING_BAGS:
			addingBagsScreen();
			break;
		case FINISHED:
			finishedScreen();
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
		b1.setBounds(400,300,200,100);
		frame.getContentPane().add(b1);
		
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stationData.changeState(StationState.NORMAL);
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
				stationData.changeState(StationState.SCAN_MEMBERSHIP);
			}
		});
		
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stationData.changeState(StationState.CHECKOUT);
				stationData.getStationSoftware().getCheckoutHandler().startCheckout();
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
				stationData.changeState(StationState.ASK_MEMBERSHIP);
			}
		});
		
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stationData.changeState(StationState.TYPE_MEMBERSHIP);
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
				stationData.changeState(StationState.SCAN_MEMBERSHIP);
			}
		});
		
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stationData.setMembershipID(l1.getText());
//				stationData.changeState(StationState.TEST_MEMBERSHIP);
				stationData.changeState(StationState.CHECKOUT);//(Brody) for now just skip verification
				stationData.getStationSoftware().getCheckoutHandler().startCheckout();
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
				stationData.changeState(StationState.ADDING_BAGS);
			}
		});
		
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stationData.changeState(StationState.ASK_MEMBERSHIP);
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
				stationData.changeState(StationState.ADD_BAGS_PROMPT);
			}
		});
		
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stationData.changeState(StationState.ASK_MEMBERSHIP);
			}
		});
	}

	private void finishedScreen(){
		frame.setLayout(null);
		
		JLabel l1 = new JLabel("Thank you for shopping with us today");
		l1.setVerticalAlignment(SwingConstants.BOTTOM);
		l1.setFont(new Font("Tahoma", Font.PLAIN, 40));
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setBounds(0, 0, 1000, 150);
		frame.getContentPane().add(l1);
		
		JLabel l2 = new JLabel("Please take your bag(s) and your receipt");
		l2.setVerticalAlignment(SwingConstants.TOP);
		l2.setHorizontalAlignment(SwingConstants.CENTER);
		l2.setFont(new Font("Tahoma", Font.PLAIN, 40));
		l2.setBounds(0, 150, 1000, 150);
		frame.getContentPane().add(l2);
		
		Timer timer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
				stationData.changeState(StationState.WELCOME);
            }
        });
        timer.start();
		
	}
	
	
}
