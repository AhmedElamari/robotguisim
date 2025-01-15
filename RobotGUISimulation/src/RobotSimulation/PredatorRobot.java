package RobotSimulation;

import java.util.ArrayList;
import java.util.List;

/**
 * A PredatorRobot class that extends Beam and demonstrates advanced predator
 * behaviors such as: 1. Hunting Packs: Coordinates with nearby Predators to
 * chase the same target. 2. Stealth Mode: Becomes less visible or slower when
 * no prey is detected. 3. Roar Mechanic: Occasionally emits a roar that can
 * stun or slow nearby Prey.
 */
public class PredatorRobot extends Beam {

	// Reference to the tracked Prey or Predator
	private Robot trackedPrey = null;
	// Indicates if this predator is actively chasing
	private boolean isChasing = false;
	// Slightly enlarged buffer for detection
	private static final double DETECTION_BUFFER = 15.0;

	// Stealth mode state: If no targets are found, predator goes into stealth
	private boolean isStealth = false;

	// Roar mechanic: Count how long until next roar
	private int roarCooldown = 0;
	private static final int ROAR_INTERVAL = 300; // example: roar every 300 ticks
	private boolean isEating = false;
	private int preyEaten = 0;
	private static final double EATING_DISTANCE = 5.0; // Distance threshold for eating prey

	private boolean isDetecting = false; // New flag to track detection

	public PredatorRobot(double ix, double iy, double ir, double ia, double is, RobotArena arena) {
		super(ix, iy, ir, ia, is, arena);
		col = 'r'; // Color representation for Predator
		this.beamRadius = 80.0; // Increase beam radius for wider detection
		this.beamSpread = 60.0; // Increase beam angular spread for a wider look
	}

	/**
	 * Checks for collisions or sensor detections with other arena items. Looks
	 * specifically for Prey (or other desired classes). If no fresh targets are
	 * detected, it may go into stealth mode -- but only if the currently tracked
	 * target is definitely out of range.
	 */
	@Override
	public void checkItem(RobotArena r) {
		super.checkItem(r);
		isDetecting = false; // Reset detection flag

		for (double[] pt : beamPoints) {
			for (ArenaItem item : r.items) {
				if (item != this) {
					double dist = distance(pt[0], pt[1], item.getX(), item.getY());
					if (dist < item.getRad() + DETECTION_BUFFER) {
						isDetecting = true; // Set detection flag
					}
				}
			}
		}

		// Check if we can eat currently tracked prey
		if (isChasing && trackedPrey != null) {
			double distToPrey = distance(x, y, trackedPrey.x, trackedPrey.y);
			if (distToPrey < rad + trackedPrey.rad + EATING_DISTANCE) {
				consumePrey(r, trackedPrey);
				return; // Exit after eating
			}
		}
		// Track whether we find any new or existing prey in range
		boolean preyDetectedThisFrame = false;
		Robot nearestPrey = null;
		double closestDistance = Double.MAX_VALUE;

		// Attempt to form or join a “hunting pack” if other Predator is chasing
		// something
		List<PredatorRobot> packPredators = new ArrayList<>();

		// Iterate over the beamPoints to see if we detect a Prey
		for (double[] pt : beamPoints) {
			for (ArenaItem item : r.items) {
				// Check only for Prey (or a specific class if needed)
				if (item instanceof Prey && item != this) {
					double dist = distance(pt[0], pt[1], item.getX(), item.getY());
					if (dist < item.rad + DETECTION_BUFFER) {
						preyDetectedThisFrame = true;

						// Keep track of the nearest Prey
						if (dist < closestDistance) {
							closestDistance = dist;
							nearestPrey = (Robot) item;
						}
					}
				}
				// Identify other PredatorRobots for potential pack behavior
				if (item instanceof PredatorRobot && item != this) {
					packPredators.add((PredatorRobot) item);
				}
			}
		}

		// If we found a Prey in this frame, update the locked-on logic
		if (preyDetectedThisFrame) {
			trackedPrey = nearestPrey;
			isChasing = true;
			isStealth = false; // No stealth when actively hunting
			coordinatePack(packPredators);
		} else {
			// If no new prey is detected this frame, check if an old target is still in
			// range
			if (trackedPrey != null) {
				double currentDist = distance(x, y, trackedPrey.x, trackedPrey.y);
				// If the existing locked target is still close enough, keep chasing
				if (currentDist < trackedPrey.rad + DETECTION_BUFFER + 20.0) {
					// Optional: the +20.0 adds some buffer to avoid dropping the target
					isChasing = true;
					isStealth = false;
				} else {
					// If the trackedPrey is definitely out of range, drop it
					trackedPrey = null;
					isChasing = false;
					isStealth = true;
				}
			} else {
				// If we had no tracked target at all, default to stealth
				isChasing = false;
				isStealth = true;
			}
		}
	}

	private void consumePrey(RobotArena r, Robot prey) {
		if (prey instanceof Prey) {
			// Mark the prey for removal instead of removing it directly
			r.removePrey(prey);

			// Update predator stats
			preyEaten++;
			isEating = true;

			// Grow slightly after eating
			rad = Math.min(rad + 1, 20);

			// Speed boost after eating
			rSpeed = Math.min(rSpeed + 0.1, 3.0);

			// Reset tracking
			trackedPrey = null;
			isChasing = false;
			isStealth = true;
		}
	}

	/**
	 * Adjust the Predator’s movement each tick: 1. If chasing, move faster and
	 * angle toward the tracked Prey. 2. If in stealth mode, reduce speed or move
	 * randomly. 3. Handle roar cooldown and attempt to roar if available.
	 */
	@Override
	public void adjustItem() {
		if (isChasing && trackedPrey != null) {
			double dx = trackedPrey.x - x;
			double dy = trackedPrey.y - y;
			double chaseAngle = Math.toDegrees(Math.atan2(dy, dx));
			rAngle = chaseAngle;
			rSpeed = 2.5; // Increase speed when chasing
		} else if (isStealth) {
			rSpeed = 1.0; // Slow speed in stealth mode
			// Optional: random drifting
			if (Math.random() < 0.05) {
				rAngle = (rAngle + (Math.random() * 40 - 20)) % 360;
			}
		} else {
			// Default movement
			rSpeed = 1.5;
		}

		// Handle roar cooldown
		if (roarCooldown > 0) {
			roarCooldown--;
		} else {
			// Attempt to roar with a small chance each tick
			if (Math.random() < 0.02) {
				performRoar(arena);
				roarCooldown = ROAR_INTERVAL;
			}
		}

		// Standard movement update
		double radAngle = Math.toRadians(rAngle);
		x += rSpeed * Math.cos(radAngle);
		y += rSpeed * Math.sin(radAngle);
	}

	/**
	 * Draws the Predator on the canvas. Draws a line to any tracked prey when
	 * chasing.
	 */
	@Override
	public void drawItem(MyCanvas mc) {
		char originalColor = col; // Save the original color

		if (isDetecting) {
			col = 'g'; // Change to green if detecting
		}

		// Draw eating animation when applicable
		if (isEating) {
			char eatingColor = 'o'; // Orange flash
			mc.showCircle(x, y, rad * 1.2, eatingColor); // Larger circle when eating
			isEating = false; // Reset eating state
		} else {
			super.drawItem(mc);
			if (isChasing && trackedPrey != null) {
				mc.drawLine(x, y, trackedPrey.x, trackedPrey.y);
			}
		}

		col = originalColor; // Revert to the original color
	}

	/**
	 * Simple utility to calculate distance between two points.
	 */
	protected double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

	/**
	 * Coordinates with nearby Predators to focus on a single Prey.
	 */
	private void coordinatePack(List<PredatorRobot> packPredators) {
		// Example logic: update each pack member to chase the same Prey
		for (PredatorRobot predator : packPredators) {
			if (!predator.isChasing && predator.trackedPrey == null) {
				predator.trackedPrey = this.trackedPrey;
				predator.isChasing = true;
				predator.isStealth = false;
			}
		}
	}

	/**
	 * Perform a roar that affects nearby Prey. This can reduce speed or trigger
	 * another effect.
	 */

	private void performRoar(RobotArena r) {
		final double ROAR_RANGE = 100.0; // Radius of impact
		for (ArenaItem item : r.items) {
			if (item instanceof Prey prey) {
				double dist = distance(x, y, prey.getX(), prey.getY());
				if (dist <= ROAR_RANGE) {
					// slow down prey speed
					if (dist < ROAR_RANGE) {
						Prey p = (Prey) item;
						p.rSpeed = Math.max(p.rSpeed * 0.8, 0.8); // Slow down by 20% was 50% but that was too easy for
																	// predator to catch prey
					}

				}
			}
		}
	}

	@Override
	protected String getStrType() {
		return String.format("Predator (Prey Eaten: %d)", preyEaten);
	}

	/**
	 * Save this Predator's state to a file string if needed.
	 */
	@Override
	public String fileString() {
		return String.format("Predator %.1f %.1f %.1f %c %.1f %.1f %d %d", x, y, rad, col, rAngle, rSpeed, preyEaten,
				roarCooldown);
	}
}
