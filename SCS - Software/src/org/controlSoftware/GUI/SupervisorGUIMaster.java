package org.controlSoftware.GUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
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
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.SimulationException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SupervisionStation;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

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
	
	private String buffer = "";
	private Object objectBuffer = null;
	
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
		case EMPTY_COINS:
			emptyCoinsScreen();
			break;
		case EMPTY_NOTES:
			emptyNotesScreen();
			break;
		case REFILL_COINS:
			refillCoinsScreen();
			break;
		case REFILL_NOTES:
			refillNotesScreen();
			break;
		case REFILL_PAPER:
			refillPaperScreen();
			break;
		case REFILL_INK:
			refillInkScreen();
			break;
		case BARCODE:
			barcodeScreen();
			break;
		case PLUCODE:
			plucodeScreen();
			break;
		case CATALOGUE:
			catalogueScreen();
			break;
		case BARCODE_CHECK:
			barcodeCheckScreen();
			break;
		case PLU_CHECK:
			plucodeCheckScreen();
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
	private void catalogueScreen() {
		// Unused
	}
	private void plucodeCheckScreen() {
		frame.setLayout(null);
		
		final JLabel l1 = new JLabel(buffer);
		l1.setFont(new Font("Tahoma", Font.PLAIN, 40));
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setBounds(0, 0, 1000, 300);
		frame.getContentPane().add(l1);
		
		final JButton b2 = new JButton("BACK");
		b2.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b2.setBounds(275,300,200,100);
		frame.getContentPane().add(b2);
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.PLUCODE);
			}
		});
		
		final JButton b1 = new JButton("CONFIRM");
		b1.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b1.setBounds(525,300,200,100);
		if (buffer != "Invalid PLU code. Try again.") {
			frame.getContentPane().add(b1);
		}
		else {
			b2.setBounds(400,300,200,100);
		}
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PriceLookupCode pluCode = (PriceLookupCode) objectBuffer;
				// Let station know to weigh item.
				attendantData.changeState(AttendantState.ACTIVE);
			}
		});
	}
	private void plucodeScreen() {
		frame.setLayout(null);
		
		JLabel l2 = new JLabel("Enter item PLU code");
		l2.setBounds(0,0,1000,75);
		l2.setHorizontalAlignment(SwingConstants.CENTER);
		l2.setFont(new Font("Tahoma", Font.PLAIN, 36));
		frame.getContentPane().add(l2);
		
		final JLabel l1 = new JLabel("");
		l1.setFont(new Font("Tahoma", Font.PLAIN, 30));
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setBounds(0, 75, 1000, 75);
		frame.getContentPane().add(l1);
		
		ActionListener keyList = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton src = (JButton) e.getSource();
				String val = src.getText();
				if (val == "DEL") {
					l1.setText(l1.getText().substring(0, l1.getText().length()-1));
				}
				else {
					if (l1.getText().length() < 48) {
						l1.setText(l1.getText() + val);
					}
				}
				
			}
		};
		
		JButton[] keypad = new JButton[9];
		for(Integer i = 0; i < keypad.length; i++) {
			keypad[i]  = new JButton(String.valueOf((i+1) % 10));
			keypad[i].setFont(new Font("Tahoma", Font.PLAIN, 36));
			keypad[i].setBounds(275 + (i % 3) * 150, 150 + (int) Math.floor(i.floatValue() / 3.0) * 75, 150, 75);
			keypad[i].addActionListener(keyList);
			
			frame.getContentPane().add(keypad[i]);
		}
		
		JButton b3 = new JButton("DEL");
		b3.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b3.setBounds(275, 375, 150, 75);
		b3.addActionListener(keyList);
		frame.getContentPane().add(b3);
		
		JButton b4 = new JButton("0");
		b4.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b4.setBounds(425, 375, 150, 75);
		b4.addActionListener(keyList);
		frame.getContentPane().add(b4);
		
		
		
		JButton b1 = new JButton("BACK");
		b1.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b1.setBounds(275, 475, 200, 100);
		frame.getContentPane().add(b1);
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.PRODUCT_LOOKUP);
			}
		});
		
		JButton b2 = new JButton("CONFIRM");
		b2.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b2.setBounds(525, 475, 200, 100);
		frame.getContentPane().add(b2);
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String string = l1.getText();
				PriceLookupCode pluCode = new PriceLookupCode(string);
				PLUCodedProduct product = targetStation.getSoftware().getPLUCodedItem(pluCode);
				if (product != null) {
					buffer = product.getDescription();
					objectBuffer = pluCode;
				}
				else {
					buffer = "Invalid PLU code. Try again.";
				}
				attendantData.changeState(AttendantState.PLU_CHECK);
			}
		});
	}
	private void barcodeCheckScreen() {
		frame.setLayout(null);
		
		final JLabel l1 = new JLabel(buffer);
		l1.setFont(new Font("Tahoma", Font.PLAIN, 40));
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setBounds(0, 0, 1000, 300);
		frame.getContentPane().add(l1);
		
		final JButton b2 = new JButton("BACK");
		b2.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b2.setBounds(275,300,200,100);
		frame.getContentPane().add(b2);
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.BARCODE);
			}
		});
		
		final JButton b1 = new JButton("CONFIRM");
		b1.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b1.setBounds(525,300,200,100);
		if (buffer != "Invalid barcode. Try again.") {
			frame.getContentPane().add(b1);
		}
		else {
			b2.setBounds(400,300,200,100);
		}
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Barcode barcode = (Barcode) objectBuffer;
				targetStation.getSoftware().scannerHandler.barcodeScanned(targetStation.getSelfCheckoutStationHardware().mainScanner, barcode);
				attendantData.changeState(AttendantState.ACTIVE);
			}
		});
	}
	private void barcodeScreen() {
		frame.setLayout(null);
		
		JLabel l2 = new JLabel("Enter item barcode");
		l2.setBounds(0,0,1000,75);
		l2.setHorizontalAlignment(SwingConstants.CENTER);
		l2.setFont(new Font("Tahoma", Font.PLAIN, 36));
		frame.getContentPane().add(l2);
		
		final JLabel l1 = new JLabel("");
		l1.setFont(new Font("Tahoma", Font.PLAIN, 30));
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setBounds(0, 75, 1000, 75);
		frame.getContentPane().add(l1);
		
		ActionListener keyList = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton src = (JButton) e.getSource();
				String val = src.getText();
				if (val == "DEL") {
					l1.setText(l1.getText().substring(0, l1.getText().length()-1));
				}
				else {
					if (l1.getText().length() < 48) {
						l1.setText(l1.getText() + val);
					}
				}
				
			}
		};
		
		JButton[] keypad = new JButton[9];
		for(Integer i = 0; i < keypad.length; i++) {
			keypad[i]  = new JButton(String.valueOf((i+1) % 10));
			keypad[i].setFont(new Font("Tahoma", Font.PLAIN, 36));
			keypad[i].setBounds(275 + (i % 3) * 150, 150 + (int) Math.floor(i.floatValue() / 3.0) * 75, 150, 75);
			keypad[i].addActionListener(keyList);
			
			frame.getContentPane().add(keypad[i]);
		}
		
		JButton b3 = new JButton("DEL");
		b3.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b3.setBounds(275, 375, 150, 75);
		b3.addActionListener(keyList);
		frame.getContentPane().add(b3);
		
		JButton b4 = new JButton("0");
		b4.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b4.setBounds(425, 375, 150, 75);
		b4.addActionListener(keyList);
		frame.getContentPane().add(b4);
		
		
		
		JButton b1 = new JButton("BACK");
		b1.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b1.setBounds(275, 475, 200, 100);
		frame.getContentPane().add(b1);
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.PRODUCT_LOOKUP);
			}
		});
		
		JButton b2 = new JButton("CONFIRM");
		b2.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b2.setBounds(525, 475, 200, 100);
		frame.getContentPane().add(b2);
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String string = l1.getText();
				Numeral[] code = new Numeral[string.length()];
				for (int i = 0; i < string.length(); i++) {
					char c = string.toCharArray()[i];
					Numeral num = Numeral.valueOf((byte) Character.getNumericValue(c));
					code[i] = num;
				}
				Barcode barcode = new Barcode(code);
				BarcodedProduct product = targetStation.getSoftware().getBarcodedItem(barcode);
				if (product != null) {
					buffer = product.getDescription();
					objectBuffer = barcode;
				}
				else {
					buffer = "Invalid barcode. Try again.";
				}
				attendantData.changeState(AttendantState.BARCODE_CHECK);
			}
		});
	}
	private void searchItemScreen() {
		frame.setLayout(null);
		
		JLabel l2 = new JLabel("Choose method");
		l2.setBounds(0,0,1000,100);
		l2.setHorizontalAlignment(SwingConstants.CENTER);
		l2.setFont(new Font("Tahoma", Font.PLAIN, 36));
		frame.getContentPane().add(l2);
		
		JButton b3 = new JButton("INPUT BARCODE");
		b3.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b3.setBounds(300, 100, 400, 100);
		frame.getContentPane().add(b3);
		b3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.BARCODE);
			}
		});
		
		JButton b4 = new JButton("INPUT PLU CODE");
		b4.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b4.setBounds(300, 225, 400, 100);
		frame.getContentPane().add(b4);
		b4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.PLUCODE);
			}
		});		
		
		// Attendant no access to catalogue for times sake.
		/*JButton b2 = new JButton("CATALOGUE");
		b2.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b2.setBounds(300, 350, 400, 100);
		frame.getContentPane().add(b2);
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.CATALOGUE);
			}
		});*/
		
		JButton b1 = new JButton("BACK");
		b1.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b1.setBounds(400, 475, 200, 100);
		frame.getContentPane().add(b1);
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.ACTIVE);
			}
		});
	}
	private void maintenanceScreen() {
		frame.setLayout(null);
		
		// Back button
		final JButton b1 = new JButton("BACK");
		b1.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b1.setBounds(275, 475, 200, 100);
		frame.getContentPane().add(b1);
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.ACTIVE);
			}
		});
		
		// Logout button
		final JButton b2 = new JButton("LOGOUT");
		b2.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b2.setBounds(500, 475, 200, 100);
		frame.getContentPane().add(b2);
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.START);
			}
		});
		
		JLabel l1 = new JLabel("Select maintenance option");
		l1.setFont(new Font("Tahoma", Font.PLAIN, 40));
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setBounds(0, 0, 1000, 100);
		frame.getContentPane().add(l1);
		
		JButton b3 = new JButton();
		b3.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b3.setText("REFILL PRINTER PAPER");
		b3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.REFILL_PAPER);
			}
		});
		
		JButton b4 = new JButton();
		b4.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b4.setText("REFILL PRINTER INK");
		b4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.REFILL_INK);
			}
		});
		
		JButton b5 = new JButton();
		b5.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b5.setText("EMPTY COIN STORAGE");
		b5.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.EMPTY_COINS);
			}
		});
		
		JButton b6 = new JButton();
		b6.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b6.setText("EMPTY BILL STORAGE");
		b6.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.EMPTY_NOTES);
			}
		});
		
		JButton b7 = new JButton();
		b7.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b7.setText("REFILL COIN DISPENSER");
		b7.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				itemListIndex = 0;
				attendantData.changeState(AttendantState.REFILL_COINS);
			}
		});
		
		JButton b8 = new JButton();
		b8.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b8.setText("REFILL BILL DISPENSER");
		b8.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.REFILL_NOTES);
			}
		});
		
		//Initialize list of buttons to appear on this screen.
		ArrayList<JButton> optionButtons = new ArrayList<JButton>();
		
		optionButtons.add(b3);	
		optionButtons.add(b4);	
		optionButtons.add(b5);	
		optionButtons.add(b6);	
		optionButtons.add(b7);	
		optionButtons.add(b8);	
		
		// Place the buttons in optionButtons
		for(Integer i = 0; i < optionButtons.size(); i++) {
			optionButtons.get(i).setBounds(25 + (i % 2) * 475, 100 + (int) Math.floor(i.floatValue() / 2.0) * 125, 450, 100);
			frame.getContentPane().add(optionButtons.get(i));
		}
		
		
	}
	private void refillInkScreen() {
		frame.setLayout(null);
		
		JLabel l2 = new JLabel("Confirm units of ink refilled");
		l2.setBounds(0,0,1000,75);
		l2.setHorizontalAlignment(SwingConstants.CENTER);
		l2.setFont(new Font("Tahoma", Font.PLAIN, 36));
		frame.getContentPane().add(l2);
		
		final JLabel l1 = new JLabel("");
		l1.setFont(new Font("Tahoma", Font.PLAIN, 40));
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setBounds(0, 75, 1000, 75);
		frame.getContentPane().add(l1);
		
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
			keypad[i].setBounds(275 + (i % 3) * 150, 150 + (int) Math.floor(i.floatValue() / 3.0) * 75, 150, 75);
			keypad[i].addActionListener(keyList);
			
			frame.getContentPane().add(keypad[i]);
		}
		
		JButton b3 = new JButton("DEL");
		b3.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b3.setBounds(275, 375, 150, 75);
		b3.addActionListener(keyList);
		frame.getContentPane().add(b3);
		
		JButton b4 = new JButton("0");
		b4.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b4.setBounds(425, 375, 150, 75);
		b4.addActionListener(keyList);
		frame.getContentPane().add(b4);
		
		
		
		JButton b1 = new JButton("BACK");
		b1.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b1.setBounds(275, 475, 200, 100);
		frame.getContentPane().add(b1);
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.MAINTENANCE);
			}
		});
		
		JButton b2 = new JButton("CONFIRM");
		b2.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b2.setBounds(525, 475, 200, 100);
		frame.getContentPane().add(b2);
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					targetStation.getSelfCheckoutStationHardware().printer.addInk(Integer.parseInt(l1.getText()));
				} catch (NumberFormatException e1) {
					e1.printStackTrace();
					return;
				} catch (OverloadException e1) {
					e1.printStackTrace();
					return;
				}
				attendantData.changeState(AttendantState.MAINTENANCE);
			}
		});
	}
	private void refillPaperScreen() {
		frame.setLayout(null);
		
		JLabel l2 = new JLabel("Confirm units of paper refilled");
		l2.setBounds(0,0,1000,75);
		l2.setHorizontalAlignment(SwingConstants.CENTER);
		l2.setFont(new Font("Tahoma", Font.PLAIN, 36));
		frame.getContentPane().add(l2);
		
		final JLabel l1 = new JLabel("");
		l1.setFont(new Font("Tahoma", Font.PLAIN, 40));
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setBounds(0, 75, 1000, 75);
		frame.getContentPane().add(l1);
		
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
			keypad[i].setBounds(275 + (i % 3) * 150, 150 + (int) Math.floor(i.floatValue() / 3.0) * 75, 150, 75);
			keypad[i].addActionListener(keyList);
			
			frame.getContentPane().add(keypad[i]);
		}
		
		JButton b3 = new JButton("DEL");
		b3.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b3.setBounds(275, 375, 150, 75);
		b3.addActionListener(keyList);
		frame.getContentPane().add(b3);
		
		JButton b4 = new JButton("0");
		b4.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b4.setBounds(425, 375, 150, 75);
		b4.addActionListener(keyList);
		frame.getContentPane().add(b4);
		
		
		
		JButton b1 = new JButton("BACK");
		b1.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b1.setBounds(275, 475, 200, 100);
		frame.getContentPane().add(b1);
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.MAINTENANCE);
			}
		});
		
		JButton b2 = new JButton("CONFIRM");
		b2.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b2.setBounds(525, 475, 200, 100);
		frame.getContentPane().add(b2);
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					targetStation.getSelfCheckoutStationHardware().printer.addPaper(Integer.parseInt(l1.getText()));
				} catch (NumberFormatException e1) {
					e1.printStackTrace();
					return;
				} catch (OverloadException e1) {
					e1.printStackTrace();
					return;
				}
				attendantData.changeState(AttendantState.MAINTENANCE);
			}
		});
	}
	private void refillNotesScreen() {
		frame.setLayout(null);
		
		JLabel l1 = new JLabel("Confirm denominations added");
		l1.setBounds(0,0,1000,100);
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setFont(new Font("Tahoma", Font.PLAIN, 36));
		frame.getContentPane().add(l1);
		
		final int[] denominations = SelfCheckoutStationUnit.getBanknoteDenominations();
		
		ArrayList<JLabel> labels = new ArrayList<JLabel>();
		ArrayList<JButton> buttons1 = new ArrayList<JButton>();
		ArrayList<JButton> buttons2 = new ArrayList<JButton>();
		final ArrayList<JLabel> quants = new ArrayList<JLabel>();
		
		for (int i = 0; i < denominations.length; i++) {
			labels.add(new JLabel("$" + String.valueOf(denominations[i])));
			labels.get(i).setFont(new Font("Tahoma", Font.PLAIN, 36));
			labels.get(i).setHorizontalAlignment(SwingConstants.CENTER);
			labels.get(i).setBounds(225, 100 + i * 70, 200, 65);
			frame.getContentPane().add(labels.get(i));
			
			quants.add(new JLabel("0"));
			quants.get(i).setFont(new Font("Tahoma", Font.PLAIN, 36));
			quants.get(i).setHorizontalAlignment(SwingConstants.CENTER);
			quants.get(i).setBounds(575, 100 + i * 70, 200, 65);
			frame.getContentPane().add(quants.get(i));
			
			buttons1.add(new JButton("-"));
			buttons1.get(i).setFont(new Font("Tahoma", Font.PLAIN, 36));
			buttons1.get(i).setBounds(425, 100 + i * 70, 75, 65);
			buttons1.get(i).putClientProperty("index", i);
			buttons1.get(i).addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JButton butt = (JButton) e.getSource();
					int index = (int) butt.getClientProperty("index");
					Integer quant = Integer.parseInt(quants.get(index).getText());
					if (quant > 0) {
						quant--;
						quants.get(index).setText(quant.toString());
					}
				}
			});
			
			frame.getContentPane().add(buttons1.get(i));
			
			buttons2.add(new JButton("+"));
			buttons2.get(i).setFont(new Font("Tahoma", Font.PLAIN, 36));
			buttons2.get(i).setBounds(500, 100 + i * 70, 75, 65);
			buttons2.get(i).putClientProperty("index", i);
			buttons2.get(i).addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JButton butt = (JButton) e.getSource();
					int index = (int) butt.getClientProperty("index");
					Integer quant = Integer.parseInt(quants.get(index).getText());
					quant += 10;
					quants.get(index).setText(quant.toString());
				}
			});
			frame.getContentPane().add(buttons2.get(i));
		}
		
		JButton b1 = new JButton("BACK");
		b1.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b1.setBounds(275, 475, 200, 100);
		frame.getContentPane().add(b1);
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.MAINTENANCE);
			}
		});
		
		JButton b2 = new JButton("CONFIRM");
		b2.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b2.setBounds(525, 475, 200, 100);
		frame.getContentPane().add(b2);
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < denominations.length; i++) {
					int den = denominations[i];
					int quant = Integer.parseInt(quants.get(i).getText());
					Banknote banknote = new Banknote(SelfCheckoutStationUnit.getCurrency(), den);
					Banknote[] banknotes = new Banknote [quant];
					for (int j = 0; j < quant; j++) {
						banknotes[j] = banknote;
					}
					try {
						attendantData.getSoftware().refillbanknoteDispenser(targetStation.getStationID(), banknotes);
					} catch (SimulationException e1) {
						e1.printStackTrace();
						return; // No state change if load fails
					} catch (OverloadException e1) {
						// Potentially add additional code to handle overload. Don't really care presently.
						System.out.println("too full");
						e1.printStackTrace();
						return; // No state change if load fails
					}
				}
				attendantData.changeState(AttendantState.MAINTENANCE);
			}
		});
	}
	private void refillCoinsScreen() {
		frame.setLayout(null);
		
		JLabel l1 = new JLabel("Confirm denominations added");
		l1.setBounds(0,0,1000,100);
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setFont(new Font("Tahoma", Font.PLAIN, 36));
		frame.getContentPane().add(l1);
		
		final BigDecimal[] denominations = SelfCheckoutStationUnit.getCoinDenominations();
		
		ArrayList<JLabel> labels = new ArrayList<JLabel>();
		ArrayList<JButton> buttons1 = new ArrayList<JButton>();
		ArrayList<JButton> buttons2 = new ArrayList<JButton>();
		final ArrayList<JLabel> quants = new ArrayList<JLabel>();
		
		for (int i = 0; i < denominations.length; i++) {
			labels.add(new JLabel("$" + denominations[i].toString()));
			labels.get(i).setFont(new Font("Tahoma", Font.PLAIN, 36));
			labels.get(i).setHorizontalAlignment(SwingConstants.CENTER);
			labels.get(i).setBounds(225, 100 + i * 70, 200, 65);
			frame.getContentPane().add(labels.get(i));
			
			quants.add(new JLabel("0"));
			quants.get(i).setFont(new Font("Tahoma", Font.PLAIN, 36));
			quants.get(i).setHorizontalAlignment(SwingConstants.CENTER);
			quants.get(i).setBounds(575, 100 + i * 70, 200, 65);
			frame.getContentPane().add(quants.get(i));
			
			buttons1.add(new JButton("-"));
			buttons1.get(i).setFont(new Font("Tahoma", Font.PLAIN, 36));
			buttons1.get(i).setBounds(425, 100 + i * 70, 75, 65);
			buttons1.get(i).putClientProperty("index", i);
			buttons1.get(i).addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JButton butt = (JButton) e.getSource();
					int index = (int) butt.getClientProperty("index");
					Integer quant = Integer.parseInt(quants.get(index).getText());
					if (quant > 0) {
						quant--;
						quants.get(index).setText(quant.toString());
					}
				}
			});
			
			frame.getContentPane().add(buttons1.get(i));
			
			buttons2.add(new JButton("+"));
			buttons2.get(i).setFont(new Font("Tahoma", Font.PLAIN, 36));
			buttons2.get(i).setBounds(500, 100 + i * 70, 75, 65);
			buttons2.get(i).putClientProperty("index", i);
			buttons2.get(i).addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JButton butt = (JButton) e.getSource();
					int index = (int) butt.getClientProperty("index");
					Integer quant = Integer.parseInt(quants.get(index).getText());
					quant += 10;
					quants.get(index).setText(quant.toString());
				}
			});
			frame.getContentPane().add(buttons2.get(i));
		}
		
		JButton b1 = new JButton("BACK");
		b1.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b1.setBounds(275, 475, 200, 100);
		frame.getContentPane().add(b1);
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.MAINTENANCE);
			}
		});
		
		JButton b2 = new JButton("CONFIRM");
		b2.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b2.setBounds(525, 475, 200, 100);
		frame.getContentPane().add(b2);
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < denominations.length; i++) {
					BigDecimal den = denominations[i];
					int quant = Integer.parseInt(quants.get(i).getText());
					Coin coin = new Coin(SelfCheckoutStationUnit.getCurrency(), den);
					Coin[] coins = new Coin [quant];
					for (int j = 0; j < quant; j++) {
						coins[j] = coin;
					}
					try {
						attendantData.getSoftware().refillCoinDispenser(targetStation.getStationID(), coins);
					} catch (SimulationException e1) {
						e1.printStackTrace();
						return; // No state change if load fails
					} catch (OverloadException e1) {
						// Potentially add additional code to handle overload. Don't really care presently.
						System.out.println("too full");
						e1.printStackTrace();
						return; // No state change if load fails
					}
				}
				attendantData.changeState(AttendantState.MAINTENANCE);
			}
		});		
	}
	private void emptyNotesScreen() {
		frame.setLayout(null);
		
		JLabel l1 = new JLabel("Confirm banknotes emptied from storage");
		l1.setFont(new Font("Tahoma", Font.PLAIN, 36));
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setBounds(0, 0, 1000, 300);
		frame.getContentPane().add(l1);
		
		JButton b1 = new JButton("BACK");
		b1.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b1.setBounds(275, 475, 200, 100);
		frame.getContentPane().add(b1);
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.MAINTENANCE);
			}
		});
		
		JButton b2 = new JButton("CONFIRM");
		b2.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b2.setBounds(500, 475, 200, 100);
		frame.getContentPane().add(b2);
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.getSoftware().emptyBanknoteStorageUnit(targetStation.getStationID());
				attendantData.changeState(AttendantState.MAINTENANCE);
			}
		});
	}
	private void emptyCoinsScreen() {
		frame.setLayout(null);
		
		JLabel l1 = new JLabel("Confirm coins emptied from storage");
		l1.setFont(new Font("Tahoma", Font.PLAIN, 36));
		l1.setHorizontalAlignment(SwingConstants.CENTER);
		l1.setBounds(0, 0, 1000, 300);
		frame.getContentPane().add(l1);
		
		JButton b1 = new JButton("BACK");
		b1.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b1.setBounds(275, 475, 200, 100);
		frame.getContentPane().add(b1);
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.changeState(AttendantState.MAINTENANCE);
			}
		});
		
		JButton b2 = new JButton("CONFIRM");
		b2.setFont(new Font("Tahoma", Font.PLAIN, 36));
		b2.setBounds(500, 475, 200, 100);
		frame.getContentPane().add(b2);
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attendantData.getSoftware().emptyCoinStorageUnit(targetStation.getStationID());
				attendantData.changeState(AttendantState.MAINTENANCE);
			}
		});
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
		
		final JLabel l1 = new JLabel("Select station to manage");
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
		
		final JLabel l1 = new JLabel("Select a station option");
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
		b6.setText("ADD ITEM");
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
		
		// Weight error
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
			// Only allow add/remove product if station was in the main scanning screen prior to block.
			if (targetStation.getSelfCheckoutData().getPreBlockedState() == StationState.NORMAL) {
				optionButtons.add(b6);	// ADD PRODUCT
				optionButtons.add(b7);	// REMOVE PRODUCT
			}
			// TBD WHEN THIS BUTTON APPEARS
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

/*
JLabel l2 = new JLabel("Units");
l2.setFont(new Font("Tahoma", Font.PLAIN, 36));
l2.setHorizontalAlignment(SwingConstants.CENTER);
l2.setBounds(150, 100, 200, 75);
frame.getContentPane().add(l2);

final JLabel l3 = new JLabel("0");
l3.setFont(new Font("Tahoma", Font.PLAIN, 36));
l3.setHorizontalAlignment(SwingConstants.CENTER);
l3.setBounds(650, 100, 200, 75);
frame.getContentPane().add(l3);

JButton b5 = new JButton("--");
b5.setFont(new Font("Tahoma", Font.PLAIN, 36));
b5.setBounds(350, 100, 75, 75);
b5.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
		Integer quant = Integer.parseInt(l3.getText());
		if (quant > 10) {
			quant -= 10;
			l3.setText(quant.toString());
		}
	}
});
frame.getContentPane().add(b5);

JButton b3 = new JButton("-");
b3.setFont(new Font("Tahoma", Font.PLAIN, 36));
b3.setBounds(425, 100, 75, 75);
b3.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
		Integer quant = Integer.parseInt(l3.getText());
		if (quant > 0) {
			quant--;
			l3.setText(quant.toString());
		}
	}
});
frame.getContentPane().add(b3);

JButton b4 = new JButton("+");
b4.setFont(new Font("Tahoma", Font.PLAIN, 36));
b4.setBounds(500, 100, 75, 75);
b4.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
		Integer quant = Integer.parseInt(l3.getText());
		quant += 10;
		l3.setText(quant.toString());
	}
});
frame.getContentPane().add(b4);

JButton b6 = new JButton("++");
b6.setFont(new Font("Tahoma", Font.PLAIN, 36));
b6.setBounds(575, 100, 75, 75);
b6.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
		Integer quant = Integer.parseInt(l3.getText());
		quant += 100;
		l3.setText(quant.toString());
	}
});
frame.getContentPane().add(b6);
*/
















