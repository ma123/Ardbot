package sk.umb.projekty.ardbot.robot.command;

import java.io.IOException;

public class ReadUltrasonicSensors extends Command {

	public Object process() throws IOException {
		write("U");
		int s1 = readInt();
		int s2 = readInt();
		int s3 = readInt();
		int s4 = readInt();
		return new int[] {s1, s2, s3, s4};
	}

}
