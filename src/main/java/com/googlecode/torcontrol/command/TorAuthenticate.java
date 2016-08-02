package com.googlecode.torcontrol.command;

import static com.googlecode.torcontrol.TorTokens.CRLF;
import static com.googlecode.torcontrol.TorTokens.SP;

/**
 * Sent from the client to the server.
 * 
 * <p>
 * The server responds with "250 OK" on success or "515 Bad authentication" if
 * the authentication cookie is incorrect. Tor closes the connection on an
 * authentication failure.
 * 
 * <p>
 * The format of the 'cookie' is implementation-dependent; see 5.1 below for
 * information on how the standard Tor implementation handles it.
 * 
 * <p>
 * Before the client has authenticated, no command other than PROTOCOLINFO,
 * AUTHENTICATE, or QUIT is valid. If the controller sends any other command, or
 * sends a malformed command, or sends an unsuccessful AUTHENTICATE command, or
 * sends PROTOCOLINFO more than once, Tor sends an error reply and closes the
 * connection.
 * 
 * <p>
 * To prevent some cross-protocol attacks, the AUTHENTICATE command is still
 * required even if all authentication methods in Tor are disabled. In this
 * case, the controller should just send "AUTHENTICATE" CRLF.
 * 
 * @author Denis Migol
 * 
 */
public class TorAuthenticate extends TorCommandBase {

	public static final String KEYWORD = "AUTHENTICATE";

	private String code;

	/**
	 * 
	 */
	public TorAuthenticate() {
		this(null);
	}

	/**
	 * 
	 * @param code
	 */
	public TorAuthenticate(final String code) {
		super(KEYWORD);
		this.code = code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(final String code) {
		this.code = code;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		final String code = this.getCode();
		// "AUTHENTICATE" [ SP 1*HEXDIG / QuotedString ] CRLF
		return KEYWORD + SP + (code == null || code.length() == 0 ? "" : code) + CRLF;
	}
}
