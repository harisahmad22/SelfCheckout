package org.controlSoftware.GUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import org.driver.SelfCheckoutData;
import org.driver.SelfCheckoutData.StationState;
import org.driver.databases.ProductInfo;
import org.driver.databases.TestBarcodedProducts;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

public class ScanningScreenGUI {
	private SelfCheckoutStation station;
	private SelfCheckoutData stationData;

	private JFrame frame;

	public ScanningScreenGUI(SelfCheckoutStation newStation, SelfCheckoutData newData) {
		station = newStation;
		stationData = newData;

		frame = station.screen.getFrame();
	}

	public void stateChanged() {
		switch (stationData.getCurrentState()) {
		case NORMAL:
			Main();
			break;
		case LETTER_SEARCH:
			LetterSearch();
			break;
		case PLU_SEARCH:
			PluSearch();
			break;
		case CHECKOUT_CHECK:
			checkoutPopup();
			break;
		case WAITING_FOR_ITEM:
			scanPopup();
			break;
		default:
			break;
		}
	}

	// The main page for scanning
	private void Main() {
		frame.setLayout(null);

		// Display of scanned items

		// TESTING
		ArrayList<BarcodedProduct> testProducts = new TestBarcodedProducts().getBarcodedProductList();
//		stationData.debugAddProductToCheckout(testProducts.get(0));
//		stationData.debugAddProductToCheckout(testProducts.get(1));
//		stationData.debugAddProductToCheckout(testProducts.get(2));
		// TESTING
		HashMap<String, ProductInfo> currentAddedProducts = stationData.getProductsAddedToCheckoutHashMap();
		String productListString = "<html>";
		for (String prodDescription : currentAddedProducts.keySet()) {
			productListString += prodDescription + " --- " + "$"
					+ currentAddedProducts.get(prodDescription).getProduct().getPrice() + " --- " + "Quantity: "
					+ currentAddedProducts.get(prodDescription).getQuantity() + "<br>";
		}
		productListString += "</html>";

		debugScanTestItemButton();
		debugBlockStationButton();
		debugForceWeightIssueButton();

		JLabel itemList = new JLabel(productListString);
		itemList.setBounds(20, 20, 700, 420);
		itemList.setBackground(Color.blue);
		itemList.setOpaque(true);
		frame.getContentPane().add(itemList);

		// Display of the total price
		JLabel totalPrice = new JLabel("$" + stationData.getTotalDue().toString());
		totalPrice.setBounds(20, 460, 700, 80);
		totalPrice.setBackground(Color.red);
		totalPrice.setFont(new Font("Calibri", Font.BOLD, 48));
		totalPrice.setOpaque(true);
		frame.getContentPane().add(totalPrice);

		// All of the option buttons
		JPanel options = new JPanel();
		options.setLayout(new GridLayout(0, 1));
		JButton itemButton = new JButton("Item Lookup");
		options.add(itemButton);
		itemButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stationData.changeState(StationState.LETTER_SEARCH);
			}
		});
		JButton pluButton = new JButton("PLU Lookup");
		options.add(pluButton);
		pluButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stationData.changeState(StationState.PLU_SEARCH);
			}
		});
//		JButton assistButton = new JButton("Ask Attendant for Assistance");
//		options.add(assistButton);
//		assistButton.addActionListener(new ActionListener() {
//	        public void actionPerformed(ActionEvent e) {
//	        	//Ask for assistance
//	        }
//	    });
		JButton checkoutButton = new JButton("Proceed to checkout");
		options.add(checkoutButton);
		checkoutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stationData.changeState(StationState.CHECKOUT_CHECK);
			}
		});
		options.setBounds(740, 20, 200, 520);
		frame.getContentPane().add(options);

		frame.setVisible(true);
	}

	private void debugBlockStationButton() {

		final JButton b1 = new JButton("[DEBUG] Block Station");
		b1.setFont(new Font("Tahoma", Font.PLAIN, 11));
		b1.setBounds(0, 0, 300, 100);
		frame.getContentPane().add(b1);

		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stationData.getStationSoftware().blockStation();
			}
		});
	}

	private void debugForceWeightIssueButton() {

		final JButton b1 = new JButton("[DEBUG] Force Weight Issue");
		b1.setFont(new Font("Tahoma", Font.PLAIN, 11));
		b1.setBounds(0, 100, 300, 100);
		frame.getContentPane().add(b1);

		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stationData.changeState(StationState.WEIGHT_ISSUE);
			}
		});
	}

	// BRODY
	private void debugScanTestItemButton() {
		Color color = new Color(128, 128, 255);
		JButton payCoin = new JButton();
		payCoin.setBounds(350, 150, 300, 200);
		payCoin.setText("[DEBUG] Scan in a Milk Jug");
		payCoin.setFont(new Font("Calibri", Font.BOLD, 18));
		payCoin.setBackground(color);

		payCoin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stationData.getStationHardware().mainScanner.scan(stationData.getTestProducts().getItemList().get(0));
			}
		});

		frame.add(payCoin);
		payCoin.setVisible(true);
	}

	// Page for looking for PLU
	private void PluSearch() {
		frame.setLayout(null);

		// Display of searched items
		JLabel inventoryPLU = new JLabel("Placeholder for inventory");
		frame.add(inventoryPLU);
		inventoryPLU.setBounds(20, 20, 700, 520);
		inventoryPLU.setBackground(Color.blue);
		inventoryPLU.setOpaque(true);

		// Shows current input
		final JLabel codePLU = new JLabel("");
		frame.add(codePLU);
		codePLU.setFont(new Font("Tahoma", Font.PLAIN, 40));
		codePLU.setBounds(740, 100, 220, 100);
		codePLU.setBackground(Color.gray);
		codePLU.setOpaque(true);

		// Code for recording numbers (From Jonah)
		ActionListener Numpad = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton src = (JButton) e.getSource();
				String val = src.getText();
				if (val == "DEL") {
					codePLU.setText(codePLU.getText().substring(0, codePLU.getText().length() - 1));
				} else if (val == "GO") {
					double weight = 0;
					String search = codePLU.getText();
					PriceLookupCode PLUCode = new PriceLookupCode(search);
					PLUCodedProduct PLUProduct = stationData.getPLUDatabase().getPLUProductFromDatabase(PLUCode);
					try {
						weight = stationData.getStationHardware().scanningArea.getCurrentWeight();
					} catch (OverloadException e1) {
						e1.printStackTrace();
					}
					stationData.addProductToCheckout(PLUProduct, weight);
				} else {
					if (codePLU.getText().length() < 5) {
						codePLU.setText(codePLU.getText() + val);
					}
				}

			}
		};

		// All the numpad buttons
		JPanel numpad = new JPanel();
		numpad.setLayout(new GridLayout(0, 3));
		JButton numpad1 = new JButton("1");
		numpad1.addActionListener(Numpad);
		numpad.add(numpad1);
		JButton numpad2 = new JButton("2");
		numpad2.addActionListener(Numpad);
		numpad.add(numpad2);
		JButton numpad3 = new JButton("3");
		numpad3.addActionListener(Numpad);
		numpad.add(numpad3);
		JButton numpad4 = new JButton("4");
		numpad4.addActionListener(Numpad);
		numpad.add(numpad4);
		JButton numpad5 = new JButton("5");
		numpad5.addActionListener(Numpad);
		numpad.add(numpad5);
		JButton numpad6 = new JButton("6");
		numpad6.addActionListener(Numpad);
		numpad.add(numpad6);
		JButton numpad7 = new JButton("7");
		numpad7.addActionListener(Numpad);
		numpad.add(numpad7);
		JButton numpad8 = new JButton("8");
		numpad8.addActionListener(Numpad);
		numpad.add(numpad8);
		JButton numpad9 = new JButton("9");
		numpad9.addActionListener(Numpad);
		numpad.add(numpad9);
		JButton numpad0 = new JButton("0");
		numpad0.addActionListener(Numpad);
		numpad.add(numpad0);
		JButton numpadDel = new JButton("DEL");
		numpadDel.addActionListener(Numpad);
		numpad.add(numpadDel);
		JButton numpadGo = new JButton("GO");
		numpadGo.addActionListener(Numpad);
		numpad.add(numpadGo);
		frame.add(numpad);
		numpad.setBounds(740, 220, 220, 320);

		// Return to main scanning screen
		JButton pluReturn = new JButton("Return");
		frame.add(pluReturn);
		pluReturn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stationData.changeState(StationState.NORMAL);
			}
		});
		pluReturn.setBounds(740, 20, 220, 60);

		frame.setVisible(true);

	}

	// Screen for searching by letter
	private void LetterSearch() {
		final String[] letters = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
				"R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
		frame.setLayout(null);

		// Display for search
		JLabel inventoryLetter = new JLabel("Placeholder for inventory");
		frame.add(inventoryLetter);
		inventoryLetter.setBounds(20, 20, 700, 520);
		inventoryLetter.setBackground(Color.blue);
		inventoryLetter.setOpaque(true);

		// List of letters to select
		JScrollPane alphabetContainer = new JScrollPane();
		final JList alphabetList = new JList(letters);
		alphabetContainer.setViewportView(alphabetList);
		alphabetList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		frame.add(alphabetContainer);
		alphabetList.setFont(new Font("Tahoma", Font.PLAIN, 80));
		alphabetContainer.setBounds(740, 100, 220, 360);

		// Button that gets the letter from the list
		JButton alphabetSearch = new JButton("Search");
		alphabetSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = alphabetList.getSelectedIndex();
				if (index != -1) {
					String search = letters[index];
					System.out.println(search);
				}
			}
		});
		frame.add(alphabetSearch);
		alphabetSearch.setBounds(740, 480, 220, 60);

		// Return to main scanning screen
		JButton alphabetReturn = new JButton("Return to Scanning");
		frame.add(alphabetReturn);
		alphabetReturn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stationData.changeState(StationState.NORMAL);
			}
		});
		alphabetReturn.setBounds(740, 20, 220, 60);

		frame.setVisible(true);
	}

	// Prompt if user is sure if they want to check out
	private void checkoutPopup() {
		frame.setLayout(null);

		// Label for text
		JLabel confirmCheckout = new JLabel("Are you sure you want to proceed to checkout?", SwingConstants.CENTER);
		confirmCheckout.setFont(new Font("Tahoma", Font.PLAIN, 35));
		frame.add(confirmCheckout);
		confirmCheckout.setBounds(100, 20, 800, 100);

		// Proceeds to checkout
		JButton outYesButton = new JButton("Yes");
		frame.add(outYesButton);
		outYesButton.setBounds(200, 200, 200, 100);
		outYesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stationData.changeState(StationState.CHECKOUT);
				stationData.getStationSoftware().getCheckoutHandler().startCheckout();
			}
		});

		// Brings user back
		JButton outNoButton = new JButton("No");
		frame.add(outNoButton);
		outNoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stationData.changeState(StationState.NORMAL);
			}
		});
		outNoButton.setBounds(600, 200, 200, 100);

		frame.setVisible(true);
	}

	// Not sure if this is good yet
	private void scanPopup() {
		frame.setLayout(null);

		JLabel askBag = new JLabel("Do you want to bag this item?", SwingConstants.CENTER);
		askBag.setFont(new Font("Tahoma", Font.PLAIN, 40));
		frame.add(askBag);
		askBag.setBounds(100, 20, 800, 100);

//	    JButton scanYesButton = new JButton("Yes");
//	    frame.add(scanYesButton);
//	    scanYesButton.setBounds(200,200,200,100);

		JButton scanNoButton = new JButton("No");
		frame.add(scanNoButton);
		scanNoButton.setBounds(600, 200, 200, 100);

		scanNoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					stationData.setExpectedWeight(stationData.getStationHardware().baggingArea.getCurrentWeight());
				} catch (OverloadException e1) {
					System.out.println("Error! Scale overloaded when choosing not to bag item!");
				}
				stationData.changeState(StationState.NORMAL);
			}
		});

		debugPlaceTestItemButton();

		frame.setVisible(true);

//	    scanYesButton.addActionListener(new ActionListener() {
//	        public void actionPerformed(ActionEvent e) {
//	        	stationData.changeState(StationState.NORMAL);
//	        }
//	    });
	}

	// BRODY
	private void debugPlaceTestItemButton() {
		TestBarcodedProducts testProducts = new TestBarcodedProducts();
		final BarcodedItem testMilkJug = new TestBarcodedProducts()
				.getItem(testProducts.getBarcodedProductList().get(0));
		Color color = new Color(128, 128, 255);
		JButton payCoin = new JButton();
		payCoin.setBounds(250, 150, 300, 200);
		payCoin.setText("[DEBUG] Put Milk Jug in Bagging Area");
		payCoin.setFont(new Font("Calibri", Font.BOLD, 16));
		payCoin.setBackground(color);

		payCoin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stationData.getStationHardware().baggingArea.add(stationData.getTestProducts().getItemList().get(0));
			}
		});

		frame.add(payCoin);
		payCoin.setVisible(true);
	}
}
