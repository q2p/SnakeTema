package tema.snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public final class SnakeTema {
	static final byte mapSize = 16;
	static final byte cellSize = 16;
	static final short minSizes = mapSize*cellSize;
	private static final Frame frame = new Frame();
		
	private static final boolean wall[][] = new boolean[mapSize][mapSize];
	private static short wallCount = 0;
	private static final Random random = new Random();
	final static ArrayList<byte[]> snakeParts = new ArrayList<byte[]>();
	static short puzo = 4;
	static byte[] rotation = null;
	static byte[] fruitPos = new byte[2];
	
	static {
		byte padding = 2*(mapSize/5)-1;

		for(byte y = 0; y != mapSize; y++)
			for(byte x = 0; x != mapSize; x++)
				wall[y][x] = true;
		
		for(byte y = 1; y != mapSize-1; y++)
			for(byte x = 1; x != mapSize-1; x++)
				wall[y][x] = false;
		
		for(byte p = padding; p != mapSize-padding; p++) {
			wall[0][p] = false;
			wall[mapSize-1][p] = false;
			wall[p][0] = false;
			wall[p][mapSize-1] = false;
		}

		for(byte y = 0; y != mapSize; y++)
			for(byte x = 0; x != mapSize; x++)
				if(wall[y][x]) wallCount++;
		
		snakeParts.add(findFreeSpace());
		fruitPos = findFreeSpace();
	}
	
	private static final byte[] findFreeSpace() {
		short id;
		try {
			id = (short)random.nextInt(mapSize*mapSize - wallCount - snakeParts.size());
		} catch(IllegalArgumentException e) {
			return null;
		}
		
		for(byte y = 0; y != mapSize; y++) {
			for(byte x = 0; x != mapSize; x++) {
				if(wall[y][x] == false) {
					boolean solid = false;
					for(final byte[] pos : snakeParts) {
						if(pos[0] == x && pos[1] == y) {
							solid = true;
							break;
						}
					}
					
					if(solid)
						continue;
					
					if(id == 0)
						return new byte[] {x, y};
					id--;
				}
			}
		}
		return null;
	}
	
	public static void main(final String[] args) {
		while(true) {
			logic();
			render();
			sleep();
		}
	}
	
	private static void logic() {
		exchange();

		controll();
		
		moveSnake();
		
		checkLoss();
				
		generateFruit();
	}
	
	private static void checkLoss() {
		boolean lost = wall[snakeParts.get(0)[1]][snakeParts.get(0)[0]];
		
		for(int i = 1; i != snakeParts.size(); i++)
			if(snakeParts.get(0)[0] == snakeParts.get(i)[0] && snakeParts.get(0)[1] == snakeParts.get(i)[1])
				lost = true;
		
		if(lost) {
			g.dispose();
			frame.dispose();
			JOptionPane.showMessageDialog(null, "You lost.", "Shit happens.", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}

	private static void controll() {
		byte[] dr = null;
		
		if(up)
			dr = new byte[] {0,-1};
		else if(down)
			dr = new byte[] {0,1};
		else if(left)
			dr = new byte[] {-1,0};
		else if(right)
			dr = new byte[] {1,0};
		
		if(rotation == null) { 
			rotation = dr;
		} else if(dr != null) {
			if(dr[0]-rotation[0] != 0 || dr[1]-rotation[1] != 0) {
				rotation = dr;
			}
		}
	}

	private static void generateFruit() {
		if(fruitPos == null)
			fruitPos = findFreeSpace();
		if(fruitPos == null) {
			g.dispose();
			frame.dispose();
			JOptionPane.showMessageDialog(null, "You Won!", "Congrats, bruh!", JOptionPane.INFORMATION_MESSAGE);
			System.exit(0);
		}
	}

	private static void moveSnake() {
		if(rotation != null) {
			final byte[] prev = snakeParts.get(snakeParts.size()-1);
			for(int i = snakeParts.size()-1; i > 0; i--) {
				snakeParts.set(i, snakeParts.get(i-1));
			}
			byte[] npos = new byte[] {
				(byte)(snakeParts.get(0)[0]+rotation[0]),
				(byte)(snakeParts.get(0)[1]+rotation[1])
			};
			if(npos[0] < 0)
				npos[0] = mapSize-1;
			if(npos[0] > mapSize-1)
				npos[0] = 0;
			if(npos[1] < 0)
				npos[1] = mapSize-1;
			if(npos[1] > mapSize-1)
				npos[1] = 0;
			snakeParts.set(0, npos);
			if(snakeParts.get(0)[0] == fruitPos[0] && snakeParts.get(0)[1] == fruitPos[1]) {
				fruitPos = null;
				puzo++;
			}
			if(puzo != 0) {
				puzo--;
				snakeParts.add(prev);
			}
		}
	}

	private static void exchange() {
		synchronized (LOCK) {
			up = iup;
			down = idown;
			left = ileft;
			right = iright;
		}
	}

	private static BufferedImage surface = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(minSizes, minSizes, Transparency.OPAQUE);
	private static Graphics g = surface.getGraphics();
	
	private static void render() {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, minSizes, minSizes);

		for(byte y = 0; y != mapSize; y++) {
			for(byte x = 0; x != mapSize; x++) {
				if(wall[y][x])
					g.setColor(Color.WHITE);
				else if(fruitPos[0] == x && fruitPos[1] == y)
					g.setColor(Color.GREEN);
				else
					g.setColor(Color.BLACK);
				
				g.fillRect(x*cellSize, y*cellSize, cellSize, cellSize);
			}
		}
		
		for(short i = 0; i != snakeParts.size(); i++) {
			g.setColor(i==0?Color.ORANGE:Color.RED);
			g.fillRect(snakeParts.get(i)[0]*cellSize, snakeParts.get(i)[1]*cellSize, cellSize, cellSize);
		}
		
		frame.getContentPane().getGraphics().drawImage(surface, 0, 0, null);
	}

	private static boolean up = false;
	private static boolean down = false;
	private static boolean left = false;
	private static boolean right = false;
	
	private static boolean iup = false;
	private static boolean idown = false;
	private static boolean ileft = false;
	private static boolean iright = false;

	private static final Object LOCK = new Object();
	
	static final void push(final int code) {
		synchronized (LOCK) {
			switch(code) {
				case KeyEvent.VK_UP:
					iup = true;
					return;
				case KeyEvent.VK_DOWN:
					idown = true;
					return;
				case KeyEvent.VK_LEFT:
					ileft = true;
					return;
				case KeyEvent.VK_RIGHT:
					iright = true;
			}
		}
	}
	static final void pull(final int code) {
		synchronized (LOCK) {
			switch(code) {
				case KeyEvent.VK_UP:
					iup = false;
					return;
				case KeyEvent.VK_DOWN:
					idown = false;
					return;
				case KeyEvent.VK_LEFT:
					ileft = false;
					return;
				case KeyEvent.VK_RIGHT:
					iright = false;
			}
		}
	}
	private static final short milisecPerFrame = 1000/5;
	static void sleep() {
		try {
			Thread.sleep(milisecPerFrame);
		} catch (InterruptedException e) {}
	}
}