package com.googlecode.torcontrol.command;

/**
 * 
 * @author Denis Migol
 * 
 */
public class TorProtocolinfo extends TorCommandBase {

	public static final String KEYWORD = "PROTOCOLINFO";

	public TorProtocolinfo() {
		super(KEYWORD);
	}
}
