package RobotSimulation;

/**
 * Whisker class - An advanced robot simulation with whisker-based obstacle
 * detection
 *
 * This class implements a robot that uses whisker sensors to detect and respond
 * to obstacles, walls, and light sources in its environment. Just like a real
 * robot's whiskers (or a cat's whiskers), these virtual sensors help navigate
 * the space safely.
 *
 * Features: - Dual whisker system for left/right detection - Smart obstacle
 * avoidance with randomized responses - Light-seeking behavior (like a moths,
 * but hopefully smarter!) - Temporary speed boosts for escaping tight spots
 *
 * @author Ahmed Elamari
 * @version 2.0
 */
public class Whisker extends Robot {
	// The whiskers are represented as Line objects for collision detection
	private Line leftWhisker;
	private Line rightWhisker;
	private double whiskerLength;

	// Speed control variables for temporary boosts
	private double originalSpeed; // Remember initial speed
	private boolean isSpeedBoosted; // Keep track if we are in "panic-mode" speed
	private int speedResetCounter; // Count updates until speed returns to normal

	// Constants - tweak these for different robot "personalities"
	private static final int SPEED_RESET_DELAY = 20; // How long to maintain boosted speed
	private static final double SPEED_BOOST_FACTOR = 1.5; // How much faster when escaping
	private static final double WHISKER_ANGLE = 25; // Angle between whiskers (in degrees)

	/**
	 * Creates a new Whisker robot for exploring the arena with whisker sensors.
	 *
	 * @param ix    Initial x position
	 * @param iy    Initial y position
	 * @param ir    Robot's radius
	 * @param ia    Initial angle (in degrees)
	 * @param is    Initial speed
	 * @param arena The arena where the robot will roam
	 */
	public Whisker(double ix, double iy, double ir, double ia, double is, RobotArena arena) {
		super(ix, iy, ir, ia, is, arena);
		col = 'g'; // Robot color
		whiskerLength = ir * 2; // Whiskers twice as long as robot radius
		originalSpeed = is;
		isSpeedBoosted = false;
		speedResetCounter = 0;
		updateWhiskers();
	}

	/**
	 * Updates the whisker positions based on the robot's current position and
	 * angle. Whiskers are used for collision detection against walls and items.
	 */
	private void updateWhiskers() {
		// Calculate whisker angles relative to the robot's heading
		double leftAngle = rAngle - WHISKER_ANGLE;
		double rightAngle = rAngle + WHISKER_ANGLE;

		// Calculate whisker endpoints
		double leftX = calcX(rad + whiskerLength, leftAngle);
		double leftY = calcY(rad + whiskerLength, leftAngle);
		double rightX = calcX(rad + whiskerLength, rightAngle);
		double rightY = calcY(rad + whiskerLength, rightAngle);

		// Create whisker lines for collision checks
		leftWhisker = new Line(x, y, leftX, leftY);
		rightWhisker = new Line(x, y, rightX, rightY);
	}

	/**
	 * Draws the robot and its whiskers on the canvas.
	 *
	 * @param mc The canvas to draw on
	 */
	@Override
	public void drawItem(MyCanvas mc) {
		super.drawItem(mc); // Draw the robot body first
		updateWhiskers(); // Update whiskers before drawing
		leftWhisker.drawLine(mc);
		rightWhisker.drawLine(mc);
	}

	/**
	 * Main update method called every cycle to adjust the robot state: - Updates
	 * whisker positions - Checks collisions - Manages temporary speed boosts
	 */
	@Override
	public void adjustItem() {
		super.adjustItem();
		updateWhiskers();
		checkWhiskers(); // Check collision using whiskers

		// Handle temporary speed boost timing
		if (isSpeedBoosted) {
			speedResetCounter++;
			if (speedResetCounter >= SPEED_RESET_DELAY) {
				resetSpeed();
			}
		}
	}

	/**
	 * Checks what the whiskers are detecting (walls or items) and updates movement.
	 * This method now integrates the specialized collision checks for better
	 * organization.
	 */
	private void checkWhiskers() {
		// Start without detection
		boolean leftDetect = false;
		boolean rightDetect = false;

		// Use dedicated wall collision check and merge results
		boolean[] wallDetections = checkWallCollisions();
		leftDetect = leftDetect || wallDetections[0];
		rightDetect = rightDetect || wallDetections[1];

		// Use item collision checks
		double itemProximityThreshold = rad * 1.3;
		for (ArenaItem item : arena.items) {
			if (item != this) {
				boolean[] itemDetections = checkItemCollision(item, itemProximityThreshold);
				leftDetect = leftDetect || itemDetections[0];
				rightDetect = rightDetect || itemDetections[1];
			}
		}

		// Final movement adjustment based on detection
		if (leftDetect && rightDetect) {
			reverse(); // Both whiskers detect => reverse
		} else if (leftDetect) {
			turnRight(); // Left whisker detect => turn right
		} else if (rightDetect) {
			turnLeft(); // Right whisker detect => turn left
		}
	}

	/**
	 * Checks for collisions with arena walls using whiskers.
	 *
	 * @return A boolean array [leftDetected, rightDetected]
	 */
	private boolean[] checkWallCollisions() {
		boolean leftDetected = false;
		boolean rightDetected = false;

		double arenaWidth = arena.getXSize();
		double arenaHeight = arena.getYSize();

		// Create boundary lines to represent arena walls
		Line topBoundary = new Line(0, 0, arenaWidth, 0);
		Line bottomBoundary = new Line(0, arenaHeight, arenaWidth, arenaHeight);
		Line leftBoundary = new Line(0, 0, 0, arenaHeight);
		Line rightBoundary = new Line(arenaWidth, 0, arenaWidth, arenaHeight);

		// Check whiskers against each boundary
		if (intersectsBoundary(leftWhisker, topBoundary, bottomBoundary, leftBoundary, rightBoundary)) {
			leftDetected = true;
		}
		if (intersectsBoundary(rightWhisker, topBoundary, bottomBoundary, leftBoundary, rightBoundary)) {
			rightDetected = true;
		}

		return new boolean[] { leftDetected, rightDetected };
	}

	/**
	 * Checks collision with an item in the arena. Different items may prompt
	 * different behaviors (e.g. light-seeking vs. obstacle avoidance).
	 *
	 * @param item                   The item to check collision against
	 * @param itemProximityThreshold Amount of proximity allowed before detection
	 * @return A boolean array [leftDetected, rightDetected]
	 */
	private boolean[] checkItemCollision(ArenaItem item, double itemProximityThreshold) {
		boolean leftDetected = false;
		boolean rightDetected = false;

		double distanceToItem = Math.sqrt(Math.pow(item.getX() - x, 2) + Math.pow(item.getY() - y, 2));

		// If it's a Light, use specialized logic
		if (item instanceof Light) {
			handleLightDetection((Light) item, distanceToItem, itemProximityThreshold);
			// Also check if we're just "too close"
			if (leftWhisker.distanceFrom(item.getX(), item.getY()) < ((Light) item).getRad() + itemProximityThreshold) {
				leftDetected = true;
			}
			if (rightWhisker.distanceFrom(item.getX(), item.getY()) < ((Light) item).getRad()
					+ itemProximityThreshold) {
				rightDetected = true;
			}
		} else {
			// General obstacle detection
			double itemRadius = item.getRad();
			double leftDistance = leftWhisker.distanceFrom(item.getX(), item.getY());
			double rightDistance = rightWhisker.distanceFrom(item.getX(), item.getY());

			// Check if either whisker is close to this item
			if (leftDistance < itemRadius + itemProximityThreshold) {
				leftDetected = true;
				// If very close, add extra turn angle for avoidance
				if (leftDistance < itemRadius + (itemProximityThreshold * 0.5)) {
					rAngle += 15;
				}
			}
			if (rightDistance < itemRadius + itemProximityThreshold) {
				rightDetected = true;
				// If very close, add extra turn angle for avoidance
				if (rightDistance < itemRadius + (itemProximityThreshold * 0.5)) {
					rAngle -= 15;
				}
			}
		}
		return new boolean[] { leftDetected, rightDetected };
	}

	/**
	 * Special handling for light sources. If far enough, move toward the light;
	 * otherwise, consider it a detection for avoidance or close interaction.
	 *
	 * @param light              The Light object detected
	 * @param distanceToItem     Distance from the robot to the light
	 * @param proximityThreshold Threshold at which the robot decides it's too close
	 */
	private void handleLightDetection(Light light, double distanceToItem, double proximityThreshold) {
		if (distanceToItem > proximityThreshold) {
			moveTowardsLight(light); // Move closer if not too close
		}
		// If within the proximity threshold, the whisker-robot will handle it as a
		// collision
	}

	/**
	 * Moves the robot's angle to point toward a given light source.
	 *
	 * @param light The Light object to move towards
	 */
	private void moveTowardsLight(Light light) {
		double angleToLight = Math.toDegrees(Math.atan2(light.getY() - y, light.getX() - x));
		rAngle = angleToLight;
	}

	/**
	 * Emergency maneuver - back up and apply a temporary speed boost to escape.
	 * Adds randomness to reduce the chance of being stuck in loops.
	 */
	private void reverse() {
		// Reverse angle with a small random offset
		rAngle = (rAngle + 180 + (Math.random() * 40 - 20)) % 360;

		if (!isSpeedBoosted) {
			originalSpeed = rSpeed;
			rSpeed *= SPEED_BOOST_FACTOR;
			isSpeedBoosted = true;
			speedResetCounter = 0;
		}
	}

	/**
	 * Resets the robot's speed back to its original cruising speed.
	 */
	private void resetSpeed() {
		rSpeed = originalSpeed;
		isSpeedBoosted = false;
		speedResetCounter = 0;
	}

	/**
	 * Turns the robot left with a touch of randomness for less predictable
	 * movement.
	 */
	private void turnLeft() {
		rAngle = (rAngle - 90 + (Math.random() * 20 - 10)) % 360;
	}

	/**
	 * Turns the robot right with a touch of randomness for less predictable
	 * movement.
	 */
	private void turnRight() {
		rAngle = (rAngle + 90 + (Math.random() * 20 - 10)) % 360;
	}

	/**
	 * Calculates X coordinate given distance and angle relative to the robot.
	 *
	 * @param s   Distance to travel along the angle
	 * @param deg Angle in degrees
	 * @return The resulting X coordinate
	 */
	@Override
	public double calcX(double s, double deg) {
		return x + s * Math.cos(Math.toRadians(deg));
	}

	/**
	 * Calculates Y coordinate given distance and angle relative to the robot.
	 *
	 * @param s   Distance to travel along the angle
	 * @param deg Angle in degrees
	 * @return The resulting Y coordinate
	 */
	@Override
	public double calcY(double s, double deg) {
		return y + s * Math.sin(Math.toRadians(deg));
	}

	/**
	 * Checks if a whisker line intersects any boundary line.
	 *
	 * @param whisker Whisker line
	 * @param top     Top boundary line
	 * @param bottom  Bottom boundary line
	 * @param left    Left boundary line
	 * @param right   Right boundary line
	 * @return True if the whisker intersects any boundary
	 */
	private boolean intersectsBoundary(Line whisker, Line top, Line bottom, Line left, Line right) {
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
}