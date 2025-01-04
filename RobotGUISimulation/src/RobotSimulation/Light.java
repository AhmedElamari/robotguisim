package RobotSimulation;

public class Light extends ArenaItem {

	public Light(double d, double e, double f) {
		super(d, e, f);
		col = 'y'; // Example: using 'y' for a yellowish light
	}

	@Override
	public void drawItem(MyCanvas mc) {
		mc.showCircle(x, y, rad, col);
	}

	@Override
	public void checkItem(RobotArena r) {
		double approachRange = 150;
		double avoidRange = 30;

		// For a smoother approach/avoid logic, each robot should only react to the
		// single closest Light instead of multiple Lights at once. Here's one way to
		// handle that:
		// 1) Collect this Light's distance to each Robot.
		// 2) If we're the closest Light (or tied for closest) to that Robot,
		// attract/repel it.
		// 3) If there's a tie, add a small random offset so the Robot doesn't get
		// stuck.

		for (ArenaItem item : r.items) {
			if (item instanceof Robot) {
				Robot rob = (Robot) item;
				double distX = rob.getX() - x;
				double distY = rob.getY() - y;
				double distance = Math.sqrt(distX * distX + distY * distY);

				// Find the closest Light(s)
				double myDistance = distance;
				double closestDistance = myDistance;
				int tieCount = 1;
				for (ArenaItem other : r.items) {
					if (other instanceof Light && other != this) {
						double ox = other.getX();
						double oy = other.getY();
						double d = Math.sqrt(Math.pow(ox - rob.getX(), 2) + Math.pow(oy - rob.getY(), 2));
						if (d < closestDistance) {
							closestDistance = d;
							tieCount = 1;
						} else if (Math.abs(d - closestDistance) < 1e-5) {
							tieCount++;
						}
					}
				}

				// If we aren't the single closest Light, do nothing:
				if (Math.abs(myDistance - closestDistance) > 1e-5) {
					continue;
				}

				// If there's a tie, add a small random offset to break it:
				double tieOffset = 0;
				if (tieCount > 1) {
					tieOffset = (Math.random() * 30) - 15;
				}

				// Basic approach/avoid logic:
				if (distance > approachRange) {
					double angleToLight = Math.toDegrees(Math.atan2(y - rob.getY(), x - rob.getX()));
					rob.setAngle((angleToLight + tieOffset) % 360);
				} else if (distance < avoidRange) {
					double angleAway = Math.toDegrees(Math.atan2(rob.getY() - y, rob.getX() - x));
					rob.setAngle((angleAway + tieOffset) % 360);
				}

				// If physically overlapping:
				if (distance < (rob.getRad() + rad)) {
					double newAngle = r.CheckRobotAngle(rob.getX(), rob.getY(), rob.getRad(), rob.getAngle(),
							rob.getID());
					rob.setAngle(newAngle);
				}
			}
		}
	}

	@Override
	public void adjustItem() {
		// Typically nothing if the light is stationary
	}
}