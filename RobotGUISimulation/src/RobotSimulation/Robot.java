// Java class for the Robot object, which extends ArenaItem and represents a robot within the arena.
package RobotSimulation;

public class Robot extends ArenaItem {
	RobotArena arena; // Reference to the arena where the robot operates
	double rAngle, rSpeed; // Angle of rotation and speed of the robot

	public Robot(double ix, double iy, double ir, double ia, double is, RobotArena arena) {
		super(ix, iy, ir); // Initialize the parent class with position and radius
		this.arena = arena; // Assign the arena to the robot
		col = 'r'; // Set the color representation of the robot
		rAngle = ia; // Initialize the rotation angle
		rSpeed = is; // Initialize the speed
	}

	@Override
	public void drawItem(MyCanvas mc) {
		// Draw the main robot body
		mc.showCircle(x, y, rad, col);

		// Optionally set a thicker line width for drawing the wheels
		mc.setLineWidth(5);

		// These two wheels each extend from angle ±45° to angle ±135° relative to the
		// robot’s heading.
		// We'll use calcX and calcY to simplify offset calculations.

		// -------------------------
		// Right-side wheel
		// -------------------------
		// Start point at (rAngle + 45°) from the robot center
		double startX1 = calcX(rad, rAngle + 45);
		double startY1 = calcY(rad, rAngle + 45);
		// End point at (rAngle + 135°) from the robot center
		double endX1 = calcX(rad, rAngle + 135);
		double endY1 = calcY(rad, rAngle + 135);

		// Draw the right-side wheel
		mc.drawLine(startX1, startY1, endX1, endY1);

		// -------------------------
		// Left-side wheel
		// -------------------------
		// Start point at (rAngle - 45°) from the robot center
		double startX2 = calcX(rad, rAngle - 45);
		double startY2 = calcY(rad, rAngle - 45);
		// End point at (rAngle - 135°) from the robot center
		double endX2 = calcX(rad, rAngle - 135);
		double endY2 = calcY(rad, rAngle - 135);

		// Draw the left-side wheel
		mc.drawLine(startX2, startY2, endX2, endY2);

		// Reset line width if desired (depends on your MyCanvas implementation)
		mc.setLineWidth(1);
	}

	/**
	 * Helper method to calculate an X coordinate offset by distance s at angle deg
	 * relative to this robot’s position (x, y).
	 */
	public double calcX(double s, double deg) {
		double radians = Math.toRadians(deg);
		return x + s * Math.cos(radians);
	}

	/**
	 * Helper method to calculate a Y coordinate offset by distance s at angle deg
	 * relative to this robot’s position (x, y).
	 */
	public double calcY(double s, double deg) {
		double radians = Math.toRadians(deg);
		return y + s * Math.sin(radians);
	}

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

	@Override
	public void adjustItem() {
		// Convert the current angle to radians for basic movement
		double radAngle = Math.toRadians(rAngle);

		// Update position by speed along the current angle
		x += rSpeed * Math.cos(radAngle);
		y += rSpeed * Math.sin(radAngle);

		// Introduce a small chance to change direction randomly
		double changeProbability = 0.05; // 5% chance on each move
		if (Math.random() < changeProbability) {
			// Random offset between -20 and +20 degrees
			double randomOffset = (Math.random() * 40) - 20;
			rAngle = (rAngle + randomOffset) % 360;
		}
	}

	@Override
	protected String getStrType() {
		// Returns the type of the object as a string
		return "Robot";
	}

	public void setAngle(double newAngle) {
		// Sets the angle of the robot to the specified new angle
		rAngle = newAngle;
	}

	public double getAngle() {
		// Returns the current angle of the robot
		return rAngle;
	}

	@Override
	public String fileString() {
		return String.format("Robot %.1f %.1f %.1f %c %.1f %.1f", x, y, rad, col, rAngle, rSpeed);
	}
}