package com.loovjo.snake;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class SnakeGraphics extends JPanel implements Runnable, KeyListener,
		MouseListener {

	public static String title = "";
	public float sqW, sqH;
	public Snake s = new Snake();
	boolean play = false;

	public static void main(String[] args) {

		JFrame frame = new JFrame(title);
		frame.setSize(640, 400);
		frame.setLocationRelativeTo(null);
		SnakeGraphics main = new SnakeGraphics();
		main.start();
		frame.add(main);
		frame.addKeyListener(main);
		frame.addMouseListener(main);

		frame.setVisible(true);
	}

	public void paint(Graphics g) {
		s.snakePop();
		sqW = (float) getWidth() / s.BOARD_WIDTH;
		sqH = (float) getHeight() / s.BOARD_HEIGHT;
		for (int x = 0; x < s.BOARD_WIDTH; x++) {
			for (int y = 0; y < s.BOARD_WIDTH; y++) {
				int b = s.tiles[x][y];
				if (b == 0) {
					g.setColor(Color.gray);
					try {
						int lowest = Integer.MAX_VALUE;
						int highest = Integer.MIN_VALUE;
						for (int i = 0; i < s.astar.length; i++)
							for (int j = 0; j < s.astar[i].length; j++) {
								if (s.astar[i][j] > highest)
									highest = s.astar[i][j];
								if (s.astar[i][j] < lowest)
									lowest = s.astar[i][j];
							}

						// g.setColor(new
						// Color(Color.HSBtoRGB(map(s.astar[x][y], lowest,
						// highest,0f,1f), 0.5f, 1)));
					} catch (Exception e) {
					}
				} else if (b == 1) {
					float i = (float) s.snake.lastIndexOf(new Vector(x, y)) / 20; // Hue
					if ((float) s.snake.lastIndexOf(new Vector(x, y)) == s.snake
							.size() - 1)
						g.setColor(Color.black);
					g.setColor(new Color(Color.HSBtoRGB(1 - i, 1, s.dead ? 0.5f
							: 1)));
				}
				for (Vector portal : s.portals)
					if (portal.getLengthTo(new Vector(x, y)) == 0)
						g.setColor(new Color(128, 255, 255));
				if (s.applePos != null
						&& s.applePos.getLengthTo(new Vector(x, y)) == 0)
					g.setColor(Color.white);
				g.fillRect((int) (x * sqW), (int) (y * sqH), (int) sqW,
						(int) sqH);
				g.setColor(Color.black);
				g.drawRect((int) (x * sqW), (int) (y * sqH), (int) sqW,
						(int) sqH);
			}
		}
	}

	public float map(float x, float in_min, float in_max, float out_min,
			float out_max) {
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

	private void start() {
		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public void run() {
		while (true) {
			repaint();
			if (play)
				s.ai();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		Vector v = new Vector(e.getX() / sqW, (e.getY() - 23) / sqH);
		if (e.getButton() == 1)
			s.snake.add(v);
		else if (e.getButton() == 3)
			s.tiles[(int) v.getX()][(int) v.getY()] = 2;
		else if (e.getButton() == 2)
			s.tiles[(int) v.getX()][(int) v.getY()] = 0;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyChar() == ' ') {
			play = !play;
			s.lastDirection = -1;
		} else if (e.getKeyChar() == 'r') {
			s.snakePop();
			Vector np = null;
			System.out.print("hej");
			while (true) {
				np = new Vector(s.rand.nextInt(s.BOARD_WIDTH),
						s.rand.nextInt(s.BOARD_HEIGHT));
				if (s.tiles[(int) np.getX()][(int) np.getY()] == 0)
					break;
			}
			System.out.println("san");
			s.snake.remove(0);
			s.snake.add(np);
		} else if (e.getKeyChar() == 's')
			s = new Snake();
		else if (e.getKeyChar() == 'a') {
			s.apple = !s.apple;
			s.applePos = null;
		}
		int d = -1;
		if (e.getKeyCode() == e.VK_UP)
			d = 0;
		if (e.getKeyCode() == e.VK_RIGHT)
			d = 2;
		if (e.getKeyCode() == e.VK_DOWN)
			d = 4;
		if (e.getKeyCode() == e.VK_LEFT)
			d = 6;
		if (d > -1) {

			s.lastDirection = d;
		}
		if (e.getKeyCode() == e.VK_C)
			s.fixWalls();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
