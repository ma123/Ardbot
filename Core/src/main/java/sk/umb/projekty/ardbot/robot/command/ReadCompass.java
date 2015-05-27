package sk.umb.projekty.ardbot.robot.command;

import java.io.IOException;

public class ReadCompass extends Command {
	public Object process() throws IOException {
		write("K");
		int s1 = readInt();
		return s1;
	}
}
