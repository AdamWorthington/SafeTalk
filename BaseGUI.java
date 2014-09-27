import java.io.PrintWriter;

import javax.swing.JFrame;


public class BaseGUI extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	PrintWriter pwrite;
	
	
	/***COMPONENTS TO ADD TO FRAME***/
	mainGUI mGUI;
	/********************************/
	
	
	public BaseGUI(PrintWriter pwrite){
		this.pwrite = pwrite;
		makeBase();	
	}
	public void makeBase(){
		
		JFrame frame = new JFrame("IRC");
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
