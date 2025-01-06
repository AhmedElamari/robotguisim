package RobotSimulation;

/**
 * Example Whisker class that detects both items and walls using the Line class.
 */
public class Whisker extends Robot {
	private Line leftWhisker;
	private Line rightWhisker;
	private double whiskerLength;

	public Whisker(double ix, double iy, double ir, double ia, double is, RobotArena arena) {
		super(ix, iy, ir, ia, is, arena);
		col = 'g';
		// Example whisker length: can be adjusted as needed
		whiskerLength = ir * 2;
		updateWhiskers();
	}

	/**
	 * Recalculate the positions of the whiskers based on current robot angle and
	 * position.
	 */
	private void updateWhiskers() {
		double leftAngle = rAngle - 22.5;
		double rightAngle = rAngle + 22.5;

		double leftX = calcX(rad + whiskerLength, leftAngle);
		double leftY = calcY(rad + whiskerLength, leftAngle);
		double rightX = calcX(rad + whiskerLength, rightAngle);
		double rightY = calcY(rad + whiskerLength, rightAngle);

		leftWhisker = new Line(x, y, leftX, leftY);
		rightWhisker = new Line(x, y, rightX, rightY);
	}

	/**
	 * Draws the robot and its whiskers on the canvas.
	 */
	@Override
	public void drawItem(MyCanvas mc) {
		super.drawItem(mc); // Draw the robot itself

		updateWhiskers(); // Ensure whisker positions are up-to-date
		leftWhisker.drawLine(mc);
		rightWhisker.drawLine(mc);
	}

	/**
	 * Called once per update cycle. Checks sensor (whisker) collisions with items
	 * and walls.
	 */
	@Override
	public void adjustItem() {
		super.adjustItem();
		updateWhiskers();
		checkWhiskers();
	}

	/**
	 * Check whiskers against both walls and items (obstacles, lights, etc.).
	 */
	private void checkWhiskers() {
		boolean leftDetect = false;
		boolean rightDetect = false;

		// 1) Check for wall intersections by constructing lines for each arena boundary
		double arenaWidth = arena.getXSize();
		double arenaHeight = arena.getYSize();

		// Top boundary (0,0 to arenaWidth,0)
		Line topBoundary = new Line(0, 0, arenaWidth, 0);
		// Bottom boundary (0,arenaHeight to arenaWidth,arenaHeight)
		Line bottomBoundary = new Line(0, arenaHeight, arenaWidth, arenaHeight);
		// Left boundary (0,0 to 0,arenaHeight)
		Line leftBoundary = new Line(0, 0, 0, arenaHeight);
		// Right boundary (arenaWidth,0 to arenaWidth,arenaHeight)
		Line rightBoundary = new Line(arenaWidth, 0, arenaWidth, arenaHeight);

		// Check if either whisker intersects any boundary line
		if (intersectsBoundary(leftWhisker, topBoundary, bottomBoundary, leftBoundary, rightBoundary)) {
			leftDetect = true;
		}
		if (intersectsBoundary(rightWhisker, topBoundary, bottomBoundary, leftBoundary, rightBoundary)) {
			rightDetect = true;
		}

		// 2) Check collisions with items in the arena (lights, obstacles, other robots,
		// etc.)
		for (ArenaItem item : arena.items) {
			if (item != this) {
				// If detecting a light, the robot may move toward it
				if (item instanceof Light) {
					Light light = (Light) item;
					if (leftWhisker.distanceFrom(light.getX(), light.getY()) < light.getRad()) {
						moveTowardsLight(light);
						leftDetect = true;
					}
					if (rightWhisker.distanceFrom(light.getX(), light.getY()) < light.getRad()) {
						moveTowardsLight(light);
						rightDetect = true;
					}
				}
				// For other items, just detect collision
				else {
					if (leftWhisker.distanceFrom(item.getX(), item.getY()) < item.getRad()) {
						leftDetect = true;
					}
					if (rightWhisker.distanceFrom(item.getX(), item.getY()) < item.getRad()) {
						rightDetect = true;
					}
				}
			}
		}

		// 3) Respond to any detected collisions
		if (leftDetect && rightDetect) {
			bounceOff();
		} else if (leftDetect) {
			avoidObstacle();
		} else if (rightDetect) {
			avoidObstacle();
		}
	}

	/**
	 * A helper method to check intersection of a whisker line with any arena
	 * boundary line.
	 */
	private boolean intersectsBoundary(Line whisker, Line top, Line bottom, Line left, Line right) {
		// Using findintersection(...) from the Line class
		// If the whisker line intersects any boundary, return true
		if (whisker.findintersection(top))
			return true;
		if (whisker.findintersection(bottom))
			return true;
		if (whisker.findintersection(left))
			return true;
		if (whisker.findintersection(right))
			return true;
		return false;
	}

	/**
	 * Rotate the robot to face a Light item.
	 */
	private void moveTowardsLight(Light light) {
		double angleToLight = Math.toDegrees(Math.atan2(light.getY() - y, light.getX() - x));
		rAngle = angleToLight;
	}

	/**
	 * Simple avoidance maneuver when a single whisker detects an obstacle or wall.
	 */
	private void avoidObstacle() {
		// Randomize bounce to help avoid repeated collisions
		rAngle = (rAngle + 180 + (Math.random() * 60 - 30)) % 360;
	}

	/**
	 * A more direct bounce-off when both whiskers detect obstacles or walls
	 * simultaneously.
	 */
	private void bounceOff() {
		rAngle = (rAngle + 180) % 360;
	}

	/**
	 * Calculate the X coordinate of a point at distance s and angle deg from (x,
	 * y).
	 */
	@Override
	public double calcX(double s, double deg) {
		return x + s * Math.cos(Math.toRadians(deg));
	}

	/**
	 * Calculate the Y coordinate of a point at distance s and angle deg from (x,
	 * y).
	 */
	@Override
	public double calcY(double s, double deg) {
		return y + s * Math.sin(Math.toRadians(deg));
	}
}