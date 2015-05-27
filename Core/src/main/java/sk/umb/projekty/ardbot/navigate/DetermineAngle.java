package sk.umb.projekty.ardbot.navigate;

public class DetermineAngle {
	 private int xRobot;
	 private int yRobot;
	 private int xDestination;
	 private int yDestination;
	 private float destinationAngle;
     public DetermineAngle(int xRobot, int yRobot, int xDestination, int yDestination) {
    	 this.xRobot = xRobot;
    	 this.yRobot = yRobot;
    	 this.xDestination = xDestination;
    	 this.yDestination = yDestination;
         int deltaX = this.xDestination - this.xRobot;
         int deltaY = this.yDestination - this.yRobot;
         
         destinationAngle = (float) (Math.atan2(deltaY, deltaX) * (180 / Math.PI));
         determineQuadrant();             	 
     }
     
	private void determineQuadrant() {
		 if((destinationAngle > -90) && (destinationAngle < 0)) {  // prvy kvadrant 
   		    destinationAngle = 360 - Math.abs(destinationAngle);
   	     } else {
   	    	if((destinationAngle > 0) && (destinationAngle < 90)) {  // druhy kvadrant 
   	    		
      	    } else {
      	    	 if((destinationAngle > 90) && (destinationAngle < 180)) {  // treti kvadrant 
      	       	    destinationAngle = 180 - (90 - (Math.abs(destinationAngle) % 90));
      	   	     } else {
      	   	        if((destinationAngle < -90) && (destinationAngle > -180)) {  // stvrty kvadrant 
      	       	       destinationAngle = 270 - (90 - (90 - (Math.abs(destinationAngle) % 90)));
      	   	        }    
      	   	     }
      	    }
   	     }	  
	}

	public float getAngle() {
		return destinationAngle;
	}
}
