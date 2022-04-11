package org.controlSoftware.GUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import org.driver.SelfCheckoutData;
import org.driver.SelfCheckoutData.StationState;
import org.driver.databases.ProductInfo;
import org.driver.databases.TestBarcodedProducts;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.InvalidArgumentSimulationException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.driver.AttendantData.AttendantState;

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
		case INACTIVE:
			frame.setVisible(false);
			break;
		case WELCOME:
			welcomeScreen();
			break;
		case ASK_MEMBERSHIP:
			askMembershipScreen();
			break;
		case SWIPE_MEMBERSHIP:
			scanMembershipScreen();
			break;
		case TYPE_MEMBERSHIP:
			typeMembershipScreen();
			break;
		case BAD_MEMBERSHIP:
			badMembershipScreen();
			break;
		case BAD_CARD:
			badCardScreen();
			break;
		case ADD_BAGS_PROMPT:
			askBagsScreen();
			break;
		case ADDING_BAGS:
			addingBagsScreen();
			break;
			
		case PRINT_RECEIPT_PROMPT:
			finishedScreen();
			break;
			
		case WEIGHT_ISSUE:
			weightIssueScreen();
			break;
			
		case BLOCKED:
			blockedScreen();
			break;
			
//		case CLEANUP:
//			finishedScreen();
//			break;
		default:
			break;
		}
		//frame.setVisible(true);
		station.screen.setVisible(true);
	}
	
	// Simple text only screen
	/*private void inactiveScreen(){
		frame.setLayout(null);
		
		final JLabel l1 = new JLabel("STATION IS INACTIVE");
		l1.setFont(new Font("Tahoma", Font.PLAIN, 40));
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setBounds(0, 0, 1000, 300);
		frame.getContentPane().add(l1);
	}*/
	
	

	private void weightIssueScreen() {
		frame.setLayout(null);
		
		final JLabel l1 = new JLabel("<html><center>WEIGHT ISSUE DETECTED!<br>PLEASE CORRECT ISSUE OR NOTIFY ATTENDANT<center></html>");
		l1.setFont(new Font("Tahoma", Font.PLAIN, 36));
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setBounds(0, 0, 1000, 300);
		frame.getContentPane().add(l1);	
		
		
		final JButton b1 = new JButton("[DEBUG] Unblock Station (via attendant override)");
		b1.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b1.setBounds(1000,300,300,100);
		frame.getContentPane().add(b1);
		
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stationData.getStationSoftware().performAttendantWeightOverride();
			}
		});
		
		
		final JButton b2 = new JButton("NOTIFY ATTENDANT");
		b2.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b2.setBounds(300,300,400,100);
		frame.getContentPane().add(b2);
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int stationNum = stationData.getAttendantUnit().getAttendantData().getUnitIndex(stationData.getThisUnit()) + 1;
				stationData.getAttendantUnit().getAttendantData().setGuiBuffer("Station " + stationNum + " has weight issue.");
				stationData.getAttendantUnit().getAttendantData().changeState(AttendantState.NOTIFIED_BY_STATION);
			}
		});
	}


	private void blockedScreen() {
		frame.setLayout(null);
		
		final JLabel l1 = new JLabel("<html><center>THIS STATION IS BLOCKED<br>PLEASE ASK ATTENDANT FOR ASSISTANCE<center></html>");
		l1.setFont(new Font("Tahoma", Font.PLAIN, 36));
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setBounds(0, 0, 1000, 300);
		frame.getContentPane().add(l1);	
		
		final JButton b1 = new JButton("[DEBUG] Unblock Station");
		b1.setFont(new Font("Tahoma", Font.PLAIN, 11));
		b1.setBounds(1000,300,300,100);
		frame.getContentPane().add(b1);
		
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stationData.getStationSoftware().unBlockStation();
			}
		});
	}
	
	// Simple text and one button screen.
	private void welcomeScreen(){
		frame.setLayout(null);
		
		final JLabel l1 = new JLabel("WELCOME");
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
				stationData.changeState(StationState.SWIPE_MEMBERSHIP);
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
				stationData.changeState(StationState.SWIPE_MEMBERSHIP);
			}
		});
		
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stationData.getStationSoftware().manualMembershipCheck(l1.getText());
//				stationData.setMembershipID(l1.getText());
//				stationData.getStationSoftware().getCheckoutHandler().startCheckout();
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
		
		JLabel l1 = new JLabel("Thank you for shopping with us today.");
		l1.setVerticalAlignment(SwingConstants.BOTTOM);
		l1.setFont(new Font("Tahoma", Font.PLAIN, 40));
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setBounds(0, 0, 1000, 150);
		frame.getContentPane().add(l1);
		
		JLabel l2 = new JLabel("Please take your bag(s) and your receipt.");
		l2.setVerticalAlignment(SwingConstants.TOP);
		l2.setHorizontalAlignment(SwingConstants.CENTER);
		l2.setFont(new Font("Tahoma", Font.PLAIN, 40));
		l2.setBounds(0, 150, 1000, 150);
		frame.getContentPane().add(l2);
		
//		Check if more money needs to be paid 
		if (stationData.getTotalMoneyPaid().compareTo(stationData.getTotalDue()) < 0)
		{
			stationData.resetTotalPaidThisTransaction();
			stationData.changeState(StationState.NORMAL);
		}
		else
		{
			debugRemoveItemsFromBaggingAreaButton();
			
			takeReceiptButton();
			
			//Print Receipt
			stationData.getStationSoftware().getReceiptHandler().setMembershipID(stationData.getMembershipID());
			stationData.getStationSoftware().getReceiptHandler().printReceipt();
		}
	}
	
	private void printTotals()
    {
    	System.out.println("!!! total due: " + stationData.getTotalDue());
		System.out.println("!!! total money paid: " + stationData.getTotalMoneyPaid());
		System.out.println("!!! total paid this transaction: " + stationData.getTotalPaidThisTransaction());
		System.out.println("!!! transaction amount: " + stationData.getTransactionPaymentAmount());
    }
	//BRODY
	private void debugRemoveItemsFromBaggingAreaButton() {
		Color color = new Color(128, 128, 255);
		JButton payCoin = new JButton();
		payCoin.setBounds(1000,300,300,200);
		payCoin.setText("[DEBUG] Remove All Items");
		payCoin.setFont(new Font("Calibri", Font.BOLD, 16));
		payCoin.setBackground(color);
		
		payCoin.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				
				try { stationData.getStationHardware().baggingArea.remove(stationData.getTestProducts().getItemList().get(0)); }
				catch (InvalidArgumentSimulationException e1)
				{
					//For Testing, just reset to welcome screen
					stationData.changeState(StationState.WELCOME);
				}
			}  
		});
		
		frame.add(payCoin);
		payCoin.setVisible(true);
	}
	
	//BRODY
	private void takeReceiptButton() {
		Color color = new Color(128, 128, 255);
		JButton payCoin = new JButton();
		payCoin.setBounds(1000,0,300,200);
		payCoin.setText("Take Receipt (Will print it to console)");
		payCoin.setFont(new Font("Calibri", Font.BOLD, 14));
		payCoin.setBackground(color);
		
		payCoin.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				
				try {
					String receipt = stationData.getStationHardware().printer.removeReceipt();
					System.out.println("RECEIPT GENERATED: \n" + receipt);
				} catch (InvalidArgumentSimulationException execption)
				{
					System.out.println("Error! No receipt to take!");
				}
				
				
			}  
		});
		
		frame.add(payCoin);
		payCoin.setVisible(true);
	}

	private void badMembershipScreen() {
		frame.setLayout(null);

		JLabel l1 = new JLabel("Membership is invalid. Would you like to");
		l1.setVerticalAlignment(SwingConstants.BOTTOM);
		l1.setFont(new Font("Tahoma", Font.PLAIN, 36));
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setBounds(0, 0, 1000, 150);
		frame.getContentPane().add(l1);

		JLabel l2 = new JLabel("try again or return to the main screen?");
		l2.setVerticalAlignment(SwingConstants.TOP);
		l2.setHorizontalAlignment(SwingConstants.CENTER);
		l2.setFont(new Font("Tahoma", Font.PLAIN, 36));
		l2.setBounds(0, 150, 1000, 150);
		frame.getContentPane().add(l2);

		final JButton b1 = new JButton("RETURN");
		b1.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b1.setBounds(225,300,250,100);
		frame.getContentPane().add(b1);

		final JButton b2 = new JButton("TRY AGAIN");
		b2.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b2.setBounds(525,300,250,100);
		frame.getContentPane().add(b2);

		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stationData.setIsFirstCheckout(true);
				stationData.changeState(StationState.NORMAL);
			}
		});

		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stationData.changeState(StationState.SWIPE_MEMBERSHIP);
			}
		});
	}
	
	private void badCardScreen() {
		frame.setLayout(null);

		JLabel l1 = new JLabel("Card is Invalid. Would you like to");
		l1.setVerticalAlignment(SwingConstants.BOTTOM);
		l1.setFont(new Font("Tahoma", Font.PLAIN, 36));
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setBounds(0, 0, 1000, 150);
		frame.getContentPane().add(l1);

		JLabel l2 = new JLabel("try again or return to the main screen?");
		l2.setVerticalAlignment(SwingConstants.TOP);
		l2.setHorizontalAlignment(SwingConstants.CENTER);
		l2.setFont(new Font("Tahoma", Font.PLAIN, 36));
		l2.setBounds(0, 150, 1000, 150);
		frame.getContentPane().add(l2);

		final JButton b1 = new JButton("RETURN");
		b1.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b1.setBounds(225,300,250,100);
		frame.getContentPane().add(b1);

		final JButton b2 = new JButton("TRY AGAIN");
		b2.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b2.setBounds(525,300,250,100);
		frame.getContentPane().add(b2);

		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stationData.setIsFirstCheckout(true);
				stationData.changeState(StationState.NORMAL);
			}
		});

		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stationData.changeState(StationState.PAYMENT_MODE_PROMPT);
			}
		});
	}


}
