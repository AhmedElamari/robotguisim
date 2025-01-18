package RobotSimulation;

/**
 * The <code>Obstacle</code> class is a subclass of <code>ArenaItem</code> that
 * represents a stationary obstacle in the robot simulation arena.
 *
 * <p>
 * Obstacles do not move but can collide with robots, causing the robots to
 * bounce or change direction. This class demonstrates how collision handling
 * can be implemented for non-moving entities.
 * </p>
 *
 * @author Ahmed Elamari
 * @version 1.0
 *
 * @see ArenaItem
 */
public class Obstacle extends ArenaItem {

	/**
	 * Constructs an <code>Obstacle</code> object at the specified coordinates with
	 * the given radius.
	 *
	 * @param ix the initial X coordinate of the obstacle
	 * @param iy the initial Y coordinate of the obstacle
	 * @param ir the radius of the obstacle
	 */
	public Obstacle(double ix, double iy, double ir) {
		super(ix, iy, ir);
		col = 'b'; // Use 'b' to visually represent this obstacle (e.g., blue)
	}

	/**
	 * Draws this obstacle on the specified <code>MyCanvas</code>.
	 *
	 * <p>
	 * By default, this obstacle is represented as a simple circle.
	 * </p>
	 *
	 * @param mc the <code>MyCanvas</code> object used for drawing shapes
	 */
	@Override
	public void drawItem(MyCanvas mc) {
		mc.showCircle(x, y, rad, col);
	}

	/**
	 * Checks for collisions between any robots and this obstacle.
	 *
	 * <p>
	 * If a collision occurs, the <code>RobotArena</code>'s collision logic is
	 * invoked to update the robot’s angle. The robot may also be nudged outward
	 * slightly to prevent it from getting stuck inside the obstacle.
	 * </p>
	 *
	 * @param r the <code>RobotArena</code> containing all arena items
	 */
	@Override
	public void checkItem(RobotArena r) {
		// Loop through all items in the arena
		for (ArenaItem item : r.items) {
			if (item instanceof Robot) {
				Robot rob = (Robot) item;

				// Calculate the distance between the robot's center and this obstacle's center
				double distX = rob.getX() - x;
				double distY = rob.getY() - y;
				double distance = Math.sqrt(distX * distX + distY * distY);

				// Check if they collide (distance < sum of radii)
				if (distance < (rob.getRad() + rad)) {
					// Use RobotArena's collision logic to randomize the bounce angle
					double newAngle = r.CheckRobotAngle(rob.getX(), rob.getY(), rob.getRad(), rob.getAngle(),
							rob.getID());
					rob.setAngle(newAngle);

					// Nudge the robot outward slightly so it doesn't remain stuck
					double angleRad = Math.toRadians(newAngle);
					rob.setXY(rob.getX() + Math.cos(angleRad), rob.getY() + Math.sin(angleRad));
				}
			}
		}
	}

	/**
	 * Adjusting an obstacle is not necessary since it is stationary by default.
	 */
	@Override
	public void adjustItem() {
		// No movement for a stationary obstacle
	}

	/**
	 * Returns a string describing this obstacle, including coordinates and radius.
	 *
	 * @return a <code>String</code> containing obstacle details
	 */
	@Override
	public String toString() {
		return "Obstacle at " + x + ", " + y + " with radius " + rad;
	}

	/**
	 * Allows repositioning of this obstacle if needed.
	 *
	 * @param x2 the new X coordinate
	 * @param y2 the new Y coordinate
	 */
	@Override
	protected void setXY(double x2, double y2) {
		x = x2;
		y = y2;
	}

	/**
	 * Returns a formatted string for file output, representing this obstacle.
	 *
	 * @return a <code>String</code> containing the obstacle’s parameters
	 */
	@Override
	public String fileString() {
		// Format: Obstacle x y radius col
		return "Obstacle " + x + " " + y + " " + rad + " " + col;
	}
}
