package RobotSimulation;

import java.util.ArrayList;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.TextAlignment;

/**
 * The <code>MyCanvas</code> class encapsulates a JavaFX canvas for drawing
 * various simulation elements such as images, shapes, text, and lines.
 *
 * <p>
 * This class provides several utility methods to simplify the process of
 * drawing items within the robot simulation arena. It is responsible for
 * converting internal color representations (via characters) to actual JavaFX
 * Color values and drawing shapes accordingly.
 * </p>
 *
 * @author Ahmed Elamari
 * @version 2.0
 */
public class MyCanvas {

	/** The width of the canvas in pixels. */
	int xCanvasSize = 512;
	/** The height of the canvas in pixels. */
	int yCanvasSize = 512;

	/** The GraphicsContext used for drawing on the canvas. */
	GraphicsContext gc;

	/**
	 * Constructs a <code>MyCanvas</code> with the specified GraphicsContext and
	 * canvas dimensions.
	 *
	 * @param g   the GraphicsContext for drawing
	 * @param xcs the width of the canvas in pixels
	 * @param ycs the height of the canvas in pixels
	 */
	public MyCanvas(GraphicsContext g, int xcs, int ycs) {
		gc = g;
		xCanvasSize = xcs;
		yCanvasSize = ycs;
	}

	/**
	 * Returns the width of the canvas.
	 *
	 * @return the canvas width in pixels
	 */
	public int getXCanvasSize() {
		return xCanvasSize;
	}

	/**
	 * Returns the height of the canvas.
	 *
	 * @return the canvas height in pixels
	 */
	public int getYCanvasSize() {
		return yCanvasSize;
	}

	/**
	 * Clears the entire canvas.
	 */
	public void clearCanvas() {
		gc.clearRect(0, 0, xCanvasSize, yCanvasSize);
	}

	/**
	 * Draws an image centered at the specified position with the given size.
	 *
	 * <p>
	 * The specified position (x, y) represents the center of the image.
	 * </p>
	 *
	 * @param i  the Image to draw
	 * @param x  the X coordinate of the image center
	 * @param y  the Y coordinate of the image center
	 * @param sz the size (both width and height) of the image
	 */
	public void drawImage(Image i, double x, double y, double sz) {
		// Calculate top-left position to center the image at (x, y)
		gc.drawImage(i, x - sz / 2, y - sz / 2, sz, sz);
	}

	/**
	 * Converts a character code to the corresponding JavaFX Color.
	 *
	 * <p>
	 * Supported color codes:
	 * <ul>
	 * <li>'y' - Yellow</li>
	 * <li>'w' - White</li>
	 * <li>'r' - Red</li>
	 * <li>'g' - Green</li>
	 * <li>'b' - Blue</li>
	 * <li>'o' - Orange</li>
	 * <li>'l' - Black</li>
	 * </ul>
	 * </p>
	 *
	 * @param c the color code character
	 * @return the corresponding Color object
	 */
	Color colFromChar(char c) {
		Color ans = Color.BLACK;
		switch (c) {
		case 'y':
			ans = Color.YELLOW;
			break;
		case 'w':
			ans = Color.WHITE;
			break;
		case 'r':
			ans = Color.RED;
			break;
		case 'g':
			ans = Color.GREEN;
			break;
		case 'b':
			ans = Color.BLUE;
			break;
		case 'o':
			ans = Color.ORANGE;
			break;
		case 'l':
			ans = Color.BLACK;
			break;
		case 'p':
			ans = Color.PURPLE;
			break;
		}
		return ans;
	}

	/**
	 * Sets the current fill color for drawing shapes.
	 *
	 * @param c the Color to set as the fill color
	 */
	public void setFillColour(Color c) {
		gc.setFill(c);
	}

	/**
	 * Draws a filled circle (ball) at the specified coordinates with the given
	 * radius and color.
	 *
	 * @param x   the X coordinate of the circle's center
	 * @param y   the Y coordinate of the circle's center
	 * @param rad the radius of the circle
	 * @param col the character representing the color for the circle
	 */
	public void showCircle(double x, double y, double rad, char col) {
		setFillColour(colFromChar(col)); // Set the fill color based on the provided character
		showCircle(x, y, rad);
	}

	/**
	 * Draws a filled circle using the current fill color.
	 *
	 * @param x   the X coordinate of the circle's center
	 * @param y   the Y coordinate of the circle's center
	 * @param rad the radius of the circle
	 */
	public void showCircle(double x, double y, double rad) {
		gc.fillArc(x - rad, y - rad, rad * 2, rad * 2, 0, 360, ArcType.ROUND);
	}

	/**
	 * Draws a filled triangle at the specified coordinates with the given radius
	 * and color.
	 * 
	 * @param x
	 * @param y
	 * @param rad
	 * @param col
	 */
	public void showTriangle(double x, double y, double rad, char col) {
		setFillColour(colFromChar(col)); // Set the fill color based on the provided character
		showTriangle(x, y, rad);
	}

	/**
	 * Draws a filled triangle using the current fill color.
	 * 
	 * @param x
	 * @param y
	 * @param rad
	 */
	private void showTriangle(double x, double y, double rad) {
		double[] xpoints = { x, x - rad, x + rad };
		double[] ypoints = { y - rad, y + rad, y + rad };
		gc.fillPolygon(xpoints, ypoints, 3);
	}

	/**
	 * Draws centered text at the specified position.
	 *
	 * <p>
	 * The text is drawn using white color and centered both horizontally and
	 * vertically.
	 * </p>
	 *
	 * @param x the X coordinate for the text's center
	 * @param y the Y coordinate for the text's center
	 * @param s the string to display
	 */
	public void showText(double x, double y, String s) {
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.CENTER);
		gc.setFill(Color.WHITE);
		gc.fillText(s, x, y);
	}

	/**
	 * Draws an integer as text at the specified position.
	 *
	 * @param x the X coordinate for the text's center
	 * @param y the Y coordinate for the text's center
	 * @param i the integer to display
	 */
	public void showInt(double x, double y, int i) {
		showText(x, y, Integer.toString(i));
	}

	/**
	 * Draws a line between two points.
	 *
	 * @param d the starting X coordinate
	 * @param e the starting Y coordinate
	 * @param f the ending X coordinate
	 * @param g the ending Y coordinate
	 */
	public void drawLine(double d, double e, double f, double g) {
		gc.strokeLine(d, e, f, g);
	}

	/**
	 * Sets the background color of the canvas and fills it.
	 *
	 * @param c the character representing the background color
	 */
	public void setBackgroundColor(char c) {
		gc.setFill(colFromChar(c));
		gc.fillRect(0, 0, xCanvasSize, yCanvasSize);
	}

	/**
	 * Draws a filled rectangle at the specified position, dimensions, and color.
	 *
	 * @param x      the X coordinate of the rectangle's upper-left corner
	 * @param y      the Y coordinate of the rectangle's upper-left corner
	 * @param width  the width of the rectangle
	 * @param height the height of the rectangle
	 * @param c      the character representing the rectangle's fill color
	 */
	public void drawRect(double x, double y, double width, double height, char c) {
		gc.setFill(colFromChar(c));
		gc.fillRect(x, y, width, height);
	}

	/**
	 * Sets the stroke (line) width.
	 *
	 * @param i the new line width
	 */
	public void setLineWidth(int i) {
		gc.setLineWidth(i);
	}

	/**
	 * Sets the stroke color based on a character code.
	 *
	 * @param c the color code character
	 */
	public void setStrokeColour(char c) {
		gc.setStroke(colFromChar(c));
	}

	/**
	 * Fills a polygon defined by a list of point arrays.
	 *
	 * <p>
	 * Each element in the <code>beamPoints</code> list is a 2-element array
	 * representing the X and Y coordinates of a point. The polygon is filled with
	 * the specified color.
	 * </p>
	 *
	 * @param beamPoints a list of points defining the polygon vertices
	 * @param col        the character representing the fill color for the polygon
	 */
	public void fillPolygon(ArrayList<double[]> beamPoints, char col) {
		setFillColour(colFromChar(col));
		double[] xpoints = new double[beamPoints.size()];
		double[] ypoints = new double[beamPoints.size()];

		for (int i = 0; i < beamPoints.size(); i++) {
			xpoints[i] = beamPoints.get(i)[0];
			ypoints[i] = beamPoints.get(i)[1];
		}
		gc.fillPolygon(xpoints, ypoints, beamPoints.size());
	}

	/**
	 * Draws a circle with a specified radius and color at a given
	 * 
	 * @param x
	 * @param y
	 * @param rad
	 * @param fxColor
	 */
	public void showCircle(double x, double y, double rad, Color fxColor) {
		gc.setFill(fxColor);
		gc.fillArc(x - rad, y - rad, rad * 2, rad * 2, 0, 360, ArcType.ROUND);

	}
}
