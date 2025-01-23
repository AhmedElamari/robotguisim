package RobotSimulation;

public class triRobot extends Robot {

	// ===== NEW ADVANCED PHYSICS FIELDS =====
	private double mass; // For momentum-based collisions, e.g. kg
	private double velocityX; // Robot's velocity in X direction
	private double velocityY; // Robot's velocity in Y direction

	// Differential wheel speeds (skid-steering style)
	private double frontWheelSpeed = 0.0;
	private double leftWheelSpeed = 0.0;
	private double rightWheelSpeed = 0.0;

	// friction or drag factor: portion of velocity we lose each frame
	private double frictionFactor = 0.02;

	// For more realistic rotation, store angularVelocity if you want
	private double angularVelocity = 0.0; // degrees/frame or rad/frame

	/**
	 * Basic constructor. - ix, iy: initial position - ir: radius - ia: initial
	 * angle - is: speed (we won't rely on 'rSpeed' as much, we'll build from wheel
	 * speeds) - arena: reference to RobotArena
	 */
	public triRobot(double ix, double iy, double ir, double ia, double is, RobotArena arena) {
		super(ix, iy, ir, ia, is, arena);
		col = 'p'; // color code in char
		mass = 8.0; // pick any mass
		// Initialize velocity from the super's rSpeed + rAngle if desired
		double radAngle = Math.toRadians(rAngle);
		this.velocityX = rSpeed * Math.cos(radAngle);
		this.velocityY = rSpeed * Math.sin(radAngle);

		// Initialize each wheel to the same speed as the base 'rSpeed'
		this.frontWheelSpeed = rSpeed;
		this.leftWheelSpeed = rSpeed;
		this.rightWheelSpeed = rSpeed;
	}

	/**
	 * Draw the triRobot as a triangle plus small wheels on each corner.
	 */
	@Override
	public void drawItem(MyCanvas mc) {
		mc.showTriangle(x, y, rad, col);

		// Let's place small wheels
		double wheelRad = rad / 4.0;
		// The corners of our triangle:
		double[] xpoints = { x, x - rad, x + rad };
		double[] ypoints = { y - rad, y + rad, y + rad };

		for (int i = 0; i < 3; i++) {
			mc.showCircle(xpoints[i], ypoints[i], wheelRad, wheelLineColour);
		}
	}

	/**
	 * Overridden checkItem for advanced collisions: We'll do inelastic or partially
	 * elastic collisions. If we hit an obstacle or another robot, we compute
	 * momentum exchange, etc.
	 */
	@Override
	public void checkItem(RobotArena arena) {
		// Let's still do the "bounce off walls" logic from super:
		super.checkItem(arena);

		// Then do advanced collision with other items
		for (ArenaItem item : arena.items) {
			if (item == this)
				continue;
			// Quick bounding check
			double dx = item.getX() - x;
			double dy = item.getY() - y;
			double dist = Math.sqrt(dx * dx + dy * dy);
			if (dist < item.getRad() + rad) {
				// There's a collision
				// We'll do a naive "momentum-based" collision if the item is a triRobot or
				// Robot
				// If it's an Obstacle or something else, we might treat it as immovable or
				// having mass
				if (item instanceof Robot) {
					Robot other = (Robot) item;
					double otherMass = 5.0; // if normal Robot doesn't store mass, assume 5
					double otherVx = 0;
					double otherVy = 0;
					if (other instanceof triRobot) {
						// If the other robot is also triRobot, we can read its velocity
						otherMass = ((triRobot) other).getMass();
						otherVx = ((triRobot) other).getVelocityX();
						otherVy = ((triRobot) other).getVelocityY();
					} else {
						// Otherwise, maybe approximate from rSpeed + angle
						double oradAngle = Math.toRadians(other.getAngle());
						otherVx = other.getSpeed() * Math.cos(oradAngle);
						otherVy = other.getSpeed() * Math.sin(oradAngle);
					}

					// Combine velocities for an inelastic collision
					// (m1 v1 + m2 v2) / (m1 + m2)
					double totalMass = this.mass + otherMass;
					double newVx = (this.mass * velocityX + otherMass * otherVx) / totalMass;
					double newVy = (this.mass * velocityY + otherMass * otherVy) / totalMass;

					// Set velocities back
					this.velocityX = newVx;
					this.velocityY = newVy;
					// If the other is also triRobot, set its velocity
					if (other instanceof triRobot) {
						((triRobot) other).setVelocity(newVx, newVy);
					} else {
						// or we skip or do something simpler
						other.setSpeed(Math.sqrt(newVx * newVx + newVy * newVy));
						double colAngle = Math.toDegrees(Math.atan2(newVy, newVx));
						other.setAngle(colAngle);
					}

					// Now separate them so they don't overlap
					// Move this triRobot half the overlap distance away,
					// and the other half if it's triRobot, or skip if it's a normal Robot
					double overlap = (item.getRad() + rad) - dist;
					if (overlap > 0) {
						double half = overlap / 2.0;
						double nx = dx / dist; // normal x
						double ny = dy / dist; // normal y
						// push this out
						x -= nx * half;
						y -= ny * half;
						if (other instanceof triRobot) {
							((triRobot) other).x += nx * half;
							((triRobot) other).y += ny * half;
						}
					}
				} else if (item instanceof Obstacle) {
					// We can do a partial bounce or treat obstacle as immovable
					// e.g. reflect velocity or set velocityX,Y to 0
					// For simplicity, let's just zero out the velocity:
					this.velocityX = 0;
					this.velocityY = 0;
				}
			}
		}
	}

	/**
	 * Overridden adjustItem for advanced movement with friction, wheel speeds, etc.
	 */
	@Override
	public void adjustItem() {
		// 1) Decide how each wheel modifies velocity + rotation
		// For a simple approach, let's say:
		// frontWheel controls forward, leftWheel, rightWheel partially control turning
		double averageWheelSpeed = (frontWheelSpeed + leftWheelSpeed + rightWheelSpeed) / 3.0;
		// A turning factor from difference of left/right wheels
		double turnFactor = (rightWheelSpeed - leftWheelSpeed) * 0.5;
		// optional: incorporate frontWheel differently if you want ackermann geometry

		// 2) Angular Velocity approach:
		angularVelocity += turnFactor; // naive, you can scale it by mass or something

		// 3) Update heading
		rAngle += angularVelocity;
		rAngle %= 360.0;

		// 4) Convert averageWheelSpeed to a forward velocity in heading rAngle
		double desiredVx = averageWheelSpeed * Math.cos(Math.toRadians(rAngle));
		double desiredVy = averageWheelSpeed * Math.sin(Math.toRadians(rAngle));

		// 5) Merge with current velocity (some smoothing or direct set)
		velocityX = 0.9 * velocityX + 0.1 * desiredVx;
		velocityY = 0.9 * velocityY + 0.1 * desiredVy;

		// 6) Apply friction
		velocityX *= (1.0 - frictionFactor);
		velocityY *= (1.0 - frictionFactor);

		// 7) Finally update position
		x += velocityX;
		y += velocityY;
	}

	// ===== ADDITIONAL GETTERS/SETTERS for velocity, mass, etc. =====

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

	/**
	 * Optionally set or tweak wheel speeds if you want some AI or user input
	 * controlling them.
	 */
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
}
