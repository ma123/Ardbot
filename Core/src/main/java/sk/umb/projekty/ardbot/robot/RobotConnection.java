package sk.umb.projekty.ardbot.robot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface RobotConnection {

	void open() throws IOException;

	InputStream getInputStream();

	OutputStream getOutputStream();

	void close() throws IOException;

}
