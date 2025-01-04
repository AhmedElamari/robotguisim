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
		// Example initial robot
		items.add(new Robot(xS / 2, yS / 2, 10, 45, 2, this));
		items.add(new Obstacle(xS / 3, yS / 3, 10));
	}

	public double getXSize() {
		return xMax;
	}

	public double getYSize() {
		return yMax;
	}

	public void drawArena(MyCanvas mc) {
		for (ArenaItem i : items) {
			i.drawItem(mc);
		}
	}

	public void checkItems() {
		for (ArenaItem i : items) {
			i.checkItem(this);
		}
	}

	public void adjustItems() {
		for (ArenaItem i : items) {
			i.adjustItem();
		}
	}

	public void setRobots(double x, double y) {
		for (ArenaItem i : items) {
			if (i instanceof Robot) {
				i.setXY(x, y);
			}
		}
	}

	public ArrayList<String> describeAll() {
		ArrayList<String> all = new ArrayList<>();
		for (ArenaItem i : items) {
			all.add(i.toString());
		}
		return all;
	}

	public boolean checkRobot(double x, double y, double rad, int notID) {
		boolean ans = true;
		for (ArenaItem i : items) {
			if (i instanceof Robot && i.getID() != notID && i.hitting(x, y, rad)) {
				ans = false;
			}
		}
		return ans;
	}

	public void addRobot() {
		Robot newRobot = new Robot(xMax / 2, yMax / 2, 10, 45, 2, this);
		items.add(newRobot);
	}

	/**
	 * Modified to randomize the angle of the robot's bounce when colliding with a
	 * wall or another item.
	 */
	/**
	 * Attempts to compute a new direction angle when a robot collides with a wall
	 * or another robot. Tries multiple randomized offsets to avoid getting stuck
	 * near the walls.
	 */
	public double CheckRobotAngle(double x, double y, double rad, double ang, int notID) {
		double ans = ang;
		double randomOffset;
		int maxAttempts = 5;
		boolean collisionDetected = false;
		if (x - rad < 0 || x + rad > xMax || y - rad < 0 || y + rad > yMax) {
			collisionDetected = true;
		}
		for (ArenaItem i : items) {
			if (i instanceof Robot && i.getID() != notID) {
				Robot ro = (Robot) i;
				double dist = Math.sqrt(Math.pow(ro.getX() - x, 2) + Math.pow(ro.getY() - y, 2));
				if (dist < ro.getRad() + rad) {
					collisionDetected = true;
					break;
				}
			}
			if (i instanceof Obstacle) {
				double ox = i.getX();
				double oy = i.getY();
				double od = Math.sqrt(Math.pow(ox - x, 2) + Math.pow(oy - y, 2));
				if (od < i.getRad() + rad) {
					collisionDetected = true;
					break;
				}
			}
		}
		if (!collisionDetected) {
			return ans;
		}
		for (int attempt = 0; attempt < maxAttempts; attempt++) {
			randomOffset = (Math.random() * 90) - 45;
			double candidateAngle = (ang + 180 + randomOffset) % 360;
			double radAngle = Math.toRadians(candidateAngle);
			double step = 2.0;
			double newX = x + step * Math.cos(radAngle);
			double newY = y + step * Math.sin(radAngle);
			if (newX - rad >= 0 && newX + rad <= xMax && newY - rad >= 0 && newY + rad <= yMax) {
				ans = candidateAngle;
				break;
			}
		}
		return ans;
	}

	public void setRobot(double x, double y) {
		for (ArenaItem i : items) {
			if (i instanceof Robot) {
				i.setXY(x, y);
			}
		}
	}

	public void addObstacle() {
		double x = Math.random() * xMax;
		double y = Math.random() * yMax;
		Obstacle newObstacle = new Obstacle(x, y, 10);
		items.add(newObstacle); // Ensure obstacle is added to the items ArrayList
	}

	public boolean canMoveHere(double x, double y, double rad) {
		for (ArenaItem i : items) {
			if (i.hitting(x, y, rad)) {
				return false;
			}
		}
		return true;
	}
}