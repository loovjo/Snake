package com.loovjo.snake;

import java.util.ArrayList;

public class Vector {
	private float x, y;

	public Vector(float x, float y) {
		this.setX(x);
		this.setY(y);
	}

	public Vector(int x, int y) {
		this.setX(x);
		this.setY(y);
	}

	public Vector(double x, double y) {
		this.setX((float) x);
		this.setY((float) y);
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getLengthTo(float x, float y) {
		return (float) Math.sqrt(Math.pow(this.x - x, 2)
				+ Math.pow(this.y - y, 2));
	}

	public float getLengthTo(Vector v) {
		return getLengthTo(v.x, v.y);
	}

	public float getLength() {
		return getLengthTo(0, 0);
	}

	public Vector setLength(float setLength) {
		float length = getLength();
		x *= setLength / length;
		y *= setLength / length;
		return this;
	}

	public Vector sub(Vector v) {
		return new Vector(this.x - v.getX(), this.y - v.getY());
	}

	public Vector add(Vector v) {
		return new Vector(this.x + v.getX(), this.y + v.getY());
	}

	public Vector mul(Vector v) {
		return new Vector(this.x * v.getX(), this.y * v.getY());
	}

	public Vector mul(float f) {
		return mul(new Vector(f, f));
	}

	public Vector div(Vector v) {
		return new Vector(this.x / v.getX(), this.y / v.getY());
	}

	public Vector div(float f) {
		return div(new Vector(f, f));
	}

	public Vector mod(Vector v) {
		return new Vector(this.x % v.getX(), this.y % v.getY());
	}

	public Vector mod(int a, int b) {
		return mod(new Vector(a, b));
	}

	public Vector mod(int a) {
		return mod(a, a);
	}

	public String toString() {
		return "Vector(" + getX() + ", " + getY() + ")";
	}

	public ArrayList<Vector> loop(Vector to, float length) {
		Vector delta = this.sub(to);
		float l = delta.getLength();
		ArrayList<Vector> loops = new ArrayList<Vector>();
		for (float i = length; i < l; i += length) {
			delta.setLength(i);
			loops.add(delta.add(to));
		}
		loops.add(this);

		return loops;
	}

	public boolean intersects(Vector pos, Vector size) {
		pos.sub(this);
		if (pos.getX() < getX())
			return false;
		/*
		 * if (pos.getX() + size.getX() > getX()) return false;
		 */if (pos.getY() < getY())
			return false;/*
						 * if (pos.getY() + size.getY() > getY()) return false;
						 */
		return true;
	}

	public Vector copy() {
		return new Vector(x, y);
	}

	public void distort(float d) {
		x += Math.random() * d - d / 2;
		y += Math.random() * d - d / 2;
	}
	public boolean equals(Object o) {
		try {
			return ((Vector)o).x == x && ((Vector)o).y == y;
		}
		catch (Exception e) {
		}
		return false;
	}

	public boolean kindaEquals(Vector o, int i) {
		if (o.x + i < x)
			return false;
		if (o.x - i > x)
			return false;
		if (o.y + i < y)
			return false;
		if (o.y - i > y)
			return false;
		return true;
	}

	public int getDirection() {
		return (getDirectionInDegrees()) / (360 / 8);
	}

	public int getDirectionInDegrees() {
		return (int) positize((float) Math.toDegrees(Math.atan2(x, -y)), 360f);
	}

	private float positize(float f, float base) {
		while (f < 0)
			f += base;
		return f;
	}

	public Vector moveInDir(int d) {
		// 0 = north,
		// 1 = northeast,
		// 2 = east,
		// 3 = southeast,
		// 4 = south,
		// 5 = southwest,
		// 6 = west,
		// 7 = northwest
		if (d == 0)
			return this.add(new Vector(0, -1));
		if (d == 1)
			return this.add(new Vector(1, -1));
		if (d == 2)
			return this.add(new Vector(1, 0));
		if (d == 3)
			return this.add(new Vector(1, 1));
		if (d == 4)
			return this.add(new Vector(0, 1));
		if (d == 5)
			return this.add(new Vector(-1, 1));
		if (d == 6)
			return this.add(new Vector(-1, 0));
		if (d == 7)
			return this.add(new Vector(-1, -1));
		return this;
	}

	public boolean isPositive() {
		return x >= 0 && y >= 0;
	}

	public Vector clone() {
		return new Vector(x, y);
	}
}