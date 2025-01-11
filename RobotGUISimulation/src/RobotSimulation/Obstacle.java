package RobotSimulation;

public class Obstacle extends ArenaItem {

	public Obstacle(double ix, double iy, double ir) {
		super(ix, iy, ir);
		col = 'b'; // Set obstacle color (blue for example)
	}

	/**
	 * Example method to create a new obstacle; how you store/add this obstacle is
	 * up to your arena design.
	 */
	public void addObstacle(double x, double y, double rad, char col) {
		Obstacle o = new Obstacle(x, y, rad);
		// Optionally add 'o' to a collection in RobotArena or elsewhere
	}

	/**
	 * Draw a simple circle to represent the obstacle.
	 */
	@Override
	public void drawItem(MyCanvas mc) {
		mc.showCircle(x, y, rad, col);
	}

	/**
	 * Checks if any robots collide with this obstacle. If a collision occurs,
	 * randomize their bounce angle similarly to a wall or robot collision.
	 */
	@Override
	public void checkItem(RobotArena r) {
		for (ArenaItem item : r.items) {
			if (item instanceof Robot) {
				Robot rob = (Robot) item;
				// Calculate distance from the robot's center to the obstacle's center
				double distX = rob.getX() - x;
				double distY = rob.getY() - y;
				double distance = Math.sqrt(distX * distX + distY * distY);

				// If within collision range (sum of radii)
				if (distance < (rob.getRad() + rad)) {
					// Use RobotArena's collision logic to randomize the bounce angle
					double newAngle = r.CheckRobotAngle(rob.getX(), rob.getY(), rob.getRad(), rob.getAngle(),
							rob.getID());
					rob.setAngle(newAngle);

					// Optional: nudge the robot outward slightly so it doesn't stay stuck
					double angleRad = Math.toRadians(newAngle);
					rob.setXY(rob.getX() + Math.cos(angleRad), rob.getY() + Math.sin(angleRad));
				}
			}
		}
	}

	/**
	 * Obstacles are typically stationary, so adjusting them each tick usually isn’t
	 * necessary.
	 */
	@Override
	public void adjustItem() {
		// No movement for the obstacle by default
	}

	@Override
	public String toString() {
		return "Obstacle at " + x + ", " + y + " with radius " + rad;
	}

	/**
	 * Allows repositioning of the obstacle if needed.
	 */
	@Override
	protected void setXY(double x2, double y2) {
		x = x2;
		y = y2;
	}

	@Override
	public String fileString() {
		return "Obstacle " + x + " " + y + " " + rad + " " + col;
	}
}