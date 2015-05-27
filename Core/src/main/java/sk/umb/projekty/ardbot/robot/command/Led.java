package sk.umb.projekty.ardbot.robot.command;

import java.io.IOException;

public class Led extends Command {

	private boolean state;

	public Led(boolean state) {
		this.state = state;
	}

	public Object process() throws IOException {
		String s = "L" + (state?"T":"F");
		write(s);
		return null;
	}

}
