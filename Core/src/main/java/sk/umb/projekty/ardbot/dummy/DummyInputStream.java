package sk.umb.projekty.ardbot.dummy;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public class DummyInputStream extends InputStream {

	private static Logger logger = Logger.getLogger(DummyInputStream.class.getPackage().getName());

	public DummyInputStream() {
	}

	private String response;
	private int responsePos;

	@Override
	public int read() throws IOException {
		if (responsePos>=response.length()) {
			return -1;
		}
		return response.charAt(responsePos++);
	}

	public void setResponse(String response) {
		logger.fine("nastavujem odpoved: " + response);
		this.response = response;
		responsePos = 0;
	}
	
	public String getResponse() {
		return this.response;
	}
}
