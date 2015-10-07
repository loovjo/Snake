package com.loovjo.snake;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class Snake implements Cloneable {
	public static int BOARD_WIDTH = 20;
	public static int BOARD_HEIGHT = 20;

	public static Random rand = new Random();
	public boolean apple = true;

	public int[][] tiles; // 0 = empty space, 1 = snake, 2 = wall, 3 = portal
							// Indexed by [x][y]

	public Vector applePos;
	public CopyOnWriteArrayList<Vector> snake = new CopyOnWriteArrayList<Vector>();
	boolean dead;
	public boolean usePortals = false;
	public ArrayList<Vector> portals = new ArrayList<Vector>();
	public int lastDirection = -1;
	int[][] astar = new int[BOARD_WIDTH][];

	public Snake() {
		tiles = new int[BOARD_WIDTH][];
		for (int i = 0; i < tiles.length; i++)
			tiles[i] = new int[BOARD_HEIGHT];
		int walls = Math.max(BOARD_WIDTH, BOARD_HEIGHT) * 3;
		int start = walls / 3;
		walls -= start;
		for (int i = 0; i < start; i++) {
			Vector v = null;
			for (; v == null || tiles[(int) v.getX()][(int) v.getY()] != 0; v = new Vector(
					rand.nextInt(BOARD_WIDTH), rand.nextInt(BOARD_HEIGHT)))
				; // For loop trixery
			tiles[(int) v.getX()][(int) v.getY()] = 2;
		}
		for (int i = 0; i < walls; i++) {
			Vector v = new Vector(rand.nextInt(BOARD_WIDTH),
					rand.nextInt(BOARD_HEIGHT));
			int randDir = rand.nextInt(4) * 2;
			Vector v2 = v.moveInDir(randDir);
			try {
				if (tiles[(int) v2.getX()][(int) v2.getY()] == 2) {
					tiles[(int) v.getX()][(int) v.getY()] = 2;
					continue;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			}
			i--;
		}
		boolean change = true;
		for (int j = 0; j < 50; j++) {
			fixWalls();
		}
		Vector v = null;
		for (; v == null || tiles[(int) v.getX()][(int) v.getY()] != 0; v = new Vector(
				rand.nextInt(BOARD_WIDTH), rand.nextInt(BOARD_HEIGHT)))
			; // For loop trixery
		snake.add(v);
		if (usePortals)
			for (int i = 0; i < 5; i++) {
				Vector port = null;
				for (; port == null
						|| tiles[(int) port.getX()][(int) port.getY()] != 0; port = new Vector(
						rand.nextInt(BOARD_WIDTH), rand.nextInt(BOARD_HEIGHT)))
					; // For loop trixery
				portals.add(port);
			}
		placeApple();
		move(-1);
	}

	public Vector trans(Vector v) {
		for (int i = 0; i < portals.size(); i++) {
			if (v.getLengthTo(portals.get(i)) == 0) {
				return portals.get((i + 1) % portals.size());
			}
		}
		return v;
	}

	public Vector backtrans(Vector v) {
		for (int i = 0; i < portals.size(); i++) {
			if (v.getLengthTo(portals.get(i)) == 0) {
				i--;
				while (i > portals.size())
					i -= portals.size();
				while (i < 0)
					i += portals.size();
				return portals.get(i);
			}
		}
		return v;
	}

	void fixWalls() {
		boolean change = false;
		for (int x = 0; x < BOARD_WIDTH; x++) {
			for (int y = 0; y < BOARD_HEIGHT; y++) {
				// If there are less than 2 free tiles next to this
				// tile,
				// make it a wall
				Vector v = new Vector(x, y);
				if (tiles[(int) v.getX()][(int) v.getY()] != 0)
					continue;
				int freeAround = 0;
				for (int i = 0; i < 8; i += 2) {
					Vector v2 = v.moveInDir(i);
					if (v2.getX() < 0 || v2.getY() < 0
							|| v2.getX() >= BOARD_WIDTH
							|| v2.getY() >= BOARD_HEIGHT)
						freeAround++;
					else if (tiles[(int) v2.getX()][(int) v2.getY()] == 0)
						freeAround++;
				}
				if (freeAround < 2) {
					tiles[(int) v.getX()][(int) v.getY()] = 2;
				}

			}
		}

	}

	public void snakePop() { // Update tiles
		for (int x = 0; x < BOARD_WIDTH; x++)
			for (int y = 0; y < BOARD_HEIGHT; y++)
				if (tiles[x][y] == 1)
					tiles[x][y] = 0;
		for (Vector v : snake) {
			tiles[(int) v.getX()][(int) v.getY()] = 1;
		}
	}

	public void placeApple() {
		int around = 0;
		while (around < 1) {
			for (; applePos == null
					|| tiles[(int) applePos.getX()][(int) applePos.getY()] != 0; applePos = new Vector(
					rand.nextInt(BOARD_WIDTH - 2) + 1,
					rand.nextInt(BOARD_HEIGHT - 2) + 1))
				;
			around = 0;
			for (int i = 0; i < 8; i += 2) {
				Vector v2 = applePos.moveInDir(i);
				if (tiles[(int) v2.getX()][(int) v2.getY()] == 0)
					around++;
			}
		}
	}

	public void move(int dir) {
		lastDirection = dir;
		if (dead)
			return;
		snake.add(snake.get(snake.size() - 1).moveInDir(dir));
		Vector head = snake.get(snake.size() - 1);
		/*
		 * if (head.getX() < 0) head = head.add(new Vector(BOARD_WIDTH, 0)); if
		 * (head.getX() >= BOARD_WIDTH) head = head.sub(new Vector(BOARD_WIDTH,
		 * 0)); if (head.getY() < 0) head = head.add(new Vector(0,
		 * BOARD_HEIGHT)); if (head.getY() >= BOARD_HEIGHT) head = head.sub(new
		 * Vector(0, BOARD_WIDTH)); snake.set(snake.size() - 1, head);
		 */
		boolean eat = false;
		if (applePos != null) {
			for (Vector seg : snake)
				if (seg.getLengthTo(applePos) < 0.1) {
					eat = true;
					applePos = null;
					break;
				}
		}
		for (Vector seg : snake) {
			int tot = 0;
			for (Vector seg2 : snake)
				if (seg.getLengthTo(seg2) == 0)
					tot++;
			if (tiles[(int) seg.getX()][(int) seg.getY()] > 1)
				tot++;
			if (tot >= 2 && snake.size() > 2)
				dead = true;
		}
		snake.set(snake.size() - 1, trans(head));
		if (applePos == null && apple)
			placeApple();
		if (!eat)
			snake.remove(0); // Remove tail
	}

	void ai() {
		snakePop();
		if (applePos != null) {
			for (int i = 0; i < astar.length; i++)
				astar[i] = new int[BOARD_HEIGHT];
			astar[(int) applePos.getX()][(int) applePos.getY()] = BOARD_WIDTH
					* BOARD_HEIGHT;
			while (true) {
				boolean change = false;
				int[][] newAstar = astar.clone();
				for (int x = 0; x < BOARD_WIDTH; x++) {
					for (int y = 0; y < BOARD_HEIGHT; y++) {
						if (tiles[x][y] != 0)
							continue;
						if (astar[x][y] != 0) {
							for (int i = 0; i < 8; i += 2) {
								Vector q = backtrans(new Vector(x, y)
										.moveInDir(i));
								int rx = (int) q.getX();
								int ry = (int) q.getY();
								if (rx < 0 || rx > BOARD_WIDTH || ry < 0
										|| ry > BOARD_HEIGHT)
									continue;
								try {
									if (astar[rx][ry] != 0)
										continue;
									for (Vector v : snake) {
										if (v.getX() == rx && v.getY() == ry)
											continue;
									}
									if (q.getLengthTo(snake.get(snake.size() - 1)) < 0.1) {
										for (int j = 0; j < 8; j++) {
											if (q.moveInDir(j)
													.getLengthTo(x, y) < 0.1) {
												boolean b = false;
												for (Vector v : snake) {
													if (v.equals(backtrans(q
															.moveInDir(j))))
														b = true;
												}
												if (b)
													continue;
												move(j);
												return;
											}
										}
									}
									change = true;
									int sub = 1;
									for (int j = 0; j < 8; j++) {
										Vector v = q.moveInDir(j);
										if (tiles[(int) v.getX()][(int) v
												.getY()] == 0)
											sub++;
									}
									newAstar[rx][ry] = astar[x][y] - sub;
								} catch (IndexOutOfBoundsException e) {
								}
							}
						}
					}

				}
				astar = newAstar;

				if (!change)
					break;
			}
		}
		snakePop();
		int best = 0;
		int score = 0;
		for (int i = 0; i < 8; i += 2) {
			Snake s = new Snake();
			s.applePos = applePos;
			s.dead = dead;
			s.snake = (CopyOnWriteArrayList<Vector>) snake.clone();
			for (int j = 0; j < tiles.length; j++) {
				s.tiles[j] = tiles[j].clone();
			}
			s.snakePop();
			try {
				s.move(i);
			} catch (ArrayIndexOutOfBoundsException e) {
				continue;
			}
			if (s.dead)
				continue;
			try {
				s.tiles[(int) s.snake.get(s.snake.size() - 1).getX()][(int) s.snake
						.get(s.snake.size() - 1).getY()] = 3;
			} catch (ArrayIndexOutOfBoundsException e) {
				continue;
			}
			int size = 1;
			int k = 0;
			while (k < 100 && size < 40) {
				for (int x = 0; x < s.BOARD_WIDTH; x++) {
					for (int y = 0; y < s.BOARD_HEIGHT; y++) {
						if (s.tiles[x][y] == 3) {
							for (int d = 0; d < 8; d += 2) {
								Vector np = new Vector(x, y).moveInDir(d);
								try {
									if (s.tiles[(int) np.getX()][(int) np
											.getY()] == 0) {
										s.tiles[(int) np.getX()][(int) np
												.getY()] = 3;
										k = 0;
										size++;
									}
								} catch (ArrayIndexOutOfBoundsException e) {
								}
							}
						}
					}
				}
				k++;
			}
			if (size > score) {
				score = size;
				best = i;
			}
		}
		move(best);
	}

	private int deep(Vector pos) {
		int a = 5;
		int best = 0;
		int score = 0;
		for (int i = 0; i < Math.pow(4, a); i++) {
			String b = Integer.toString(i, 4);
			b = b + rep(" ", a - b.length());
			int[] l = new int[a];
			for (int j = 0; j < a; j++)
				l[j] = b.charAt(j) - '0';
			try {
				Snake s = (Snake) clone();
				int f = 0;
				for (int j = 0; j < l.length; j++) {
					s.move(l[j]);
					if (s.dead) {
						f = j;
						break;
					}
				}

				if (score < f) {
					best = l[0];
					score = f;
				}
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}

		}
		return best;
	}

	public String toString() {
		String a = rep("+-", BOARD_WIDTH) + "+";
		String ret = a;
		snakePop();
		for (int y = 0; y < BOARD_HEIGHT; y++) {
			ret += "\n|";
			for (int x = 0; x < BOARD_WIDTH; x++) {
				char c = ' ';
				switch (tiles[x][y]) {
				case 1:
					c = (char) ('0' + (snake.lastIndexOf(new Vector(x, y)) % 10));
					break;
				case 2:
					c = '#';
					break;
				}
				if (applePos != null)
					if (applePos.getX() == x && applePos.getY() == y)
						c = '';
				ret += c + "|";
			}
			ret += "\n" + a;
		}
		return ret;
	}

	public static String rep(String text, int length) { // Repeat string
		return new String(new char[length]).replace("\0", text);
	}

	public static void test(String[] args) {
		Snake s = new Snake();
		Scanner c = new Scanner(System.in);
		while (true) {
			clear();
			System.out.println(s);
			System.out.println(s.dead);
			s.ai();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			/*
			 * System.out.print("Move (0=up,1=right,2=down,3=left):"); int a =
			 * 0; String i = c.nextLine(); switch (i.toLowerCase()) { case "d":
			 * a = 1; break; case "s": a = 2; break; case "a": a = 3; break; }
			 * s.move(a);
			 */
		}
	}

	private static void clear() {
		System.out.println(rep("\n", 100));
	}
}
