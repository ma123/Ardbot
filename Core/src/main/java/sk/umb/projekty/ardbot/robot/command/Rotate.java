package sk.umb.projekty.ardbot.robot.command;

import java.io.IOException;

public class Rotate extends Command {
	private int angle;

	public Rotate(int angle2) {
		this.angle = angle2;
	}

	public Object process() throws IOException {
		String s = "R" + angle + ".";
		write(s);
		return null;
	}
}
