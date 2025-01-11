package RobotSimulation;

import java.util.ArrayList;

/**
 * A Beam that extends the Robot class but draws a shorter, "diamond-like" beam
 * at its front. This beam is formed by: 1) Two straight lines diverging from
 * the robot's center, 2) A series of small line segments (arc) at the top,
 * creating a closed "diamond" shape.
 *
 * Modified to ensure bouncing behavior is similar to the Robot class by adding
 * slight randomization to avoid repetitive collisions. Also improved general
 * movement randomness to avoid leaning too heavily to one side.
 */
public class Beam extends Robot {
	private double beamRadius = 60.0; // Overall length of the beam
	private double beamSpread = 45.0; // Total angular spread of the beam in degrees
	private int arcSegments = 15; // Number of segments to approximate the top arc

	protected ArrayList<double[]> beamPoints; // Stores points along the arc for detection

	public Beam(double ix, double iy, double ir, double ia, double is, RobotArena arena) {
		super(ix, iy, ir, ia, is, arena);
		beamPoints = new ArrayList<>();
	}

	/**
	 * Draw the beam item. The beam is drawn as two straight lines diverging from
	 * the robot's center, and a series of small line segments at the top to
	 * approximate the arc.
	 */
	@Override
	public void drawItem(MyCanvas mc) {
		super.drawItem(mc);

		beamPoints.clear();

		double leftAngle = rAngle - (beamSpread / 2.0);
		double rightAngle = rAngle + (beamSpread / 2.0);

		// Endpoints of the diverging lines
		double leftX = calcX(beamRadius, leftAngle);
		double leftY = calcY(beamRadius, leftAngle);
		double rightX = calcX(beamRadius, rightAngle);
		double rightY = calcY(beamRadius, rightAngle);

		// Draw the two straight lines
		mc.drawLine(x, y, leftX, leftY);
		mc.drawLine(x, y, rightX, rightY);

		// Approximate the top arc
		double angleStep = beamSpread / arcSegments;
		double currentAngle = leftAngle;
		double startArcX = leftX;
		double startArcY = leftY;

		beamPoints.add(new double[] { startArcX, startArcY });

		for (int i = 0; i < arcSegments; i++) {
			double nextAngle = currentAngle + angleStep;
			double nextArcX = calcX(beamRadius, nextAngle);
			double nextArcY = calcY(beamRadius, nextAngle);

			mc.drawLine(startArcX, startArcY, nextArcX, nextArcY);
			beamPoints.add(new double[] { nextArcX, nextArcY });

			startArcX = nextArcX;
			startArcY = nextArcY;
			currentAngle = nextAngle;
		}
	}

	/**
	 * Modified checkItem that also detects potential collisions with arena
	 * boundaries (if using a bounding rectangle) or Wall items. Bouncing behavior
	 * now includes a slight random offset to match the Robot class approach.
	 */
	@Override
	public void checkItem(RobotArena r) {
		// Delegate standard collision checks (Robot's approach for walls/robots)
		super.checkItem(r);

		// 1) Check arena boundary if RobotArena exposes width/height
		double arenaWidth = r.getXSize();
		double arenaHeight = r.getYSize();
		for (double[] pt : beamPoints) {
			if (pt[0] < 0 || pt[1] < 0 || pt[0] > arenaWidth || pt[1] > arenaHeight) {
				// Collision with boundary; randomize angle similar to Robot bounce
				double randomOffset = (Math.random() * 90) - 45; // ±45° offset
				rAngle = (rAngle + 180 + randomOffset) % 360;
				return;
			}
		}

		// 2) Check for collisions with items (walls, obstacles, lights, etc.)
		for (ArenaItem item : r.items) {
			if (item == this) {
				continue; // skip self
			}
			for (double[] pt : beamPoints) {
				double dist = distance(pt[0], pt[1], item.x, item.y);
				if (dist < (item.rad + 1)) {
					if (item instanceof Light) {
						if (dist > item.rad * 2) {
							// Turn to face the light
							rAngle = angleToPoint(item.x, item.y);
						} else {
							// Reverse away with random offset
							double randomOffset = (Math.random() * 90) - 45;
							rAngle = (rAngle + 180 + randomOffset) % 360;
						}
					} else {
						// Standard obstacle or wall; random offset for bounce
						double randomOffset = (Math.random() * 90) - 45;
						rAngle = (rAngle + 180 + randomOffset) % 360;
					}
					return; // Done reacting for this step
				}
			}
		}
	}

	/**
	 * Overriding adjustItem to improve general movement randomness. Adds a slightly
	 * higher chance and broader range for random angle changes to reduce long,
	 * overly straight paths.
	 */
	@Override
	public void adjustItem() {
		double radAngle = Math.toRadians(rAngle);

		// Move forward by rSpeed
		x += rSpeed * Math.cos(radAngle);
		y += rSpeed * Math.sin(radAngle);

		// Increase randomness: up to 10% chance to change direction each move
		double changeProbability = 0.10;
		if (Math.random() < changeProbability) {
			// Random offset between -30 and +30 degrees
			double randomOffset = (Math.random() * 60) - 30;
			rAngle = (rAngle + randomOffset) % 360;
		}
	}

	/**
	 * Calculate angle from this robot's position (x,y) to a target (tx, ty).
	 */
	protected double angleToPoint(double tx, double ty) {
		double dx = tx - x;
		double dy = ty - y;
		double radians = Math.atan2(dy, dx);
		return (Math.toDegrees(radians) + 360) % 360;
	}

	/**
	 * Utility to measure distance between two points.
	 */
	protected double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

	@Override
	protected String getStrType() {
		return "Beam Robot";
	}

	@Override
	public String fileString() {
		// Make sure there are spaces between all values
		return String.format("Beam Robot %f %f %f %c %f %f", x, y, rad, col, rAngle, rSpeed);
	}
}