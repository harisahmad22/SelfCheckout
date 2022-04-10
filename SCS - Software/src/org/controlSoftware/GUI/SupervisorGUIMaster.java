package org.controlSoftware.GUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.driver.AttendantData;
import org.driver.AttendantData.AttendantState;
import org.driver.SelfCheckoutData.StationState;
import org.driver.databases.ProductInfo;
import org.driver.databases.TestBarcodedProducts;
import org.driver.SelfCheckoutStationUnit;
import org.lsmr.selfcheckout.devices.SupervisionStation;
import org.lsmr.selfcheckout.products.BarcodedProduct;

public class SupervisorGUIMaster {
	private SupervisionStation station;
	private AttendantData attendantData;
	
	private JFrame frame;
	
	private final int WIDTH = 1000;
	private final int HEIGHT = 600;
	
	// Index for cycling through this of items for remove item.
	private int itemListIndex = 0;
	
	// Selected station is stored here to be target of additional operations
	private SelfCheckoutStationUnit targetStation;
	
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
		case START:
			startScreen();
			break;
		case LOG_IN:
			loginScreen();
			break;
		case STATIONS:
			stationsScreen();
			break;
		case ACTIVE:
			optionsScreen();
			break;
		case PRODUCT_LOOKUP:
			searchItemScreen();
			break;
		case REMOVE_ITEM_AT_STATION:
			removeItemScreen();
			break;
		case MAINTENANCE:
			maintenanceScreen();
			break;
		case WEIGHT_ERROR:
			weightErrorScreen();
			break;
		default:
			break;
		}
		//frame.setVisible(true);
		station.screen.setVisible(true);
	}
	
	private void weightErrorScreen() {
		frame.setLayout(null);

		
	}
	private void maintenanceScreen() {
		frame.setLayout(null);

		
	}
	private void searchItemScreen() {
		frame.setLayout(null);
		
		
	}
	
	private void removeItemScreen() {
		frame.setLayout(null);
		
		//Display scanned items
		HashMap<String, ProductInfo> currentAddedProducts = targetStation.getSelfCheckoutData().getProductsAddedToCheckoutHashMap();
		String descriptionString = "<html><br>";
		String quantityString = "<html><br>";
		String priceString = "<html><br>";
		
		ArrayList<String> items = new ArrayList<String>(currentAddedProducts.keySet());
		for (int i = itemListIndex; i < itemListIndex + 5; i++)
		//for (String prodDescription : currentAddedProducts.keySet())
		{
			if (i >= items.size()) {
				break;
			}
			String prodDescription = items.get(i);
			descriptionString += prodDescription + "<br><br>";
			quantityString += "x " + currentAddedProducts.get(prodDescription).getQuantity() + "<br><br>";
			priceString += "$" + currentAddedProducts.get(prodDescription).getProduct().getPrice() + "<br><br>";					  
		}
		
		descriptionString += "</html>";
		quantityString += "</html>";
		priceString += "</html>";
		
		JLabel itemList = new JLabel(descriptionString);
		itemList.setBounds(25,25,625,425);
		itemList.setVerticalAlignment(JLabel.TOP);
		itemList.setFont(new Font("Tahoma", Font.PLAIN, 34));
		itemList.setOpaque(true);
		frame.getContentPane().add(itemList);
		JLabel quantList = new JLabel(quantityString);
		quantList.setVerticalAlignment(JLabel.TOP);
		quantList.setBounds(650,25,100,425);
		quantList.setFont(new Font("Tahoma", Font.PLAIN, 34));
		quantList.setOpaque(true);
		frame.getContentPane().add(quantList);
		JLabel priceList = new JLabel(priceString);
		priceList.setVerticalAlignment(JLabel.TOP);
		priceList.setBounds(750,25,100,425);
		priceList.setFont(new Font("Tahoma", Font.PLAIN, 34));
		priceList.setOpaque(true);
		frame.getContentPane().add(priceList);
		
		// Delete buttons
		JButton[] delButtons = new JButton[5];
		for(Integer i = 0; i < delButtons.length; i++) {
			delButtons[i]  = new JButton("[X]");
			delButtons[i].setFont(new Font("Tahoma", Font.PLAIN, 36));
			delButtons[i].setBounds(875, 45 + i * 83, 100, 75);
			String item = null;
			if (itemListIndex + i < items.size()) {
				item = items.get(itemListIndex + i);
			}
			delButtons[i].putClientProperty("item", item);
			ActionListener buttList = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("Button Pressed");
					JButton butt = (JButton) e.getSource();
					targetStation.getSelfCheckoutData().removeProductFromCheckoutHashMap((String) butt.getClientProperty("item"));
					attendantData.changeState(AttendantState.REMOVE_ITEM_AT_STATION);
				}
			};
			delButtons[i].addActionListener(buttList);
			if (itemListIndex + i < items.size()) {
				frame.getContentPane().add(delButtons[i]);
			}
		}
		
		// Back button
		final JButton b1 = new JButton("BACK");
		b1.setFont(new Font("Tahoma", Font.PLAIN, 40));
		b1.setBounds(25, 475, 200, 100);
		frame.getContentPane().add(b1);
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.ACTIVE);
			}
		});
		
		// next button
		final JButton b2 = new JButton(">");
		b2.setFont(new Font("Tahoma", Font.PLAIN, 40));
		b2.setBounds(825, 475, 150, 100);
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				itemListIndex += 5;
				attendantData.changeState(AttendantState.REMOVE_ITEM_AT_STATION);
			}
		});
		
		// next button
		final JButton b3 = new JButton("<");
		b3.setFont(new Font("Tahoma", Font.PLAIN, 40));
		b3.setBounds(650, 475, 150, 100);
		b3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				itemListIndex += -5;
				attendantData.changeState(AttendantState.REMOVE_ITEM_AT_STATION);
			}
		});
		
		if (itemListIndex + 5 < items.size()) {
			frame.getContentPane().add(b2);
		}
		if (itemListIndex - 5 >= 0) {
			frame.getContentPane().add(b3);
		}
		
	}
	
	
	private void startScreen() {
		frame.setLayout(null);
		
		final JLabel l1 = new JLabel("PLEASE LOG IN");
		l1.setFont(new Font("Tahoma", Font.PLAIN, 40));
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setBounds(0, 0, 1000, 300);
		frame.getContentPane().add(l1);
		
		final JButton b1 = new JButton("LOG IN");
		b1.setFont(new Font("Tahoma", Font.PLAIN, 40));
		b1.setBounds(400,300,200,100);
		frame.getContentPane().add(b1);
		
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.LOG_IN);
			}
		});
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
				attendantData.changeState(AttendantState.STATIONS);//PLACEHOLDER
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
	
	private void stationsScreen() {
		final JButton b13 = new JButton("LOGOUT");
		b13.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b13.setBounds(400, 475, 200, 100);
		frame.getContentPane().add(b13);
		b13.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.START);
			}
		});
		
		final JLabel l1 = new JLabel("Select station to manage.");
		l1.setFont(new Font("Tahoma", Font.PLAIN, 40));
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setBounds(0, 0, 1000, 100);
		frame.getContentPane().add(l1);
		
		ActionListener buttList = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton src = (JButton) e.getSource();
				String val = src.getText();
				int stationIndex = Integer.parseInt(val) - 1;
				targetStation = attendantData.getUnitAt(stationIndex);
				attendantData.changeState(AttendantState.ACTIVE);
			}
		};
		
		JButton[] stationButtons = new JButton[12];
		for(Integer i = 0; i < stationButtons.length; i++) {
			stationButtons[i]  = new JButton(String.valueOf(i+1));
			stationButtons[i].setFont(new Font("Tahoma", Font.PLAIN, 36));
			stationButtons[i].setBounds(190 + (i % 3) * 210, 100 + (int) Math.floor(i.floatValue() / 3.0) * 90, 200, 80);
			stationButtons[i].addActionListener(buttList);
			// Only show buttons which have corresponding SCS unit
			if (attendantData.getUnitAt(i) != null) {
				frame.getContentPane().add(stationButtons[i]);
			}
		}
		
	}
	
	private void optionsScreen() {
		// Back button
		final JButton b1 = new JButton("BACK");
		b1.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b1.setBounds(275, 475, 200, 100);
		frame.getContentPane().add(b1);
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.STATIONS);
			}
		});
		
		// Logout button
		final JButton b2 = new JButton("LOGOUT");
		b2.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b2.setBounds(525, 475, 200, 100);
		frame.getContentPane().add(b2);
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.START);
			}
		});
		
		final JLabel l1 = new JLabel("Select an option.");
		l1.setFont(new Font("Tahoma", Font.PLAIN, 40));
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setBounds(0, 0, 1000, 100);
		frame.getContentPane().add(l1);
		
		//Enable/disable button
		JButton b3 = new JButton();
		b3.setFont(new Font("Tahoma", Font.PLAIN, 36));
		// Text dependent on whether target station is enabled/disabled
		if (targetStation.getSelfCheckoutData().getCurrentState() == StationState.INACTIVE) {
			b3.setText("START UP STATION");
		}
		else {
			b3.setText("SHUT DOWN STATION");
		}
		b3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (targetStation.getSelfCheckoutData().getCurrentState() == StationState.INACTIVE) {
					targetStation.getSelfCheckoutData().changeState(StationState.WELCOME);
					attendantData.changeState(AttendantState.ACTIVE);
				}
				else {
					targetStation.getSelfCheckoutData().changeState(StationState.INACTIVE);
					attendantData.changeState(AttendantState.ACTIVE);
				}
			}
		});
		
		// Block/unblock button
		JButton b4 = new JButton();
		b4.setFont(new Font("Tahoma", Font.PLAIN, 36));
		// Text dependent on whether target station is enabled/disabled
		if (targetStation.getSelfCheckoutData().getCurrentState() == StationState.BLOCKED) {
			b4.setText("UNBLOCK STATION");
		}
		else {
			b4.setText("BLOCK STATION");
		}
		b4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//if station currently blocked, restore to previous state.
				if (targetStation.getSelfCheckoutData().getCurrentState() == StationState.BLOCKED) {
					targetStation.getSelfCheckoutData().changeState(targetStation.getSelfCheckoutData().getPreBlockedState());
					attendantData.changeState(AttendantState.ACTIVE);
				}
				//else store current state and transition to blocked state.
				else {
					targetStation.getSelfCheckoutData().setPreBlockedState(targetStation.getSelfCheckoutData().getCurrentState());
					targetStation.getSelfCheckoutData().changeState(StationState.BLOCKED);
					attendantData.changeState(AttendantState.ACTIVE);
				}
			}
		});
		
		// Maintenance button.
		JButton b5 = new JButton();
		b5.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b5.setText("MAINTENANCE");
		b5.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.MAINTENANCE);
			}
		});
		
		// Add product button
		JButton b6 = new JButton();
		b6.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b6.setText("LOOK UP ITEM");
		b6.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.PRODUCT_LOOKUP);
			}
		});
		
		// Remove product button
		JButton b7 = new JButton();
		b7.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b7.setText("REMOVE ITEM");
		b7.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				itemListIndex = 0;
				attendantData.changeState(AttendantState.REMOVE_ITEM_AT_STATION);
			}
		});
		
		// Remove product button
		JButton b8 = new JButton();
		b8.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b8.setText("FIX WEIGHT ERROR");
		b8.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.WEIGHT_ERROR);
			}
		});
		
		//Initialize list of buttons to appear on this screen. Always at least the enable/disable button.
		ArrayList<JButton> optionButtons = new ArrayList<JButton>();
		
		optionButtons.add(b3);	// ENABLE/DISABLE	
		// Appear on condition that station isn't shut down.
		if (targetStation.getSelfCheckoutData().getCurrentState() != StationState.INACTIVE) {
			optionButtons.add(b4);	// BLOCK/UNBLOCK
		}
		// Appear on condition that station is shut down
		else {
			optionButtons.add(b5);	// MAINTENANCE
		}
		//Buttons to appear if station is blocked
		if (targetStation.getSelfCheckoutData().getCurrentState() == StationState.BLOCKED) {
			optionButtons.add(b6);	// ADD PRODUCT
			optionButtons.add(b7);	// REMOVE PRODUCT
			optionButtons.add(b8);	// FIX WEIGHT ERROR
		}
		
		// Place the buttons in optionButtons
		for(Integer i = 0; i < optionButtons.size(); i++) {
			optionButtons.get(i).setBounds(75 + (i % 2) * 450, 100 + (int) Math.floor(i.floatValue() / 2.0) * 90, 400, 80);
			frame.getContentPane().add(optionButtons.get(i));
		}
		
	}
	
	public void setTargetStation(SelfCheckoutStationUnit unit) {
		targetStation = unit;
	}
}
