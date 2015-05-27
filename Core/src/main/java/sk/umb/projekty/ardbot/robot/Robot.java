package sk.umb.projekty.ardbot.robot;

import java.io.IOException;
import java.util.logging.Logger;

import sk.umb.projekty.ardbot.robot.command.Command;

public class Robot {

	private static Logger logger = Logger.getLogger(Robot.class.getPackage().getName());

	private RobotConnection con;

	public Robot(RobotConnection con) {
		this.con = con;
	}

	public void connect() throws IOException {
		con.open();
	}

	public Object execute(Command c) throws IOException {
		c.setRobotConnection(con);
		return c.process();
	}

	public void commTest() throws IOException {
/*		for (int i = 0; i < 100; i++)
			con.getOutputStream().write(65);
		System.out.println("done2");*/
/*		java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
		int c, i = 0;
		while ((c = con.getInputStream().read()) != -1 && i < 5) {
			baos.write(c);
			i++;
		}
		logger.fine("odpoved: " + new String(baos.toByteArray()));*/

/*		//send string
	    OutputStream outStream=streamConnection.openOutputStream();
	    PrintWriter pWriter=new PrintWriter(new OutputStreamWriter(outStream));
	    pWriter.write("Test String from SPP Client\r\n");
	    pWriter.flush();
	    //read response
	    InputStream inStream=streamConnection.openInputStream();
	    BufferedReader bReader2=new BufferedReader(new InputStreamReader(inStream));
	    String lineRead=bReader2.readLine();
	    System.out.println(lineRead);*/
	}

	public void disconnect() throws IOException {
		con.close();
	}

}
