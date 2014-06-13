import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class mainGUI extends JPanel implements ActionListener {
	PrintWriter pwrite;
	private static final long serialVersionUID = 1L;
	protected JTextField textField;
    protected JTextArea textArea;
    private final static String newline = "\n";
	private boolean gameNotStarted = true;
	Game game;
	
	public mainGUI(PrintWriter pwrite) {
		 super(new GridBagLayout());
		 
			this.pwrite = pwrite;
	        textField = new JTextField(20);
	        textField.addActionListener(this);
	 
	        textArea = new JTextArea(20, 50);
	        textArea.setEditable(false);
	        JScrollPane scrollPane = new JScrollPane(textArea);
	 
	        //Add Components to this panel.
	        GridBagConstraints c = new GridBagConstraints();
	        c.gridwidth = GridBagConstraints.REMAINDER;
	        c.fill = GridBagConstraints.HORIZONTAL;
	        c.fill = GridBagConstraints.BOTH;
	        c.weightx = 1.0;
	        c.weighty = 1.0;
	        add(scrollPane, c);
	        add(textField, c);
	    
	}
	
	  public void actionPerformed(ActionEvent evt) {
	        String text = textField.getText();
	        
	        //appendIt(text);
	        pwrite.println(text);
	        
	        if(text.equals("/game") && gameNotStarted){
	        	gameNotStarted = false;
	        	try {
					game = new Game(pwrite);
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
	        }
	        
	        textField.setText("");
	 
	        //Make sure the new text is visible, even if there
	        //was a selection in the text area.
	        textArea.setCaretPosition(textArea.getDocument().getLength());
	    }
	  public void appendIt(String text){
		  textArea.append(text + newline);
	  }
}
