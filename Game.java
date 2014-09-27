import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.PrintWriter;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Game extends JPanel {

	@SuppressWarnings("unused")
	private PrintWriter pwrite;
	Racquet racquet;

	public Game(PrintWriter pwrite) throws InterruptedException {
		this.pwrite = pwrite;
		racquet = new Racquet(this, pwrite);
		addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				racquet.keyReleased(e);
			}

			@Override
			public void keyPressed(KeyEvent e) {
				racquet.keyPressed(e);
			}
		});
		setFocusable(true);
		new Game2(this).start();
	}

	private void move() {
		racquet.move();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		racquet.paint(g2d);
	}

	public class Game2 extends Thread {
		Game game;
		
		public Game2(Game game){
			this.game = game;
		}
		
		public void run() {
			
			JFrame frame = new JFrame("Explore");
			frame.add(game);
			frame.setSize(700, 700);
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setResizable(false);
			while (true) {
				game.move();
				game.repaint();
				try {
					Thread.sleep(7);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
}