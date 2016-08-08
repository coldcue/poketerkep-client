package com.googlecode.torcontrol.command;

/**
 * 
 * @author Denis Migol
 * 
 */
public class TorClosecircuit extends TorCommandBase {

	public static final String KEYWORD = "CLOSECIRCUIT";

	public TorClosecircuit() {
		super(KEYWORD);
	}
}
