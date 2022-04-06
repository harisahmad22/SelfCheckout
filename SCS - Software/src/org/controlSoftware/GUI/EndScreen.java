import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EndScreen extends JFrame {

	private JPanel contentPane;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EndScreen frame = new EndScreen();
					frame.setVisible(true);
					
					ActionListener taskPerformer = new ActionListener() {
			        	public void actionPerformed(ActionEvent evt) {
							frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			            }
			        };
			        
			        Timer timer = new Timer(5000, new ActionListener() {
	                    @Override
	                    public void actionPerformed(ActionEvent e) {
	                        frame.dispose();

	    	                Welcome frame2 = new Welcome();
	    					frame2.setVisible(true);
	                    }
	                });
	                timer.start();
	                
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public EndScreen() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1000, 600);
		contentPane = new JPanel() {  
			public void paintComponent(Graphics g) {  
			Image img = Toolkit.getDefaultToolkit().getImage(  
					EndScreen.class.getResource("/images/thankyou.jpeg"));  
			g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);  
			}  
		}; 
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 24));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(25, 62, 1195, 506);
		contentPane.add(lblNewLabel);
	}
	
	private void closeTheCurrentFrameAndOpenNew(java.awt.event.ActionEvent evt){

		 dispose();//To close the current window

		 EndScreen closeCurrentWindow = new EndScreen();
		 closeCurrentWindow.setVisible(true);//Open the new window

		}

}
