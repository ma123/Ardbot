package sk.umb.projekty.ardbot.world;

public class RobotDestination {
    protected static float xPoz;
	protected static float yPoz;
    protected static float rw, rh;
    protected static int xPozCm, yPozCm; // pozicia ciela v cm    
    
    public RobotDestination(float xPoz, float yPoz) {
    	xPozCm = (int) (xPoz / rw);
    	yPozCm = (int) (yPoz / rh);
    }

	public void setxPoz(float xPoz) {
		this.xPoz = xPoz;
	}

	public void setyPoz(float yPoz) {
		this.yPoz = yPoz;
	}

	public static float getyPoz() {
		return yPoz;
	}

	public void setRw(float rw) {
		this.rw = rw;
	}

	public void setRh(float rh) {
		this.rh = rh;
	}

	public static int getxPozCm() {
		return xPozCm;
	}

	public static int getyPozCm() {
		return yPozCm;
	}

}
