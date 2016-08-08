package com.googlecode.torcontrol.command;

/**
 * <p>
 * Sometimes extensions to the controller protocol break compatibility with
 * older controllers. In this case, whenever possible, the extensions are first
 * included in Tor disabled by default, and only enabled on a given controller
 * connection when the "USEFEATURE" command is given. Once a "USEFEATURE"
 * command is given, it applies to all subsequent interactions on the same
 * connection; to disable an enabled feature, a new controller connection must
 * be opened.
 * 
 * <p>
 * This is a forward-compatibility mechanism; each feature will eventually
 * become a regular part of the control protocol in some future version of Tor.
 * Tor will ignore a request to use any feature that is already on by default.
 * Tor will give a "552" error if any requested feature is not recognized.
 * 
 * <p>
 * Feature names are case-insensitive.
 * 
 * @author Denis Migol
 * 
 */
public class TorUsefeature extends TorCommandBase {

	public static final String KEYWORD = "USEFEATURE";

	public TorUsefeature() {
		super(KEYWORD);
	}
}
