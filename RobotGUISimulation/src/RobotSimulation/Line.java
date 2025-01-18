package RobotSimulation;

/**
 * The <code>Line</code> class represents a straight line segment defined by two
 * endpoints and provides methods for performing geometric operations such as
 * calculating distances, finding intersections between lines, and determining
 * the distance from a point to the line.
 *
 * <p>
 * This class also manages the line's color and provides various constructors to
 * initialize a line from individual coordinates or arrays.
 * </p>
 *
 * @author Ahmed Elamari
 * @version 2.0
 */
public class Line {

	/** Coordinates of the line stored as [x1, y1, x2, y2]. */
	private double[] coords;
	/**
	 * A working point used in calculations, for example, to store intersection
	 * coordinates.
	 */
	private double[] xy;
	/** The gradient (slope) of the line (m in the equation y = mx + c). */
	private double gradient;
	/** The offset (y-intercept, c) of the line (in y = mx + c). */
	private double offset;
	/** The character representing the color of the line. */
	private char lineColour;

	/**
	 * Constructs a basic horizontal line from (0, 0) to (1, 0).
	 */
	Line() {
		this(0, 0, 1, 0);
	}

	/**
	 * Constructs a line defined by the start point (x1, y1) and end point (x2, y2).
	 *
	 * @param x1 the x-coordinate of the starting point
	 * @param y1 the y-coordinate of the starting point
	 * @param x2 the x-coordinate of the ending point
	 * @param y2 the y-coordinate of the ending point
	 */
	Line(double x1, double y1, double x2, double y2) {
		coords = new double[] { x1, y1, x2, y2 };
		xy = new double[] { x1, y1 }; // Initialize xy with the first point
	}

	/**
	 * Constructs a line using an array of coordinates.
	 *
	 * @param cs an array containing [x1, y1, x2, y2]
	 */
	Line(double[] cs) {
		this(cs[0], cs[1], cs[2], cs[3]);
	}

	/**
	 * Constructs a line using an integer array of coordinates.
	 *
	 * @param cs an array containing [x1, y1, x2, y2]
	 */
	Line(int[] cs) {
		this(cs[0], cs[1], cs[2], cs[3]);
	}

	/**
	 * Calculates the Euclidean distance between two points (x1, y1) and (x2, y2).
	 *
	 * @param x1 the x-coordinate of the first point
	 * @param y1 the y-coordinate of the first point
	 * @param x2 the x-coordinate of the second point
	 * @param y2 the y-coordinate of the second point
	 * @return the distance between the two points
	 */
	static double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
	}

	/**
	 * Returns the length of the line segment.
	 *
	 * @return the length of the line segment
	 */
	public double lineLength() {
		return distance(coords[0], coords[1], coords[2], coords[3]);
	}

	/**
	 * Returns the working point used in calculations.
	 *
	 * @return an array containing the x and y coordinates of the working point
	 */
	public double[] getXY() {
		return xy;
	}

	/**
	 * Returns the calculated gradient (slope) of the line.
	 *
	 * @return the gradient of the line
	 */
	public double getGradient() {
		return gradient;
	}

	/**
	 * Returns the calculated offset (y-intercept) of the line.
	 *
	 * @return the offset of the line
	 */
	public double getOffset() {
		return offset;
	}

	/**
	 * Gets the current line color represented by a character.
	 *
	 * @return the line color character
	 */
	public char getLineColor() {
		return lineColour;
	}

	/**
	 * Sets the line color to the specified character.
	 *
	 * @param newColour the new color character for the line
	 */
	public void setLineColour(char newColour) {
		lineColour = newColour;
	}

	/**
	 * Calculates the gradient and offset for the line assuming it is not vertical.
	 *
	 * <p>
	 * This method computes the slope (gradient) as the change in y divided by the
	 * change in x and calculates the corresponding offset (y-intercept).
	 * </p>
	 */
	private void calcGradOff() {
		gradient = (coords[3] - coords[1]) / (coords[2] - coords[0]);
		offset = coords[3] - gradient * coords[2];
	}

	/**
	 * Calculates the y-coordinate on the line for a given x-coordinate.
	 *
	 * @param x the x-coordinate
	 * @return the corresponding y-coordinate on the line (rounded)
	 */
	public double calcY(double x) {
		return Math.round(gradient * x + offset);
	}

	/**
	 * Determines whether the line is vertical (i.e., both endpoints have the same
	 * x-coordinate).
	 *
	 * @return {@code true} if the line is vertical; {@code false} otherwise
	 */
	private boolean isVertical() {
		return coords[2] == coords[0];
	}

	/**
	 * Checks whether a value is between two other values.
	 *
	 * @param v  the value to check
	 * @param v1 one boundary value
	 * @param v2 the other boundary value
	 * @return {@code true} if {@code v} is between {@code v1} and {@code v2};
	 *         {@code false} otherwise
	 */
	private boolean isBetween(double v, double v1, double v2) {
		if (v1 > v2)
			return v >= v2 && v <= v1;
		else
			return v >= v1 && v <= v2;
	}

	/**
	 * Checks if a given point (represented as an array with x and y) lies on this
	 * line segment.
	 *
	 * @param xyp an array where xyp[0] is the x-coordinate and xyp[1] is the
	 *            y-coordinate
	 * @return {@code true} if the point is on the line segment; {@code false}
	 *         otherwise
	 */
	public boolean isOnLine(double[] xyp) {
		return isBetween(xyp[0], coords[0], coords[2]) && isBetween(xyp[1], coords[1], coords[3]);
	}

	/**
	 * Determines whether this line intersects with another line, and if so,
	 * calculates the intersection point.
	 *
	 * <p>
	 * If an intersection occurs, the intersection point is stored in the {@code xy}
	 * array.
	 * </p>
	 *
	 * @param otherLine the other line to check for intersection
	 * @return {@code true} if the lines intersect within the bounds of both
	 *         segments; {@code false} otherwise
	 */
	public boolean findintersection(Line otherLine) {
		boolean isIntersecting = true;

		if (isVertical()) { // This line is vertical
			if (otherLine.isVertical()) {
				isIntersecting = false; // Two vertical lines do not intersect in a single point
			} else {
				xy[0] = coords[0]; // Use this line's x-coordinate
				otherLine.calcGradOff(); // Calculate gradient and offset for the other line
				xy[1] = otherLine.calcY(coords[0]); // Determine y-coordinate of intersection
			}
		} else {
			calcGradOff(); // Calculate this line's gradient and offset
			if (otherLine.isVertical()) {
				xy = otherLine.getXY(); // Use the other line's x from its stored point
				xy[1] = calcY(xy[0]); // Calculate y-coordinate using this line's gradient/offset
			} else {
				otherLine.calcGradOff(); // Calculate gradient and offset for the other line
				double ograd = otherLine.getGradient();
				if (Math.abs(ograd - gradient) < 1.0e-5) { // Lines are effectively parallel
					isIntersecting = false;
				} else {
					// Calculate x-coordinate of intersection and round it
					xy[0] = Math.round((otherLine.getOffset() - offset) / (gradient - ograd));
					xy[1] = otherLine.calcY(xy[0]);
				}
			}
		}
		// Verify that the intersection point lies on both line segments
		if (isIntersecting)
			isIntersecting = isOnLine(xy) && otherLine.isOnLine(xy);

		return isIntersecting;
	}

	/**
	 * Calculates the distance from the first endpoint of the line to the
	 * intersection point with another line.
	 *
	 * @param otherLine the other line to check for intersection
	 * @return the distance from this line's starting point to the intersection
	 *         point; or a large value if no intersection occurs
	 */
	public double distintersection(Line otherLine) {
		double ans = 1e8;
		if (findintersection(otherLine))
			ans = distance(xy[0], xy[1], coords[0], coords[1]);
		return ans;
	}

	/**
	 * Calculates the shortest distance from a point (x, y) to this line segment.
	 *
	 * <p>
	 * This method first determines the point on the line (or its extension) that is
	 * closest to the given point. If that point lies on the segment, the distance
	 * is calculated. Otherwise, the distance to the nearest endpoint is used.
	 * </p>
	 *
	 * @param x the x-coordinate of the point
	 * @param y the y-coordinate of the point
	 * @return the shortest distance from the point to the line segment
	 */
	public double distanceFrom(double x, double y) {
		double sdist, sdist2;
		// For vertical lines, the perpendicular meets at the same x; for horizontal
		// lines, at the same y.
		if (coords[0] == coords[2]) { // Vertical line
			xy[0] = coords[0];
			xy[1] = y;
		} else if (coords[1] == coords[3]) { // Horizontal line
			xy[0] = x;
			xy[1] = coords[1];
		} else {
			calcGradOff();
			// Calculate the offset for the perpendicular line (whose gradient is
			// -1/gradient)
			double offset2 = y + x / gradient;
			// Calculate intersection of this line and the perpendicular line
			xy[0] = Math.round((offset2 - offset) / (gradient + 1.0 / gradient));
			xy[1] = Math.round((offset + offset2 * gradient * gradient) / (gradient * gradient + 1.0));
		}
		// If the perpendicular intersection lies on the line segment, use its distance.
		if (isOnLine(xy))
			sdist = distance(x, y, xy[0], xy[1]);
		else {
			// Otherwise, choose the shorter distance to one of the endpoints.
			sdist = distance(x, y, coords[0], coords[1]);
			sdist2 = distance(x, y, coords[2], coords[3]);
			if (sdist2 < sdist)
				sdist = sdist2;
		}
		return sdist;
	}

	/**
	 * Toggles the line color based on the blackout state.
	 *
	 * <p>
	 * If the current color is black ('l') and a blackout is active, the color
	 * changes to white ('w'). Conversely, if it is white ('w') and the blackout is
	 * turned off, it reverts to black ('l').
	 * </p>
	 *
	 * @param blackOut {@code true} if the blackout is active; {@code false}
	 *                 otherwise
	 */
	public void toggleLineColorForBlackOut(boolean blackOut) {
		if (blackOut && lineColour == 'l') {
			lineColour = 'w';
		} else if (!blackOut && lineColour == 'w') {
			lineColour = 'l';
		}
	}

	/**
	 * Draws the line on the provided <code>MyCanvas</code> using its current color.
	 *
	 * @param mc the <code>MyCanvas</code> object on which to draw the line
	 */
	public void drawLine(MyCanvas mc) {
		mc.setStrokeColour(lineColour);
		mc.drawLine(coords[0], coords[1], coords[2], coords[3]);
	}
}
