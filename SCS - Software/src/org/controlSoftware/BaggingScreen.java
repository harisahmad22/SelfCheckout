package org.lsmr.selfcheckout.devices;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import testing.Numpad;

public class BaggingScreen(SelfCheckoutStation newStation, SelfCheckoutData newData){
	station = newStation;
	stationData = newData;
	
	frame = station.screen.getFrame();
	width = frame.getWidth();
	height = frame.getHeight();
}

private static void Main(JFrame main){
	main.setLayout(null);
	
	JLabel itemList = new JLabel("Placeholder for list of scanned items");
	itemList.setBounds(20,20,700,420);
	itemList.setBackground(Color.blue);
	itemList.setOpaque(true);
	main.getContentPane().add(itemList);
	
	JLabel totalPrice = new JLabel("Placeholder for total price");
	totalPrice.setBounds(20,460,700,80);
	totalPrice.setBackground(Color.red);
	totalPrice.setOpaque(true);
	main.getContentPane().add(totalPrice);
	
	JPanel options = new JPanel();
	options.setLayout(new GridLayout(0,1));
	JButton itemButton = new JButton("Item Lookup");
	options.add(itemButton);
	itemButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
        	main.getContentPane().removeAll();
        	main.getContentPane().revalidate();
        	main.getContentPane().repaint();
        	LetterSearch(main);
        }
    });
	JButton pluButton = new JButton("PLU Lookup");
	options.add(pluButton);
	pluButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
        	main.getContentPane().removeAll();
        	main.getContentPane().revalidate();
        	main.getContentPane().repaint();
            PluSearch(main);
        }
    });
	JButton assistButton = new JButton("Ask Attendant for Assistance");
	options.add(assistButton);
	assistButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
        	//I don't know how we're doing this
        }
    });
	JButton checkoutButton = new JButton("Proceed to checkout");
	options.add(checkoutButton);
	checkoutButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
        	JOptionPane.showMessageDialog(null, "alert", "alert", JOptionPane.PLAIN_MESSAGE);
        }
    });
	options.setBounds(740,20,200,520);
	main.getContentPane().add(options);
	
	main.setVisible(true);
}

private static void PluSearch(JFrame searchPLU){
	searchPLU.setLayout(null);
	
	JLabel inventoryPLU = new JLabel("Placeholder for inventory");
	searchPLU.add(inventoryPLU);
	inventoryPLU.setBounds(20,20,700,520);
	inventoryPLU.setBackground(Color.blue);
	inventoryPLU.setOpaque(true);
	
	JLabel codePLU = new JLabel("Placeholder for PLU input");
	searchPLU.add(codePLU);
	codePLU.setBounds(740,100,220,100);
	codePLU.setBackground(Color.red);
	codePLU.setOpaque(true);
	
	JPanel numpad = new JPanel();
	numpad.setLayout(new GridLayout(0,3));
	JButton numpad1 = new JButton("1");
	numpad1.addActionListener(new Numpad());
	numpad.add(numpad1);
	JButton numpad2 = new JButton("2");
	numpad2.addActionListener(new Numpad());
	numpad.add(numpad2);
	JButton numpad3 = new JButton("3");
	numpad3.addActionListener(new Numpad());
	numpad.add(numpad3);
	JButton numpad4 = new JButton("4");
	numpad4.addActionListener(new Numpad());
	numpad.add(numpad4);
	JButton numpad5 = new JButton("5");
	numpad5.addActionListener(new Numpad());
	numpad.add(numpad5);
	JButton numpad6 = new JButton("6");
	numpad6.addActionListener(new Numpad());
	numpad.add(numpad6);
	JButton numpad7 = new JButton("7");
	numpad7.addActionListener(new Numpad());
	numpad.add(numpad7);
	JButton numpad8 = new JButton("8");
	numpad8.addActionListener(new Numpad());
	numpad.add(numpad8);
	JButton numpad9 = new JButton("9");
	numpad9.addActionListener(new Numpad());
	numpad.add(numpad9);
	JButton numpad0 = new JButton("0");
	numpad0.addActionListener(new Numpad());
	numpad.add(numpad0);
	JButton numpadDel = new JButton("Del");
	numpadDel.addActionListener(new Numpad());
	numpad.add(numpadDel);
	searchPLU.add(numpad);
	numpad.setBounds(740,220,220,320);
	
	JButton pluReturn = new JButton("Return");
	searchPLU.add(pluReturn);
	pluReturn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
        	searchPLU.getContentPane().removeAll();
        	searchPLU.getContentPane().revalidate();
        	searchPLU.getContentPane().repaint();
            Main(searchPLU);
        }
    });
	pluReturn.setBounds(740,20, 220, 60);
	
	searchPLU.setVisible(true);
	
}

private static void LetterSearch(JFrame searchLetter){
	String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"}; 
	searchLetter.setLayout(null);

	JLabel inventoryLetter = new JLabel("Placeholder for inventory");
	searchLetter.add(inventoryLetter);
	inventoryLetter.setBounds(20,20,700,520);
	inventoryLetter.setBackground(Color.blue);
	inventoryLetter.setOpaque(true);
	
	JScrollPane alphabetContainer = new JScrollPane();
	JList alphabetList = new JList(letters);
	alphabetContainer.setViewportView(alphabetList);
    alphabetList.setLayoutOrientation(JList.VERTICAL);
	searchLetter.add(alphabetContainer);
	alphabetList.setFont(new Font("Tahoma", Font.PLAIN, 80));
	alphabetContainer.setBounds(740,100,220,440);
	
	JButton alphabetReturn = new JButton("Return to Scanning");
	searchLetter.add(alphabetReturn);
	alphabetReturn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
        	searchLetter.getContentPane().removeAll();
        	searchLetter.getContentPane().revalidate();
        	searchLetter.getContentPane().repaint();
            Main(searchLetter);
        }
    });
	alphabetReturn.setBounds(740,20,220,60);
	
	searchLetter.setVisible(true);
}

private static void checkoutPopup(JFrame popupCheckout){
	JOptionPane dialogCheckout;
	JLabel confirmCheckout;
	JButton outYesButton;
	JButton outNoButton;
	
    dialogCheckout = new JOptionPane(popupCheckout);
    dialogCheckout.setLayout(null);
    dialogCheckout.setBounds(200, 100, 400, 400);
    

    confirmCheckout = new JLabel("Are you sure you want to proceed to checkout?", SwingConstants.CENTER);
    confirmCheckout.setFont(new Font("Tahoma", Font.PLAIN, 25));
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

private static void scanPopup(JFrame popupScan){
	JDialog dialogScan;
	JLabel askBag;
	JButton scanYesButton;
	JButton scanNoButton;
	
	popupScan = new JFrame("Confirm Checkout");
    dialogScan = new JDialog(popupScan);
    dialogScan.setLayout(null);
    dialogScan.setBounds(200, 200, 800, 400);
    dialogScan.setUndecorated(true);
    dialogScan.setResizable(false);
    dialogScan.setAlwaysOnTop(true);  

    askBag = new JLabel("Do you want to bag this item?", SwingConstants.CENTER);
    askBag.setFont(new Font("Tahoma", Font.PLAIN, 40));
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

class Numpad implements ActionListener{
public void actionPerformed(ActionEvent e) {
	if(e.getActionCommand() != "Del") {
		int num = Integer.parseInt(e.getActionCommand());
		if (l1.getText().length() < 12) {
			l1.setText(l1.getText() + val);
		}
		
		System.out.println(num);
	}
	else {
		l1.setText(l1.getText().substring(0, l1.getText().length()-1));
	}
}
}

	