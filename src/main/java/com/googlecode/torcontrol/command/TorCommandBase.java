package com.googlecode.torcontrol.command;

/**
 * 
 * @author Denis Migol
 * 
 */
public abstract class TorCommandBase {

	private final String Keyword;

	/**
	 * 
	 * @param Keyword
	 */
	public TorCommandBase(final String Keyword) {
		this.Keyword = Keyword;
	}

	/**
	 * @return the keyword
	 */
	public String getKeyword() {
		return Keyword;
	}

	@Override
	public String toString() {
		throw new UnsupportedOperationException("Not implemented yet");
	}
}
