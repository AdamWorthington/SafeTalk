import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;

public class Racquet {
	
	PrintWriter pwrite;
	private final int WIDTH = 20;
	private final int HEIGHT = 20;
	private int x = 0;
	private int y = 0;
	
	private int numberOnline = 0;
	private String[] nameArray = new String[10];
	private int[] xArray = new int[10];
	private int[] yArray = new int[10];
		
	private int left = 0;
	private int right = 0;
	private int up = 0;
	private int down = 0;
	
	private Game game;

	public Racquet(Game game, PrintWriter pwrite) {
		this.game = game;
		this.pwrite = pwrite;
	}

	public void move() {
		if (x + left + right > 0 && x + left + right < game.getWidth() - WIDTH){
			x = x + left + right; 
		}
		if (y + up + down > 0 && y + up + down < game.getHeight() - HEIGHT){
			y = y + up + down;
		}
		pwrite.println("/pos " + x + " " + y);
	}

	public void paint(Graphics g) {
		
		for(int i = 0; i < numberOnline; i++){
			g.fillRect(xArray[i], yArray[i], WIDTH, HEIGHT);
			g.drawString(nameArray[i], xArray[i] - 5, yArray[i] - 7);
		}
	}

	public void keyReleased(KeyEvent e) {
		
		int temp = e.getKeyCode();
		
		if (temp == KeyEvent.VK_LEFT)
			left = 0;
		else if (temp == KeyEvent.VK_RIGHT)
			right = 0;
		else if (temp == KeyEvent.VK_UP)
			up = 0;
		else if (temp == KeyEvent.VK_DOWN)
			down = 0;
	}

	public void keyPressed(KeyEvent e) {
		
		int temp = e.getKeyCode(); 
		
		if (temp == KeyEvent.VK_LEFT)
			left = -1;
		else if (temp == KeyEvent.VK_RIGHT)
			right = 1;
		else if (temp == KeyEvent.VK_UP)
			up = -1;
		else if (temp == KeyEvent.VK_DOWN)
			down = 1;
	}

	public Rectangle getBounds() {
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}

	public int getTopY() {
		return y;
	}
	public int getY(){
		return y;
	}
	public int getX(){
		return x;
	}
	public void setNumberOnline(int curr){
		numberOnline = curr;
	}
	public void setNamePos(int pos, String curr){
		nameArray[pos] = curr;
	}
	public void setXPos(int pos, int curr){
		xArray[pos] = curr;
	}
	public void setYPos(int pos, int curr){
		yArray[pos] = curr;
	}
}