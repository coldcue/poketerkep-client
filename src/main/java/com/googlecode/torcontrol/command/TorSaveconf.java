package com.googlecode.torcontrol.command;

import static com.googlecode.torcontrol.TorTokens.CRLF;

/**
 * Instructs the server to write out its config options into its torrc. Server
 * returns "250 OK" if successful, or "551 Unable to write configuration to
 * disk" if it can't write the file or some other error occurs.
 * 
 * <p>
 * See also the "getinfo config-text" command, if the controller wants to write
 * the torrc file itself.
 * 
 * @author Denis Migol
 * 
 */
public class TorSaveconf extends TorCommandBase {

	public static final String KEYWORD = "SAVECONF";

	/**
	 * 
	 */
	public TorSaveconf() {
		super(KEYWORD);
	}

	@Override
	public String toString() {
		return KEYWORD + CRLF;
	}

}
