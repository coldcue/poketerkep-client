package com.googlecode.torcontrol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;

import com.googlecode.torcontrol.command.TorCommandBase;

/**
 * 
 * @author Denis Migol
 * 
 */
public class TorControl {

	public static final String DEFAULT_CONTROL_HOST = "127.0.0.1";
	public static final int DEFAULT_CONTROL_PORT = 9051;

	private String controlHost = DEFAULT_CONTROL_HOST;
	private int controlPort = DEFAULT_CONTROL_PORT;
	private String authCode = null;

	private Socket socket = null;
	private Writer out = null;
	private BufferedReader in = null;

	/**
	 * 
	 */
	public TorControl() {
		this(DEFAULT_CONTROL_HOST, DEFAULT_CONTROL_PORT, null);
	}

	/**
	 * 
	 * @param controlHost
	 * @param controlPort
	 * @param authCode
	 */
	public TorControl(String controlHost, int controlPort, String authCode) {
		this.controlHost = controlHost;
		this.controlPort = controlPort;
		this.authCode = authCode;
	}

	public void connect() throws UnknownHostException, IOException {
		if (socket == null) {
			socket = new Socket(controlHost, controlPort);
			out = new OutputStreamWriter(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}
	}

	/**
	 * 
	 * @param response
	 * @return
	 */
	private static final int parseCode(final String response) {
		return Integer.parseInt(response.split(" ", 2)[0]);
	}

	private static String getResponse(final BufferedReader in) throws IOException {
		final StringBuilder ret = new StringBuilder();
		String line = null;
		boolean isExit = false;
		while (!isExit) {
			line = in.readLine();
			ret.append(line + TorTokens.CRLF);
			try {
				parseCode(line);
				isExit = true;
			} catch (Exception e) {
			}
		}
		// final int index = ret.lastIndexOf(TorTokens.CRLF);
		// ret.delete(index, index + TorTokens.CRLF.length());

		return ret.toString();
	}

	/**
	 * 
	 * @param command
	 * @return
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public String executeCommand(final TorCommandBase command) throws UnknownHostException, IOException {
		connect();

		out.write(command.toString());
		out.flush();

		return getResponse(in);
	}

	/**
	 * @param controlHost
	 *            the controlHost to set
	 */
	public void setControlHost(String controlHost) {
		this.controlHost = controlHost;
	}

	/**
	 * @return the controlHost
	 */
	public String getControlHost() {
		return controlHost;
	}

	/**
	 * @param controlPort
	 *            the controlPort to set
	 */
	public void setControlPort(int controlPort) {
		this.controlPort = controlPort;
	}

	/**
	 * @return the controlPort
	 */
	public int getControlPort() {
		return controlPort;
	}

	/**
	 * @param authCode
	 *            the authCode to set
	 */
	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	/**
	 * @return the authCode
	 */
	public String getAuthCode() {
		return authCode;
	}

}
