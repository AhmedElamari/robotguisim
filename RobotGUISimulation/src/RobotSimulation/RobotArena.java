package RobotSimulation;

import java.util.ArrayList;

public class RobotArena {
	double xMax, yMax;
	protected ArrayList<ArenaItem> items;
	protected ArrayList<Obstacle> obstacles;
	protected ArrayList<Robot> robots;

	RobotArena() {
		this(500, 400);
	}

	RobotArena(double xS, double yS) {
		xMax = xS;
		yMax = yS;
		items = new ArrayList<ArenaItem>();
		items.add(new Robot(xS / 2, yS / 2, 10, 45, 2, this));
	}

	/**
	 * Get the x size of the arena
	 * 
	 * @return x size
	 */
	public double getXSize() {
		return xMax;
	}

	/**
	 * Get the y size of the arena
	 * 
	 * @return y size
	 */
	public double getYSize() {
		return yMax;
	}

	/**
	 * Draw the arena
	 * 
	 * @param mc
	 */
	public void drawArena(MyCanvas mc) {
		for (ArenaItem i : items)
			i.drawItem(mc);
	}

	public void checkItems() {
		for (ArenaItem i : items)
			i.checkItem(this);
	}

	public void adjustItems() {
		for (ArenaItem i : items)
			i.adjustItem();
	}

	public void setRobots(double x, double y) {
		for (ArenaItem i : items)
			if (i instanceof Robot)
				i.setXY(x, y);

	}

	public ArrayList<String> describeAll() {
		ArrayList<String> all = new ArrayList<String>();
		for (ArenaItem i : items)
			all.add(i.toString());
		return all;
	}

	public void addRobot() {
		Robot newRobot = new Robot(xMax / 2, yMax / 2, 10, 5, 5, this);
		items.add(newRobot);
	}

	public double CheckRobotAngle(double x, double y, double rad, double ang, int notID) {
		double ans = ang;

		// Check for wall collisions
		if (x - rad < 0 || x + rad > xMax || y - rad < 0 || y + rad > yMax) {
			ans = (ang + 180) % 360;
		}

		// Check for collisions with other items
		for (ArenaItem i : items) {
			if (i.getID() != notID && i.hitting(x, y, rad)) {
				double dx = x - i.getX();
				double dy = y - i.getY();
				double distanceSquared = dx * dx + dy * dy;
				double radiusSum = rad + i.getRad();
				if (distanceSquared < radiusSum * radiusSum) {
					ans = 180 * Math.atan2(dy, dx) / Math.PI;
				}
			}
		}

		// Only change direction if a collision is detected
		if (ans != ang) {
			return ans;
		}

		return ang;
	}

	public boolean checkHit(ArenaItem target) {
		boolean ans = false;
		for (ArenaItem i : items)
			if (i instanceof Robot && i.hitting(target))
				ans = true;
		return ans;

	}

	public void setRobot(double x, double y) {
		for (ArenaItem i : items)
			if (i instanceof Robot)
				i.setXY(x, y);
	}

	public void addObstacle() {
		items.add(obstacles.addObstacle);
	}
}
