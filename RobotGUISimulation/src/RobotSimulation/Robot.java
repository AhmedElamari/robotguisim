package RobotSimulation;

public class Robot extends ArenaItem {
	RobotArena arena;
	double rAngle, rSpeed;

	public Robot(double ix, double iy, double ir, double ia, double is, RobotArena arena) {
		super(ix, iy, ir);
		this.arena = arena;
		col = 'r';
		rAngle = ia;
		rSpeed = is;
	}

	@Override
	public void drawItem(MyCanvas mc) {
		// Draw the main robot body
		mc.showCircle(x, y, rad, col);

		// Draw wheels at angles ±45° from the robot's heading (rAngle)
		// Convert final angles to radians before calling trigonometric functions

		// First wheel: rAngle + 45
		double wheelAx = calcX(rad, rAngle + 45);
		double wheelAy = calcY(rad, rAngle + 45);
		mc.showCircle(wheelAx, wheelAy, rad / 4, 'l'); // 'l' for wheel color, adjust as desired (blac)

		// Second wheel: rAngle - 45
		double wheelBx = calcX(rad, rAngle - 45);
		double wheelBy = calcY(rad, rAngle - 45);
		mc.showCircle(wheelBx, wheelBy, rad / 4, 'l');

		// Optionally, you can draw additional wheels at ±135° if you’d like a
		// four-wheel look:
		double wheelCx = calcX(rad, rAngle + 135);
		double wheelCy = calcY(rad, rAngle + 135);
		mc.showCircle(wheelCx, wheelCy, rad / 4, 'l');

		double wheelDx = calcX(rad, rAngle - 135);
		double wheelDy = calcY(rad, rAngle - 135);
		mc.showCircle(wheelDx, wheelDy, rad / 4, 'l');
	}

	/**
	 * Helper method to calculate an X coordinate offset by distance s at angle deg
	 * relative to this robot’s position (x, y) and angle orientation.
	 */
	public double calcX(double s, double deg) {
		double radians = Math.toRadians(deg);
		return x + s * Math.cos(radians);
	}

	/**
	 * Helper method to calculate a Y coordinate offset by distance s at angle deg
	 * relative to this robot’s position (x, y) and angle orientation.
	 */
	public double calcY(double s, double deg) {
		double radians = Math.toRadians(deg);
		return y + s * Math.sin(radians);
	}

	/**
	 * Check collisions with obstacles, other robots, and also re-check angle if
	 * near boundaries by calling CheckRobotAngle.
	 */
	@Override
	public void checkItem(RobotArena r) {
		// Check obstacle collisions
		for (ArenaItem i : r.items) {
			if (i instanceof Obstacle) {
				Obstacle o = (Obstacle) i;
				if (hitting(o)) {
					rAngle = r.CheckRobotAngle(x, y, rad, rAngle, itemID);
				}
			}
		}

		// Check collisions with other robots in the arena
		for (ArenaItem i : r.items) {
			if (i instanceof Robot && i.getID() != this.itemID) {
				if (hitting(i)) {
					rAngle = r.CheckRobotAngle(x, y, rad, rAngle, itemID);
				}
			}
		}

		// Finally, check if we're near the arena walls
		rAngle = r.CheckRobotAngle(x, y, rad, rAngle, itemID);
	}

	/**
	 * A possible approach: each time the robot moves, apply a small chance that its
	 * direction will randomly adjust by a few degrees. This makes it move somewhat
	 * unpredictably even after bouncing off walls.
	 */
	public void adjustItem() {
		// Convert the current angle to radians for basic movement
		double radAngle = Math.toRadians(rAngle);

		// Update position by speed along the current angle
		x += rSpeed * Math.cos(radAngle);
		y += rSpeed * Math.sin(radAngle);

		// Introduce a small chance to change direction randomly
		double changeProbability = 0.05; // 5% chance on each move (adjust as desired)
		if (Math.random() < changeProbability) {
			// Random offset between -20 and +20 degrees
			double randomOffset = (Math.random() * 40) - 20;
			rAngle = (rAngle + randomOffset) % 360;
		}

		// You can also insert a boundary check here to detect wall collisions
		// and then add a random offset to rAngle when it bounces.
		// Example:
		// if (x - rad < 0 || x + rad > arena.getXSize()
		// || y - rad < 0 || y + rad > arena.getYSize()) {
		// double collisionOffset = (Math.random() * 40) - 20;
		// rAngle = (rAngle + 180 + collisionOffset) % 360;
		// }
	}

	@Override
	protected String getStrType() {
		return "Robot";
	}

	public void setAngle(double newAngle) {
		// TODO Auto-generated method stub
		rAngle = newAngle;

	}

	public double getAngle() {
		// TODO Auto-generated method stub
		return rAngle;
	}
}