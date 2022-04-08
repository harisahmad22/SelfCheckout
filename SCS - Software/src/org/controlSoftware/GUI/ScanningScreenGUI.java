package org.controlSoftware.GUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import org.driver.SelfCheckoutData;
import org.driver.SelfCheckoutData.State;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

public class ScanningScreenGUI {
	private SelfCheckoutStation station;
	private SelfCheckoutData stationData;
	
	private JFrame frame;
	
	public ScanningScreenGUI(SelfCheckoutStation newStation, SelfCheckoutData newData){
		station = newStation;
		stationData = newData;
		
		frame = station.screen.getFrame();
	}

	public void stateChanged() {
		frame.getContentPane().removeAll();
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();

		switch (stationData.getState()) {
		case MAIN_SCAN:
			Main();
			break;
		case LETTER_SEARCH:
			PluSearch();
			break;
		case PLU_SEARCH:
			LetterSearch();
			break;
		case CHECKOUT_CHECK:
			checkoutPopup();
			break;
		default:
			break;
		}
	}
	// The main page for scanning
	private void Main(){
		frame.setLayout(null);
		
		// Display of scanned items
		JLabel itemList = new JLabel("Placeholder for list of scanned items");
		itemList.setBounds(20,20,700,420);
		itemList.setBackground(Color.blue);
		itemList.setOpaque(true);
		frame.getContentPane().add(itemList);
		
		// Display of the total price
		JLabel totalPrice = new JLabel("Placeholder for total price");
		totalPrice.setBounds(20,460,700,80);
		totalPrice.setBackground(Color.red);
		totalPrice.setOpaque(true);
		frame.getContentPane().add(totalPrice);
		
		// All of the option buttons
		JPanel options = new JPanel();
		options.setLayout(new GridLayout(0,1));
		JButton itemButton = new JButton("Item Lookup");
		options.add(itemButton);
		itemButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	stationData.changeState(State.LETTER_SEARCH);
	        }
	    });
		JButton pluButton = new JButton("PLU Lookup");
		options.add(pluButton);
		pluButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	stationData.changeState(State.PLU_SEARCH);
	        }
	    });
		JButton assistButton = new JButton("Ask Attendant for Assistance");
		options.add(assistButton);
		assistButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	//Ask for assistance
	        }
	    });
		JButton checkoutButton = new JButton("Proceed to checkout");
		options.add(checkoutButton);
		checkoutButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	stationData.changeState(State.CHECKOUT_CHECK);
	        }
	    });
		options.setBounds(740,20,200,520);
		frame.getContentPane().add(options);
		
		frame.setVisible(true);
	}

	// Page for looking for PLU
	private void PluSearch(){
		frame.setLayout(null);
		
		// Display of searched items
		JLabel inventoryPLU = new JLabel("Placeholder for inventory");
		frame.add(inventoryPLU);
		inventoryPLU.setBounds(20,20,700,520);
		inventoryPLU.setBackground(Color.blue);
		inventoryPLU.setOpaque(true);
		
		// Shows current input
		final JLabel codePLU = new JLabel("");
		frame.add(codePLU);
		codePLU.setFont(new Font("Tahoma", Font.PLAIN, 40));
		codePLU.setBounds(740,100,220,100);
		codePLU.setBackground(Color.gray);
		codePLU.setOpaque(true);
		
		// Code for recording numbers (From Jonah)
		ActionListener Numpad = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton src = (JButton) e.getSource();
				String val = src.getText();
				if (val == "DEL") {
					codePLU.setText(codePLU.getText().substring(0, codePLU.getText().length()-1));
				}
				else if (val == "GO") {
					String search = codePLU.getText();
				}
				else {
					if (codePLU.getText().length() < 5) {
						codePLU.setText(codePLU.getText() + val);
					}
				}
				
			}
		};
		
		// All the numpad buttons
		JPanel numpad = new JPanel();
		numpad.setLayout(new GridLayout(0,3));
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
		numpad.setBounds(740,220,220,320);
		
		// Return to main scanning screen
		JButton pluReturn = new JButton("Return");
		frame.add(pluReturn);
		pluReturn.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	stationData.changeState(State.MAIN_SCAN);
	        }
	    });
		pluReturn.setBounds(740,20, 220, 60);
		
		frame.setVisible(true);
		
	}

	// Screen for searching by letter
	private void LetterSearch(){
		final String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"}; 
		frame.setLayout(null);

		// Display for search
		JLabel inventoryLetter = new JLabel("Placeholder for inventory");
		frame.add(inventoryLetter);
		inventoryLetter.setBounds(20,20,700,520);
		inventoryLetter.setBackground(Color.blue);
		inventoryLetter.setOpaque(true);
		
		// List of letters to select
		JScrollPane alphabetContainer = new JScrollPane();
		final JList alphabetList = new JList(letters);
		alphabetContainer.setViewportView(alphabetList);
	    alphabetList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		frame.add(alphabetContainer);
		alphabetList.setFont(new Font("Tahoma", Font.PLAIN, 80));
		alphabetContainer.setBounds(740,100,220,360);
		
		// Button that gets the letter from the list
		JButton alphabetSearch = new JButton("Search");
		alphabetSearch.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	int index = alphabetList.getSelectedIndex();
	        	if(index != -1) {
	        		String search = letters[index];
	        	}
	        }
	    });
		frame.add(alphabetSearch);
		alphabetSearch.setBounds(740,480,220,60);
		
		// Return to main scanning screen
		JButton alphabetReturn = new JButton("Return to Scanning");
		frame.add(alphabetReturn);
		alphabetReturn.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	stationData.changeState(State.MAIN_SCAN);
	        }
	    });
		alphabetReturn.setBounds(740,20,220,60);
		
		frame.setVisible(true);
	}

	// Prompt if user is sure if they want to check out
	private void checkoutPopup(){
	    frame.setLayout(null);

	    //Label for text
	    JLabel confirmCheckout = new JLabel("Are you sure you want to proceed to checkout?", SwingConstants.CENTER);
	    confirmCheckout.setFont(new Font("Tahoma", Font.PLAIN, 35));
	    frame.add(confirmCheckout);
	    confirmCheckout.setBounds(100,20,800,100);
	    
	    // Proceeds to checkout
	    JButton outYesButton = new JButton("Yes");
	    frame.add(outYesButton);
	    outYesButton.setBounds(200,200,200,100);
	    outYesButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	frame.getContentPane().removeAll();
	        	frame.getContentPane().revalidate();
	        	frame.getContentPane().repaint();
	            //Move to checkout
	        }
	    });
	    
	    // Brings user back
	    JButton outNoButton = new JButton("No");
	    frame.add(outNoButton);
	    outNoButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	stationData.changeState(State.MAIN_SCAN);
	        }
	    });
	    outNoButton.setBounds(600,200,200,100);
	    
	    frame.setVisible(true);
	}

	// Not sure if this is good yet
	private void scanPopup(){
		frame.setLayout(null);

	    JLabel askBag = new JLabel("Do you want to bag this item?", SwingConstants.CENTER);
	    askBag.setFont(new Font("Tahoma", Font.PLAIN, 40));
	    frame.add(askBag);
	    askBag.setBounds(100,20,800,100);
	    
	    JButton scanYesButton = new JButton("Yes");
	    frame.add(scanYesButton);
	    scanYesButton.setBounds(200,200,200,100);
	    
	    JButton scanNoButton = new JButton("No");
	    frame.add(scanNoButton);
	    scanNoButton.setBounds(600,200,200,100);
	    
	    frame.setVisible(true);
	}
}

