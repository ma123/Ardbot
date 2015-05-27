package sk.umb.projekty.ardbot.robot.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import sk.umb.projekty.ardbot.robot.RobotConnection;

public abstract class Command {

	protected RobotConnection con;

	public void setRobotConnection(RobotConnection con) {
		this.con = con;
	}

	public abstract Object process() throws IOException;

	public void write(byte[] b) throws IOException {
		con.getOutputStream().write(b);
		con.getOutputStream().flush();
	}

	public void write(String s) {
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(con.getOutputStream()));
		pw.write(s);
		pw.flush();
	}

	public String read() throws IOException {  // int n		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int c = 0, i = 0;
		while ((c = con.getInputStream().read()) != -1) { // i < n
			baos.write(c);
			if(c == 10) {
				return new String(baos.toByteArray());
			}
			//i++;
		}		
		return new String(baos.toByteArray());
	}

	public int readInt() throws IOException {
		String s = read();
		s = s.replaceAll("[\n\r]", "");
		
		return Integer.parseInt(s);
	}
}
