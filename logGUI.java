import java.io.PrintWriter;

import javax.swing.*;

public class logGUI extends JFrame{
	PrintWriter pwrite;
	private static final long serialVersionUID = 1L;
	boolean logInCheck = false;

	

	public logGUI(PrintWriter pwrite) {
		super("Server Client");
		setSize(400, 300);
		setResizable(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.pwrite = pwrite;
	}

	public void logInGUI() {
		
		JTextField username = new JTextField();
		JTextField password = new JPasswordField();
		Object[] message = { "Username:", username, "Password:", password };
		int option = JOptionPane.showConfirmDialog(null, message, "Login",
				JOptionPane.OK_CANCEL_OPTION);

		String user, pass;

		if (option == JOptionPane.OK_OPTION) {
			user = username.getText();
			pwrite.println(user);
			pass = password.getText();
			pwrite.println(pass);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.err.println("THREAD NO TIRED");
			}
			
			if (logInCheck) {
				System.out.println("Login successful");
				runMainGUI();
			} else {
				System.out.println("login failed");
			}
		} else {
			System.out.println("Login canceled");
		}
	}
	public void passed(){
		logInCheck = true;
	}
	public boolean getStatus(){
		return logInCheck;
	}
	mainGUI mGUI;
	public void runMainGUI(){
		 JFrame frame = new JFrame("mainGUI");
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	 
	        //Add contents to the window.
	        frame.add(mGUI = new mainGUI(pwrite));
	 
	        //Display the window.
	        frame.pack();
	        frame.setVisible(true);
	}
	public void appendIt(String s){
		mGUI.appendIt(s);
	}
}
