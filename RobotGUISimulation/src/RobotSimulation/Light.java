package RobotSimulation;

/**
 * The <code>Light</code> class is a subclass of <code>ArenaItem</code> that
 * represents a stationary light source in the simulation arena.
 *
 * <p>
 * This light can influence nearby robots by causing them to either move toward
 * it (when they are far away) or move away from it (when they are too close).
 * Additionally, the logic ensures that only the closest light (or a randomly
 * chosen one among ties) affects a given robot at any time.
 * </p>
 *
 * <p>
 * This demonstrates more advanced interaction between arena items, particularly
 * how robots respond to environmental stimuli such as lights.
 * </p>
 *
 * @author Ahmed Elamari
 * @version 1.0
 *
 * @see ArenaItem
 */
public class Light extends ArenaItem {

	/**
	 * Constructs a <code>Light</code> object at the specified coordinates with the
	 * given radius.
	 *
	 * @param d the X coordinate of the light
	 * @param e the Y coordinate of the light
	 * @param f the radius of the light
	 */
	public Light(double d, double e, double f) {
		super(d, e, f);
		col = 'y'; // Use 'y' (yellow) to visually represent this light
	}

	/**
	 * Draws this light on the specified <code>MyCanvas</code>.
	 *
	 * <p>
	 * By default, the light is represented as a circle with a yellow colour
	 * (character 'y').
	 * </p>
	 *
	 * @param mc the <code>MyCanvas</code> object used for drawing shapes
	 */
	@Override
	public void drawItem(MyCanvas mc) {
		mc.showCircle(x, y, rad, col);
	}

	/**
	 * Checks interactions between this light and all robots in the
	 * <code>RobotArena</code>.
	 *
	 * <p>
	 * The robots may move toward this light if they are beyond
	 * <code>approachRange</code>, or move away if they are within
	 * <code>avoidRange</code>. Only the closest light (or one among multiple lights
	 * that are equally close) will attract or repel a robot at a time, to avoid
	 * conflicting movements.
	 * </p>
	 *
	 * <ul>
	 * <li>If a robot is physically overlapping with the light (distance less than
	 * the sum of radii), collision logic from <code>RobotArena</code> is invoked to
	 * adjust the robot's angle.</li>
	 * <li>If a tie occurs between multiple lights, a small random tie offset is
	 * added to the robot's angle, preventing it from getting stuck.</li>
	 * </ul>
	 *
	 * @param r the <code>RobotArena</code> containing all arena items
	 */
	@Override
	public void checkItem(RobotArena r) {
		// Define approach and avoidance thresholds
		double approachRange = 150;
		double avoidRange = 30;

		// Loop through each item in the arena
		for (ArenaItem item : r.items) {
			if (item instanceof Robot) {
				Robot rob = (Robot) item;

				// Calculate distance between this light and the robot
				double distX = rob.getX() - x;
				double distY = rob.getY() - y;
				double distance = Math.sqrt(distX * distX + distY * distY);

				// 1) Find the closest light distance to this robot
				double myDistance = distance;
				double closestDistance = myDistance; // Assume this light is closest initially
				int tieCount = 1;

				// Compare distance to other lights
				for (ArenaItem other : r.items) {
					if (other instanceof Light && other != this) {
						double ox = other.getX();
						double oy = other.getY();
						double d = Math.sqrt(Math.pow(ox - rob.getX(), 2) + Math.pow(oy - rob.getY(), 2));

						// Update the closest light distance and tie count
						if (d < closestDistance) {
							closestDistance = d;
							tieCount = 1;
						} else if (Math.abs(d - closestDistance) < 1e-5) {
							tieCount++;
						}
					}
				}

				// 2) If this light is not the single closest (within a tiny epsilon), skip
				if (Math.abs(myDistance - closestDistance) > 1e-5) {
					continue;
				}

				// 3) If there is a tie, add a small random angle offset to break it
				double tieOffset = 0;
				if (tieCount > 1) {
					tieOffset = (Math.random() * 30) - 15; // e.g., a random ±15° offset
				}

				// 4) Apply approach/avoid logic
				if (distance > approachRange) {
					// Robot is far -> move towards the light
					double angleToLight = Math.toDegrees(Math.atan2(y - rob.getY(), x - rob.getX()));
					rob.setAngle((angleToLight + tieOffset) % 360);
				} else if (distance < avoidRange) {
					// Robot is too close -> move away from the light
					double angleAway = Math.toDegrees(Math.atan2(rob.getY() - y, rob.getX() - x));
					rob.setAngle((angleAway + tieOffset) % 360);
				}

				// 5) If physically overlapping, adjust the robot's angle
				if (distance < (rob.getRad() + rad)) {
					double newAngle = r.CheckRobotAngle(rob.getX(), rob.getY(), rob.getRad(), rob.getAngle(),
							rob.getID());
					rob.setAngle(newAngle);
				}
			}
		}
	}

	/**
	 * Adjusts this light's state each simulation tick if necessary.
	 *
	 * <p>
	 * By default, a stationary light does nothing on each update. Extend or
	 * override for dynamic or moving lights.
	 * </p>
	 */
	@Override
	public void adjustItem() {
		// Currently, the light remains stationary
	}

	/**
	 * Returns a human-readable description of this light, including coordinates and
	 * radius formatted to limited decimal places.
	 *
	 * @return a <code>String</code> describing this light’s position and size
	 */
	@Override
	public String toString() {
		// x and y to 2 decimals, radius to 1 decimal
		return "Light at " + String.format("%.2f", x) + ", " + String.format("%.2f", y) + " with radius "
				+ String.format("%.1f", rad);
	}

	/**
	 * Returns a formatted string for file output, representing this light's key
	 * properties.
	 *
	 * <p>
	 * Format example: <code>Light 100.0 200.0 10.0 y</code>
	 * </p>
	 *
	 * @return a <code>String</code> containing the light’s parameters
	 */
	@Override
	public String fileString() {
		return "Light " + x + " " + y + " " + rad + " " + col;
	}
}
