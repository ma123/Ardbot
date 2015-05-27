package sk.kamil.gui.log;

import sk.umb.projekty.ardbot.robot.Robot;

public class LogCommand {

	private Robot robot;
	private String logComm;
	private LogPanel log;
	
	public LogCommand(Robot robot, String logComm, LogPanel log) {
		this.robot = robot;
		this.logComm = logComm;
		this.log = log;
		makeCommand();
	}
	
	private void makeCommand() {
		/*
		switch(logComm) {
            case "U":
            	try {
            		int[] pi = (int[])robot.execute(new ReadUltrasonicSensors());
        			log.sendMessageArduino(Arrays.toString(pi));
        		} catch (IOException e) {
        				e.printStackTrace();
        		}
            break;
            case "K":
            	 try {
					  int compassValue = (Integer)robot.execute(new ReadCompass());
					  log.sendMessageArduino("[" + String.valueOf(compassValue) + "]");
				   } catch (IOException e1) {
					  e1.printStackTrace();
				   }
            break;
            default:
            	log.sendMessageArduino("ERR Command");
        }
        */
	}
}
