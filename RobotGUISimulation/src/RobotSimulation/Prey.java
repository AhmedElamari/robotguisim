package RobotSimulation;

import java.util.ArrayList;
import java.util.List;

public class Prey extends Robot {
	// Time counter to manage temporary state changes (e.g., camouflage or panic
	// sprint)
	private int stateTimer = 0;
	// Flags to track prey states
	private boolean isCamouflaged = false;
	private boolean isPanicMode = false;
	private boolean isBeingEaten = false;

	/**
	 * Constructs a new Prey item in the arena, using the Robot class as a base.
	 * 
	 * @param ix    Initial x-coordinate.
	 * @param iy    Initial y-coordinate.
	 * @param ir    Radius of the Prey (appearance size).
	 * @param ia    Initial angle.
	 * @param is    Initial speed.
	 * @param arena Reference to the current arena.
	 */
	public Prey(double ix, double iy, double ir, double ia, double is, RobotArena arena) {
		super(ix, iy, ir, ia, is, arena);
		// Choose a default color for the Prey
		this.col = 'g'; // e.g., 'g' for green
	}

	@Override
	protected String getStrType() {
		return "Prey";
	}

	@Override
	public String fileString() {
		return String.format("Prey %.1f %.1f %.1f %c %.1f %.1f", x, y, rad, col, rAngle, rSpeed);
	}

	/**
	 * In checkItem, we look for nearby predators to see if we should enter panic
	 * mode or camouflage, and also optionally look for fellow Prey to flock with.
	 */
	@Override
	public void checkItem(RobotArena r) {
		super.checkItem(r);

		boolean predatorNearby = false;
		double panicDistance = 100.0; // Distance threshold for panic

		boolean preyNearby = false;
		double flockDistance = 80.0; // Distance threshold for flocking

		List<Prey> flockMates = new ArrayList<>();

		for (ArenaItem item : r.items) {
			// Detect predators (example: Predator class from earlier)
			if (item instanceof PredatorRobot) {
				double distToPredator = distance(x, y, item.getX(), item.getY());
				if (distToPredator < panicDistance) {
					predatorNearby = true;
				}
			}
			// Look for other Prey to flock with
			if (item instanceof Prey && item != this) {
				double distToPrey = distance(x, y, item.getX(), item.getY());
				if (distToPrey < flockDistance) {
					preyNearby = true;
					flockMates.add((Prey) item);
				}
			}
		}

		// If a predator is in range, either camouflage or panic
		if (predatorNearby) {
			if (!isCamouflaged && !isPanicMode) {
				// Randomly choose to camouflage or to panic
				if (Math.random() < 0.5) {
					isCamouflaged = true;
					col = 'c'; // e.g., 'c' for camouflage color
					rSpeed = 0.5 * rSpeed; // Slow down or become harder to detect
				} else {
					isPanicMode = true;
					rSpeed = 3.0; // Increase speed significantly
				}
				stateTimer = 100; // Time to remain in this state
			}
		}

		// If we found other Prey nearby, note that we may want to group
		if (preyNearby) {
			// Optionally store references in a list for flock movement in adjustItem
		}
	}

	@Override
	public void adjustItem() {
		// Manage the timer for special states
		if ((isCamouflaged || isPanicMode) && stateTimer > 0) {
			stateTimer--;
		} else if ((isCamouflaged || isPanicMode) && stateTimer <= 0) {
			// Reset states when timer is done
			isCamouflaged = false;
			isPanicMode = false;
			col = 'g'; // Revert to original color
			rSpeed = 2.0; // Example normal speed
		}

		// Incorporate flocking logic: nudge angle towards center of close Prey
		// (optional)
		// Example placeholder: if you store a list of close Prey, average their
		// positions
		// to steer or slightly adjust angle, etc.

		// Incorporate random evasive maneuver
		if (Math.random() < 0.02) {
			double randomOffset = (Math.random() * 180) - 90; // -90 to +90 degrees
			rAngle = (rAngle + randomOffset) % 360;
		}

		// Standard Robot movement
		double radAngle = Math.toRadians(rAngle);
		x += rSpeed * Math.cos(radAngle);
		y += rSpeed * Math.sin(radAngle);
	}

	/**
	 * Calculates distance between two points.
	 */
	private double distance(double x1, double y1, double x2, double y2) {
		double dx = x1 - x2;
		double dy = y1 - y2;
		return Math.sqrt(dx * dx + dy * dy);
	}

	public boolean isBeingEaten() {
		return isBeingEaten;
	}

	public void beingEaten() {
		isBeingEaten = true;
		col = 'o'; // Change color briefly before removal
	}
}