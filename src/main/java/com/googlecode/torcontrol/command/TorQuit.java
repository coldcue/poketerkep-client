package com.googlecode.torcontrol.command;

/**
 * Tells the server to hang up on this controller connection. This command can
 * be used before authenticating.
 * 
 * @author Denis Migol
 * 
 */
public class TorQuit extends TorCommandBase {

	public static final String KEYWORD = "QUIT";

	public TorQuit() {
		super(KEYWORD);
	}
}
