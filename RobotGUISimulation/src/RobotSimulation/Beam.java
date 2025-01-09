
package RobotSimulation;

import java.util.ArrayList;

/**
 * A BeamRobot that extends the Robot class but draws a shorter, "kite-like"
 * beam at its front. This beam is formed by: 1) Two straight lines diverging
 * from the robot's center, 2) A series of small line segments (arc) at the top,
 * creating a closed "kite" shape.
 *
 * Modified to also check for walls or arena boundaries in checkItem(). Assumes
 * that either: (a) RobotArena has getWidth() and getHeight() methods for arena
 * boundaries, or (b) There is a "Wall" item type you can detect similarly to
 * other obstacles.
 */
public class Beam extends Robot {
	private double beamRadius = 60.0; // Overall length of the beam
	private double beamSpread = 45.0; // Total angular spread of the beam in degrees
	private int arcSegments = 8; // Number of segments to approximate the top arc

	private ArrayList<double[]> beamPoints; // Stores points along the arc for detection

	public Beam(double ix, double iy, double ir, double ia, double is, RobotArena arena) {
		super(ix, iy, ir, ia, is, arena);
		beamPoints = new ArrayList<>();
	}

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
	 * boundaries (if using a bounding rectangle) or Wall items.
	 */
	@Override
	public void checkItem(RobotArena r) {
		// Normal collision checks
		super.checkItem(r);

		// 1) Check arena boundary if your RobotArena exposes width/height:
		// We'll do a simple approach: if any beam point is outside the valid area,
		// react.
		// If your environment has a specialized "Wall" item instead, see section (2).
		double arenaWidth = r.getXSize(); // Make sure RobotArena defines these
		double arenaHeight = r.getYSize(); // or adapt to your naming
		for (double[] pt : beamPoints) {
			if (pt[0] < 0 || pt[1] < 0 || pt[0] > arenaWidth || pt[1] > arenaHeight) {
				// We consider this an interception with the "wall"
				// For demo, just turn the robot around:
				rAngle = (rAngle + 180) % 360;
				return;
			}
		}

		// 2) Check for collisions with items (walls, obstacles, or Lights, etc.)
		for (ArenaItem item : r.items) {
			// Skip self
			if (item == this)
				continue;

			// If you have a specialized 'Wall' class instead of bounding rectangle,
			// you could do: if (item instanceof Wall) { ... line-collision logic ... }
			// Or if 'Wall' is stored as a rectangle in item.x/item.y, do line intersection
			// checks.

			for (double[] pt : beamPoints) {
				double dist = distance(pt[0], pt[1], item.x, item.y);
				if (dist < (item.rad + 1)) {
					// If item is a Light, move closer or turn away based on distance
					if (item instanceof Light) {
						if (dist > item.rad * 2) {
							// Turn to face the light
							rAngle = angleToPoint(item.x, item.y);
						} else {
							// Reverse away
							rAngle = (rAngle + 180) % 360;
						}
					} else {
						// For standard obstacle or wall item, just turn around
						rAngle = (rAngle + 180) % 360;
					}
					return; // Done reacting this step
				}
			}
		}
	}

	/**
	 * Calculate angle from this robot's position (x,y) to a target (tx, ty).
	 */
	private double angleToPoint(double tx, double ty) {
		double dx = tx - x;
		double dy = ty - y;
		double radians = Math.atan2(dy, dx);
		double deg = Math.toDegrees(radians);
		return (deg + 360) % 360;
	}

	/**
	 * Utility to measure distance between two points.
	 */
	private double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

	@Override
	protected String getStrType() {
		return "Beam Robot";
	}
}