package RobotSimulation;

/**
 * The <code>BeamLight</code> class extends the <code>Beam</code> class to
 * represent a beam that detects and interacts with lights in the arena.
 *
 * <p>
 * When a beam of this type detects a light, it stops moving and tracks that
 * light's position. If no light is detected, the beam resumes a normal speed
 * and moves in a straight line without random directional changes.
 * </p>
 *
 * @author Ahmed Elamari
 * @version 1.0
 *
 * @see Beam
 */
public class BeamLight extends Beam {

	/**
	 * Stores a reference to the currently detected <code>Light</code>, if any.
	 */
	private Light trackedLight = null;

	/**
	 * Indicates whether this beam is currently at (i.e., has detected) a light.
	 */
	private boolean isAtLight = false;

	/**
	 * Constructs a <code>BeamLight</code> object at the specified coordinates,
	 * radius, angle, and speed in the given <code>RobotArena</code>.
	 *
	 * @param ix    the initial X coordinate
	 * @param iy    the initial Y coordinate
	 * @param ir    the radius of the beam
	 * @param ia    the initial angle in degrees
	 * @param is    the movement speed of the beam
	 * @param arena the <code>RobotArena</code> in which this beam operates
	 */
	public BeamLight(double ix, double iy, double ir, double ia, double is, RobotArena arena) {
		super(ix, iy, ir, ia, is, arena);
		col = 'y'; // Use 'y' to visually represent this beam as yellow
	}

	/**
	 * Checks for collisions and interactions with other items in the arena.
	 *
	 * <p>
	 * In addition to the standard collision checks inherited from
	 * <code>Beam</code>, this method checks whether any of the beam’s points
	 * intersect with a <code>Light</code>. If a light is detected, the beam will
	 * stop moving and track that light. Otherwise, the beam continues at normal
	 * speed.
	 * </p>
	 *
	 * @param r the <code>RobotArena</code> containing all items
	 */
	@Override
	public void checkItem(RobotArena r) {
		// Retain standard collision logic from the parent Beam class
		super.checkItem(r);

		// Check if any beam points intersect with a light
		boolean beamDetectsLight = false;
		Light nearestLight = null;
		double closestDist = Double.MAX_VALUE;

		// 'beamPoints' is presumably defined in the parent Beam class,
		// representing discrete points along the beam.
		for (double[] pt : beamPoints) {
			for (ArenaItem item : r.items) {
				if (item instanceof Light) {
					double dist = distance(pt[0], pt[1], item.x, item.y);
					// Add a small buffer around the light radius for intersection
					if (dist < item.rad + 5) {
						beamDetectsLight = true;
						// Keep track of the closest light
						if (dist < closestDist) {
							closestDist = dist;
							nearestLight = (Light) item;
						}
					}
				}
			}
		}

		// Update tracking state based on detection
		if (beamDetectsLight) {
			trackedLight = nearestLight;
			isAtLight = true;
			rSpeed = 0; // Stop movement when a light is detected
		} else {
			trackedLight = null;
			isAtLight = false;
			rSpeed = 1; // Resume normal speed if no light is detected
		}
	}

	/**
	 * Updates the beam’s position each simulation tick, if not at a light.
	 *
	 * <p>
	 * This subclass overrides the parent <code>adjustItem</code> to remove random
	 * direction changes, resulting in straight-line movement until a light is
	 * detected.
	 * </p>
	 */
	@Override
	public void adjustItem() {
		// Only move if the beam has not stopped at a light
		if (!isAtLight) {
			// 1. Randomly adjust the angle slightly (drift)
			// -0.5 to +0.5 range multiplied by a drift factor
			double drift = (Math.random() - 0.5) * 2.0; // range: -1 to +1
			double driftFactor = 0.3; // how strongly to apply the drift
			rAngle += driftFactor * drift; // add a small deviation to rAngle

			// 2. Randomly vary the speed a bit
			// E.g. ±5% variation around the original speed
			double speedVariation = 1.0 + ((Math.random() - 0.5) * 0.1);
			double currentSpeed = rSpeed * speedVariation;

			// 3. Convert the angle to radians for the movement calculation
			double radAngle = Math.toRadians(rAngle);

			// 4. Update x and y coordinates based on the adjusted speed and angle
			x += currentSpeed * Math.cos(radAngle);
			y += currentSpeed * Math.sin(radAngle);

			// 5. (Optional) Adding a small damping/friction so that speed gradually
			// decreases:
			// rSpeed *= 0.99;
		}
	}

	/**
	 * Draws this beam on the specified <code>MyCanvas</code>, along with a line to
	 * the detected light (if any).
	 *
	 * @param mc the <code>MyCanvas</code> object used for drawing shapes
	 */
	@Override
	public void drawItem(MyCanvas mc) {
		// Draw the beam as defined in the parent Beam class
		super.drawItem(mc);

		// Draw a line connecting the beam to the tracked light, if detected
		if (trackedLight != null) {
			mc.drawLine(x, y, trackedLight.getX(), trackedLight.getY());
		}
	}

	/**
	 * Calculates the Euclidean distance between two points.
	 *
	 * @param x1 the X coordinate of the first point
	 * @param y1 the Y coordinate of the first point
	 * @param x2 the X coordinate of the second point
	 * @param y2 the Y coordinate of the second point
	 * @return the distance between the two points
	 */
	protected double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

	/**
	 * Returns a formatted string suitable for file output, representing the key
	 * properties of this beam light.
	 *
	 * @return a <code>String</code> containing the beam light’s parameters
	 */
	@Override
	public String fileString() {
		// Example format: "Beam Light x y radius col angle speed"
		return String.format("Beam Light %.1f %.1f %.1f %c %.1f %.1f", x, y, rad, col, rAngle, rSpeed);
	}
}
