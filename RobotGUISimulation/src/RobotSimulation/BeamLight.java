package RobotSimulation;

public class BeamLight extends Beam {

	private Light trackedLight = null;
	private boolean isAtLight = false;

	public BeamLight(double ix, double iy, double ir, double ia, double is, RobotArena arena) {
		super(ix, iy, ir, ia, is, arena);
		col = 'y'; // Set beam color to yellow
	}

	@Override
	public void checkItem(RobotArena r) {
		// Keep standard collision checks from Beam class
		super.checkItem(r);

		// Check if any beam points intersect with a light
		boolean beamDetectsLight = false;
		Light nearestLight = null;
		double closestDist = Double.MAX_VALUE;

		for (double[] pt : beamPoints) {
			for (ArenaItem item : r.items) {
				if (item instanceof Light) {
					double dist = distance(pt[0], pt[1], item.x, item.y);
					if (dist < item.rad + 5) { // Small buffer for beam-light intersection
						beamDetectsLight = true;
						if (dist < closestDist) {
							closestDist = dist;
							nearestLight = (Light) item;
						}
					}
				}
			}
		}

		// Update tracking and movement based on beam detection
		if (beamDetectsLight) {
			trackedLight = nearestLight;
			isAtLight = true;
			rSpeed = 0; // Stop when beam detects light
		} else {
			trackedLight = null;
			isAtLight = false;
			rSpeed = 1; // Resume normal speed when no light detected
		}
	}

	@Override
	public void adjustItem() {
		// Only handle straight-line movement without random direction changes
		if (!isAtLight) {
			double radAngle = Math.toRadians(rAngle);
			x += rSpeed * Math.cos(radAngle);
			y += rSpeed * Math.sin(radAngle);
		}
	}

	@Override
	public void drawItem(MyCanvas mc) {
		super.drawItem(mc);

		// Draw tracking line when beam detects a light
		if (trackedLight != null) {
			mc.drawLine(x, y, trackedLight.getX(), trackedLight.getY());
		}
	}

	protected double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

	@Override
	public String fileString() {
		return String.format("Beam Light %.1f %.1f %.1f %c %.1f %.1f", x, y, rad, col, rAngle, rSpeed);
	}
}