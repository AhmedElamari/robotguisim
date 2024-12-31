package RobotSimulation;

public class Whisker extends Robot {

	public Whisker(double ix, double iy, double ir, double ia, double is) { {

	private double wAngle;
	private double wLength;

	public Whisker(double ix, double iy, double ir, double ia, double is, double wA, double wL) {
		super(ix, iy, ir, ia, is);
		wAngle = wA;
		wLength = wL;
	}

	@Override
	public void drawItem(MyCanvas mc) {
		double x1 = calcX(rad, rAngle + wAngle);
		double y1 = calcY(rad, rAngle + wAngle);
		double x2 = calcX(rad + wLength, rAngle + wAngle);
		double y2 = calcY(rad + wLength, rAngle + wAngle);
	}

	public void drawWhisker(MyCanvas mc) {
		
	    Line line = new Line(x1, y1, x2, y2);
		line.drawLine(line);
	}
	

	@Override
	public void checkItem(RobotArena r) {
		rAngle = r.CheckRobotAngle(x, y, rad, rAngle, itemID);
	}

	@Override
	public void adjustItem() {
		x = calcX(rSpeed, rAngle);
		y = calcY(rSpeed, rAngle);
	}

	@Override
	protected void setXY(double x2, double y2) {
		x = x2;
		y = y2;
	}

	public double getWAngle() {
		return wAngle;
	}

	public double getWLength() {
		return wLength;
	}

	public void setWAngle(double wA) {
		wAngle = wA;
	}

	public void setWLength(double wL) {
		wLength = wL;
	}

	@Override
    public String toString() {
        return "Whisker: " + super.toString() + " Angle: " + wAngle + " Length: " + wLength;
    }
		super(ix, iy, ir, ia, is);
		// TODO Auto-generated constructor stub
	}

}
