package org.lsmr.selfcheckout.devices;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.*;

public class BaggingScreen {}

public class main(){
	JFrame main;
	JLabel itemList;
	JLabel totalPrice;
	JPanel options;
	JButton itemButton;
	JButton pluButton;
	JButton assistButton;
	JButton checkoutButton;
	JButton exitButton;
		
	main = new JFrame("Scanning and Bagging");
	main.setSize(1920,1080);
	main.setLayout(null);
	
	itemList = new JLabel("Placeholder for list of scanned items");
	main.add(itemList);
	itemList.setBounds(20,20,1300,800);
	itemList.setBackground(Color.blue);
	itemList.setOpaque(true);
	
	totalPrice = new JLabel("Placeholder for total price");
	main.add(totalPrice);
	totalPrice.setBounds(20,850,1300,150);
	totalPrice.setBackground(Color.red);
	totalPrice.setOpaque(true);
	
	options = new JPanel();
	options.setLayout(new GridLayout(0,1));
	itemButton = new JButton("Item Lookup");
	options.add(itemButton);
	pluButton = new JButton("PLU Lookup");
	options.add(pluButton);
	assistButton = new JButton("Ask Attendant for Assistance");
	options.add(assistButton);
	checkoutButton = new JButton("Proceed to checkout");
	options.add(checkoutButton);
	exitButton = new JButton("Cancel Purchase");
	options.add(exitButton);
	main.add(options);
	options.setBounds(1340,20,540,980);
	
	main.setVisible(true);
}

public class PluSearch(){

	JFrame searchPLU;
	JLabel inventoryPLU;
	JLabel codePLU;
	JPanel numpad;
	JButton numpad1;
	JButton numpad2;
	JButton numpad3;
	JButton numpad4;
	JButton numpad5;
	JButton numpad6;
	JButton numpad7;
	JButton numpad8;
	JButton numpad9;
	JButton numpad0;
	JButton numpadDel;
	JButton pluReturn;
	
	searchPLU = new JFrame("Search by PLU Code");
	searchPLU.setSize(1920,1080);
	searchPLU.setLayout(null);
	
	inventoryPLU = new JLabel("Placeholder for inventory");
	searchPLU.add(inventoryPLU);
	inventoryPLU.setBounds(20,20,1300,1000);
	inventoryPLU.setBackground(Color.blue);
	inventoryPLU.setOpaque(true);
	
	codePLU = new JLabel("Placeholder for PLU input");
	searchPLU.add(codePLU);
	codePLU.setBounds(1340,140,540,160);
	codePLU.setBackground(Color.red);
	codePLU.setOpaque(true);
	
	numpad = new JPanel();
	numpad.setLayout(new GridLayout(0,3));
	numpad1 = new JButton("1");
	numpad.add(numpad1);
	numpad2 = new JButton("2");
	numpad.add(numpad2);
	numpad3 = new JButton("3");
	numpad.add(numpad3);
	numpad4 = new JButton("4");
	numpad.add(numpad4);
	numpad5 = new JButton("5");
	numpad.add(numpad5);
	numpad6 = new JButton("6");
	numpad.add(numpad6);
	numpad7 = new JButton("7");
	numpad.add(numpad7);
	numpad8 = new JButton("8");
	numpad.add(numpad8);
	numpad9 = new JButton("9");
	numpad.add(numpad9);
	numpad0 = new JButton("0");
	numpad.add(numpad0);
	numpadDel = new JButton("Del");
	numpad.add(numpadDel);
	searchPLU.add(numpad);
	numpad.setBounds(1340,320,540,700);
	
	pluReturn = new JButton("Return to Scanning");
	searchPLU.add(pluReturn);
	pluReturn.setBounds(1620,20, 260, 100);
	
	searchPLU.setVisible(true);
	
}

public class letterSearch(){
	String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"}; 
	JFrame searchLetter;
	JLabel inventoryLetter;
	JScrollPane alphabetContainer;
	JList alphabetList;
	JButton alphabetReturn;
	
	searchLetter = new JFrame("Search by Letter");
	searchLetter.setLayout(null);

	inventoryLetter = new JLabel("Placeholder for inventory");
	searchLetter.add(inventoryLetter);
	inventoryLetter.setBounds(20,20,1550,1000);
	inventoryLetter.setBackground(Color.blue);
	inventoryLetter.setOpaque(true);
	
	alphabetContainer = new JScrollPane();
	alphabetList = new JList(letters);
	alphabetContainer.setViewportView(alphabetList);
    alphabetList.setLayoutOrientation(JList.VERTICAL);
	searchLetter.add(alphabetContainer);
	alphabetList.setFont(new Font("Arial", Font.PLAIN, 80));
	alphabetContainer.setBounds(1680,150,160,870);
	
	alphabetReturn = new JButton("Return to Scanning");
	searchLetter.add(alphabetReturn);
	alphabetReturn.setBounds(1620,20, 260, 100);
	
	searchLetter.setSize(1920,1080);
	searchLetter.setVisible(true);
}

public class checkoutPopup(){
	JFrame popupCheckout;
	JDialog dialogCheckout;
	JLabel confirmCheckout;
	JButton outYesButton;
	JButton outNoButton;
	
	popupCheckout = new JFrame("Confirm Checkout");
    dialogCheckout = new JDialog(popupCheckout);
    dialogCheckout.setLayout(null);
    dialogCheckout.setBounds(200, 200, 800, 400);

    confirmCheckout = new JLabel("Are you sure you want to proceed to checkout?", SwingConstants.CENTER);
    confirmCheckout.setFont(new Font("Arial", Font.PLAIN, 25));
    dialogCheckout.add(confirmCheckout);
    confirmCheckout.setBounds(100,20,600,100);
    
    outYesButton = new JButton("Yes");
    dialogCheckout.add(outYesButton);
    outYesButton.setBounds(150,200,200,100);
    
    outNoButton = new JButton("No");
    dialogCheckout.add(outNoButton);
    outNoButton.setBounds(450,200,200,100);
    
    dialogCheckout.setVisible(true);
}

public class scanPopup(){
	JFrame popupScan;
	JDialog dialogScan;
	JLabel askBag;
	JButton scanYesButton;
	JButton scanNoButton;
	
	popupScan = new JFrame("Confirm Checkout");
    dialogScan = new JDialog(popupScan);
    dialogScan.setLayout(null);
    dialogScan.setBounds(200, 200, 800, 400);

    askBag = new JLabel("Do you want to bag this item?", SwingConstants.CENTER);
    askBag.setFont(new Font("Arial", Font.PLAIN, 40));
    dialogScan.add(askBag);
    askBag.setBounds(100,20,600,100);
    
    scanYesButton = new JButton("Yes");
    dialogScan.add(scanYesButton);
    scanYesButton.setBounds(150,200,200,100);
    
    scanNoButton = new JButton("No");
    dialogScan.add(scanNoButton);
    scanNoButton.setBounds(450,200,200,100);
    
    dialogScan.setVisible(true);
}

	