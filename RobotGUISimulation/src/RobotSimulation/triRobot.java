package RobotSimulation;

/**
 * The <code>triRobot</code> class represents an advanced robotic entity in the
 * simulation, equipped with mass, velocity, and angular momentum for realistic
 * physics-based interactions. It supports advanced collision handling,
 * momentum-based physics, and wheel-speed-driven movement.
 * 
 * <p>
 * This class extends the base Robot class and adds additional fields for
 * physics and wheel-based control.
 * </p>
 * 
 * @author Ahmed Elamari
 * @version 2.0
 */
public class triRobot extends Robot {

	/** Mass of the robot for physics-based collision calculations. */
	private double mass;

	/** Velocity of the robot in the X direction. */
	private double velocityX;

	/** Velocity of the robot in the Y direction. */
	private double velocityY;

	/** Speed of the front wheel for advanced movement control. */
	private double frontWheelSpeed = 0.0;

	/** Speed of the left wheel for advanced movement control. */
	private double leftWheelSpeed = 0.0;

	/** Speed of the right wheel for advanced movement control. */
	private double rightWheelSpeed = 0.0;

	/** Friction factor representing the percentage of velocity lost per frame. */
	private double frictionFactor = 0.02;

	/** Angular velocity of the robot, determining its rotational speed. */
	private double angularVelocity = 0.0;

	/**
	 * Constructs a triRobot with specified initial position, radius, angle, speed,
	 * and arena reference.
	 * 
	 * @param ix    The initial x-coordinate.
	 * @param iy    The initial y-coordinate.
	 * @param ir    The radius of the robot.
	 * @param ia    The initial angle (in degrees).
	 * @param is    The initial speed.
	 * @param arena Reference to the RobotArena.
	 */
	public triRobot(double ix, double iy, double ir, double ia, double is, RobotArena arena) {
		super(ix, iy, ir, ia, is, arena);
		col = 'p'; // Robot color (e.g., 'p' for purple).
		mass = 8.0; // Default mass value for physics calculations.

		// Initialize velocity based on the robot's speed and angle.
		double radAngle = Math.toRadians(rAngle);
		this.velocityX = rSpeed * Math.cos(radAngle);
		this.velocityY = rSpeed * Math.sin(radAngle);

		// Set initial wheel speeds to the robot's base speed.
		this.frontWheelSpeed = rSpeed;
		this.leftWheelSpeed = rSpeed;
		this.rightWheelSpeed = rSpeed;
	}

	/**
	 * Draws the triRobot as a triangle with small wheels on each corner.
	 * 
	 * @param mc The MyCanvas object to handle drawing operations.
	 */
	@Override
	public void drawItem(MyCanvas mc) {
		// Draw the main triangular body.
		mc.showTriangle(x, y, rad, col);

		// Calculate wheel positions and draw them as circles.
		double wheelRad = rad / 4.0;
		double[] xpoints = { x, x - rad, x + rad };
		double[] ypoints = { y - rad, y + rad, y + rad };
		for (int i = 0; i < 3; i++) {
			mc.showCircle(xpoints[i], ypoints[i], wheelRad, wheelLineColour);
		}
	}

	/**
	 * Checks for collisions with other items in the arena and handles
	 * momentum-based interactions.
	 * 
	 * @param arena The RobotArena containing the triRobot and other items.
	 */
	@Override
	public void checkItem(RobotArena arena) {
		// Perform default wall collision checks.
		super.checkItem(arena);

		// Check for collisions with other items in the arena.
		for (ArenaItem item : arena.items) {
			if (item == this) {
				continue; // Skip self-check.
			}

			// Calculate the distance between this robot and the item.
			double dx = item.getX() - x;
			double dy = item.getY() - y;
			double dist = Math.sqrt(dx * dx + dy * dy);

			// Check if the item is within collision distance.
			if (dist < item.getRad() + rad) {
				// Handle collision based on the type of item.
				if (item instanceof Robot) {
					handleRobotCollision(arena, (Robot) item);
				} else if (item instanceof Obstacle) {
					// Treat obstacle as immovable; zero out the velocity.
					this.velocityX = 0;
					this.velocityY = 0;
				}
			}
		}
	}

	/**
	 * Handles advanced movement logic using wheel speeds, angular velocity, and
	 * friction.
	 */
	@Override
	public void adjustItem() {
		// Calculate the average speed of all wheels and a turning factor.
		double averageWheelSpeed = (frontWheelSpeed + leftWheelSpeed + rightWheelSpeed) / 3.0;
		double turnFactor = (rightWheelSpeed - leftWheelSpeed) * 0.5;

		// Update angular velocity and heading.
		angularVelocity += turnFactor;
		rAngle += angularVelocity;
		rAngle %= 360.0; // Keep angle within 0-360 degrees.

		// Calculate desired velocity based on wheel speeds and angle.
		double desiredVx = averageWheelSpeed * Math.cos(Math.toRadians(rAngle));
		double desiredVy = averageWheelSpeed * Math.sin(Math.toRadians(rAngle));

		// Smoothly adjust the velocity toward the desired velocity.
		velocityX = 0.9 * velocityX + 0.1 * desiredVx;
		velocityY = 0.9 * velocityY + 0.1 * desiredVy;

		// Apply friction to the velocity.
		velocityX *= (1.0 - frictionFactor);
		velocityY *= (1.0 - frictionFactor);

		// Update the robot's position.
		x += velocityX;
		y += velocityY;
	}

	// ===== Additional Helper Methods =====

	/**
	 * Handles momentum-based collision with another Robot.
	 * 
	 * @param arena the RobotArena context
	 * @param other the Robot with which we're colliding
	 */
	private void handleRobotCollision(RobotArena arena, Robot other) {
		// Example: inelastic collision logic
		double dx = other.getX() - x;
		double dy = other.getY() - y;
		double dist = Math.sqrt(dx * dx + dy * dy);

		// Just a quick bounding check again
		if (dist >= other.getRad() + this.rad) {
			return; // No real collision
		}

		// For momentum-based collision, we need mass & velocity.
		// If 'other' is also a triRobot, we can read its mass & velocity.
		double otherMass = 5.0; // default if normal Robot
		double otherVx = 0;
		double otherVy = 0;

		if (other instanceof triRobot) {
			otherMass = ((triRobot) other).getMass();
			otherVx = ((triRobot) other).getVelocityX();
			otherVy = ((triRobot) other).getVelocityY();
		} else {
			// Approx for normal Robot
			double angleRad = Math.toRadians(other.getAngle());
			otherVx = other.getSpeed() * Math.cos(angleRad);
			otherVy = other.getSpeed() * Math.sin(angleRad);
		}

		double totalMass = this.mass + otherMass;
		double newVx = (this.mass * this.velocityX + otherMass * otherVx) / totalMass;
		double newVy = (this.mass * this.velocityY + otherMass * otherVy) / totalMass;

		// Update this triRobot
		this.velocityX = newVx;
		this.velocityY = newVy;

		// Update the other Robot if it's also triRobot
		if (other instanceof triRobot) {
			((triRobot) other).setVelocity(newVx, newVy);
		} else {
			// Or forcibly set its speed/angle
			other.setSpeed(Math.sqrt(newVx * newVx + newVy * newVy));
			double colAngle = Math.toDegrees(Math.atan2(newVy, newVx));
			other.setAngle(colAngle);
		}

		// Optional feature: push them apart so they don't remain overlapping
		double overlap = other.getRad() + this.rad - dist;
		if (overlap > 0) {
			double half = overlap / 2.0;
			double nx = dx / dist;
			double ny = dy / dist;

			// Move this triRobot
			this.x -= nx * half;
			this.y -= ny * half;

			// If it's also triRobot, move that one
			if (other instanceof triRobot) {
				((triRobot) other).x += nx * half;
				((triRobot) other).y += ny * half;
			}
		}
	}

	// Getters and setters for advanced physics fields.

	public double getMass() {
		return mass;
	}

	public double getVelocityX() {
		return velocityX;
	}

	public double getVelocityY() {
		return velocityY;
	}

	public void setVelocity(double vx, double vy) {
		this.velocityX = vx;
		this.velocityY = vy;
	}

	public void setWheelSpeeds(double front, double left, double right) {
		this.frontWheelSpeed = front;
		this.leftWheelSpeed = left;
		this.rightWheelSpeed = right;
	}

	public double getFrontWheelSpeed() {
		return frontWheelSpeed;
	}

	public double getLeftWheelSpeed() {
		return leftWheelSpeed;
	}

	public double getRightWheelSpeed() {
		return rightWheelSpeed;
	}

	public void setFrictionFactor(double f) {
		frictionFactor = f;
	}

	/**
	 * Returns the type of the Robot as a string.
	 */
	public String getStrType() {
		return "triRobot";
	}

	/**
	 * Returns a formatted string to represent this triRobot for file output.
	 */
	public String fileString() {
		return String.format("triRobot %.1f %.1f %.1f %c %.1f %.1f", x, y, rad, col, rAngle, rSpeed);
	}
}
