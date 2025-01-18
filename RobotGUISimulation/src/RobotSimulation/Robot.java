package RobotSimulation;

/**
 * The <code> Robot </code> class is a subclass of the <code> ArenaItem </code>
 * that represents a mobile robot in a 2d simulation.
 * 
 * 
 * <p>
 * This class demonstrates the use of inheritance in Java by extending
 * <codeArenaItem</code>, adding movement, collision detection, and drawing
 * logic specific to the robot.
 * </p>
 * 
 * <p>
 * The robot can toggle its wheel colour under certain conditions, move at
 * specified speed and angle, and detect collisions with obstacles and other
 * robots.
 * </p>
 * 
 * @author Ahmed Elamari
 * @version 1.0
 * 
 * @see ArenaItem
 */
public class Robot extends ArenaItem {
	/** a reference to the arnea in which this robot operates. */
	RobotArena arena;

	/** Current rotation angle of the robot in degrees. */
	protected double rAngle;

	/** Speed at which the robot moves (units per simulation step). */
	protected double rSpeed;

	/**
	 * Current wheel color indicator.
	 * <p>
	 * Default is 'l' (representing black), can toggle to 'w' (white) if blackout.
	 * </p>
	 */
	protected char wheelLineColour = 'l';

	/**
	 * Constructs a <code>Robot</code> object with the specified coordinates,
	 * radius, angle, speed, and arena reference.
	 * 
	 * @param ix    the initial X coordinate of the robot
	 * @param iy    the initial Y coordinate of the robot
	 * @param ir    the radius of the robot
	 * @param ia    the initial rotation angle of the robot (degrees)
	 * @param is    the movement speed of the robot
	 * @param arena reference to the code <code>RobotArena</code> where robot
	 *              operates.
	 */
	public Robot(double ix, double iy, double ir, double ia, double is, RobotArena arena) {
		super(ix, iy, ir); // Initialize the parent class with position and radius
		this.arena = arena;
		col = 'r'; // Set the colour representation of the robot to 'r' (red)
		rAngle = ia; // Initialize the rotation angle
		rSpeed = is; // Initialize the movement speed
		wheelLineColour = 'l'; // Default wheel line colour (black)
	}

	/**
	 * Toggles the wheel line colour based on blackout status.
	 * 
	 * <p>
	 * If <code>blackOut</code> is true and the wheel colour is currently black
	 * ('l'), switch it to white ('w'). If <code>blackOut</code> is false and the
	 * wheel colour is white ('w'), switch it back to black ('l').
	 * </p>
	 * 
	 * @param blackOut a boolean indicating whether the arnea lights are out
	 */
	public void toggleWheelColor(boolean blackOut) {
		if (blackOut && wheelLineColour == 'l') {
			wheelLineColour = 'w'; // Turn them white
		} else if (!blackOut && wheelLineColour == 'w') {
			wheelLineColour = 'l'; // Turn them black
		}
	}

	/**
	 * Draws this robot on the specified <code>MyCanvas</code>.
	 * 
	 * <p>
	 * In addition to the main body, this robot has two wheels drawn as thick lines
	 * offset from the center by ±45° to ±135° relative to the robot's heading
	 * (<code>rAngle</code>).
	 * </p>
	 * 
	 * @param mc the <code>MyCanvas</code> object used for drawing shapes
	 */
	@Override
	public void drawItem(MyCanvas mc) {
		// Draw the main robot body
		mc.showCircle(x, y, rad, col);

		// Optionally set a thicker line width for drawing the wheels
		mc.setLineWidth(5);

		mc.setStrokeColour(wheelLineColour); // Set the wheel line colour

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
	 * Calculates the X coordinate offset by distance <code>s</code> at angle
	 * <code>deg</code> relative to this robot’s current position (<code>x</code>,
	 * <code>y</code>).
	 * 
	 * @param s   the distance from robot's center
	 * @param deg the angle in degrees relative to robot's center
	 * @return the computed X coordinate
	 */
	public double calcX(double s, double deg) {
		double radians = Math.toRadians(deg);
		return x + s * Math.cos(radians);
	}

	/**
	 * Calculates the Y coordinate offset by distance <code>s</code> at angle
	 * <code>deg</code> relative to this robot’s current position
	 * (<code>x, y</code>).
	 *
	 * @param s   the distance from the robot's center
	 * @param deg the angle in degrees relative to the robot's center
	 * @return the computed Y coordinate
	 */
	public double calcY(double s, double deg) {
		double radians = Math.toRadians(deg);
		return y + s * Math.sin(radians);
	}

	/**
	 * Checks for collisions with obstacles and other robots in the arena, and
	 * adjusts the robot’s angle accordingly.
	 *
	 * @param r the <code>RobotArena</code> that contains this robot and other items
	 */
	@Override
	public void checkItem(RobotArena r) {
		// Check for collisions with obstacles
		for (ArenaItem i : r.items) {
			if (i instanceof Obstacle) {
				Obstacle o = (Obstacle) i;
				if (hitting(o)) {
					// Request a new angle from the arena upon collision
					rAngle = r.CheckRobotAngle(x, y, rad, rAngle, itemID);
				}
			}
		}

		// Check collisions with other robots
		for (ArenaItem i : r.items) {
			if (i instanceof Robot && i.getID() != this.itemID) {
				if (hitting(i)) {
					// Request a new angle if robots collide
					rAngle = r.CheckRobotAngle(x, y, rad, rAngle, itemID);
				}
			}
		}

		// Finally, check if the robot is near the arena walls and adjust angle if
		// needed (bounce off walls)
		rAngle = r.CheckRobotAngle(x, y, rad, rAngle, itemID);
	}

	/**
	 * Updates the robot's position based on its speed and angle, and randomly
	 * changes its angle with a small probability.
	 */
	@Override
	public void adjustItem() {
		// Convert angle to radians for movement
		double radAngle = Math.toRadians(rAngle);

		// Update position by speed along the current angle
		x += rSpeed * Math.cos(radAngle);
		y += rSpeed * Math.sin(radAngle);

		// Introduce a small (5%) chance to change direction randomly
		double changeProbability = 0.05; // 5% chance
		if (Math.random() < changeProbability) {
			// Random offset between -20 and +20 degrees
			double randomOffset = (Math.random() * 40) - 20;
			rAngle = (rAngle + randomOffset) % 360;
		}
	}

	/**
	 * Returns a string representing the type of this item.
	 *
	 * @return a <code>String</code> "Robot"
	 */
	@Override
	protected String getStrType() {
		// Returns the type of the object as a string
		return "Robot";
	}

	/**
	 * Sets the angle of the robot to the specified new angle.
	 *
	 * @param newAngle the new angle in degrees
	 */
	public void setAngle(double newAngle) {
		// Sets the angle of the robot to the specified new angle
		rAngle = newAngle;
	}

	/**
	 * Returns the current angle of the robot in degrees.
	 *
	 * @return the current angle of the robot
	 */
	public double getAngle() {
		// Returns the current angle of the robot
		return rAngle;
	}

	/**
	 * Returns a formatted string to represent this robot for file output.
	 *
	 * @return a <code>String</code> containing the robot’s parameters
	 */
	@Override
	public String fileString() {
		return String.format("Robot %.1f %.1f %.1f %c %.1f %.1f", x, y, rad, col, rAngle, rSpeed);
	}
}