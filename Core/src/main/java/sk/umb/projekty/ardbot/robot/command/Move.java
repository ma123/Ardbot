package sk.umb.projekty.ardbot.robot.command;

import java.io.IOException;

public class Move extends Command {

	private Direction dir1;
	private byte speed1;
	private Direction dir2;
	private byte speed2;
	private WaitType waitType;
	private int wait;
	
	public Move() {
	}

	public Move(Direction dir1, byte speed1, Direction dir2, byte speed2,
			WaitType waitType, int wait) {
		this.dir1 = dir1;
		this.speed1 = speed1;
		this.dir2 = dir2;
		this.speed2 = speed2;
		this.waitType = waitType;
		this.wait = wait;
	}

	public Object process() throws IOException {
		String s = "M" + dir1.getC() + ((int)(speed1 & 0xFF)) + dir2.getC() + ((int)(speed2 & 0xFF)) + waitType.getC() + wait + ".";
		write(s);
		int s1 = readInt();
		int s2 = readInt();
		return new int[] {s1, s2};
	}

	public enum Direction {
		FORWARD('F'), BACKWARD('B'), RELEASE('R');
		private final char c;
		Direction(char c) {
			this.c = c;
		}
		public char getC() {
			return c;
		}
	}

	public enum WaitType {
		TIME('T'), ROTATION('R');
		private final char c;
		WaitType(char c) {
			this.c = c;
		}
		public char getC() {
			return c;
		}
	}

}
