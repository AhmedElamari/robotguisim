package RobotSimulation;

/**
 * The <code>miniObstacleXP</code> class represents a small obstacle in the
 * robot arena. This obstacle can interact with robots in the arena and be
 * "absorbed" upon collision, awarding points to the arena's score.
 * 
 * <p>
 * This class extends the <code> Obstacle </code> class and overrides its
 * behavior to include collision detection and handling with robots in the
 * arena.
 * </p>
 * 
 * @author Ahmed Elamari
 * @version 1.0
 */
public class miniObstacleXP extends Obstacle {

	/** Flag indicating whether the obstacle is still active in the arena. */
	private boolean active = true;

	/**
	 * Constructs a miniObstacleXP object with a specific position and radius.
	 * 
	 * @param x The x-coordinate of the obstacle.
	 * @param y The y-coordinate of the obstacle.
	 * @param r The radius of the obstacle.
	 */
	public miniObstacleXP(double x, double y, double r) {
		super(x, y, r); // Initialize the obstacle's position and radius.
		col = 'g'; // Set the obstacle's color (e.g., 'g' for green).
	}

	/**
	 * Checks whether the obstacle has collided with any robot in the arena. If a
	 * collision occurs, the obstacle is "absorbed" (removed) and a point is added
	 * to the arena's score.
	 * 
	 * @param arena The arena in which this obstacle exists.
	 */
	@Override
	public void checkItem(RobotArena arena) {
		if (!active) {
			// Skip processing if the obstacle is no longer active.
			return;
		}

		// Iterate through each robot in the arena to check for collisions.
		for (Robot robot : arena.getRobots()) {
			// Calculate the distance between the obstacle and the robot.
			double dx = robot.getX() - x;
			double dy = robot.getY() - y;
			double dist = Math.sqrt(dx * dx + dy * dy);

			// If the distance is less than the sum of their radii, a collision has
			// occurred.
			if (dist < robot.getRad() + this.rad) {
				// Remove the obstacle from the arena's item list.
				arena.items.remove(this);
				active = false; // Mark the obstacle as inactive.

				// Award a point to the arena's score.
				arena.addScore(1);

				// Log the absorption event.
				System.out.println("MiniObstacle absorbed for +1 point!");

				// Stop further processing once the obstacle is removed.
				return;
			}
		}
	}
}
