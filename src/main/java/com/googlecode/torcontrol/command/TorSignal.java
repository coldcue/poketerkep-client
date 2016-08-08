package com.googlecode.torcontrol.command;

import static com.googlecode.torcontrol.TorTokens.CRLF;
import static com.googlecode.torcontrol.TorTokens.SP;

/**
 * Sent from the client to the server.
 * 
 * <p>
 * The server responds with "250 OK" if the signal is recognized (or simply
 * closes the socket if it was asked to close immediately), or "552 Unrecognized
 * signal" if the signal is unrecognized.
 * 
 * @author Denis Migol
 * 
 */
public class TorSignal extends TorCommandBase {

	public static final String KEYWORD = "SIGNAL";

	// Signals:

	/**
	 * Reload: reload config items, refetch directory. (like HUP)
	 */
	public static final String RELOAD = "RELOAD";
	public static final String HUP = "HUP";

	/**
	 * Controlled shutdown: if server is an OP, exit immediately. If it's an OR,
	 * close listeners and exit after 30 seconds. (like INT)
	 */
	public static final String SHUTDOWN = "SHUTDOWN";
	public static final String INT = "INT";

	/**
	 * Dump stats: log information about open connections and circuits. (like
	 * USR1)
	 */
	public static final String DUMP = "DUMP";
	public static final String USR1 = "USR1";

	/**
	 * Debug: switch all open logs to loglevel debug. (like USR2)
	 */
	public static final String DEBUG = "DEBUG";
	public static final String USR2 = "USR2";

	/**
	 * Immediate shutdown: clean up and exit now. (like TERM)
	 */
	public static final String HALT = "HALT";
	public static final String TERM = "TERM";

	/**
	 * Switch to clean circuits, so new application requests don't share any
	 * circuits with old ones. Also clears the client-side DNS cache. (Tor MAY
	 * rate-limit its response to this signal.)
	 */
	public static final String NEWNYM = "NEWNYM";

	/**
	 * Forget the client-side cached IPs for all hostnames.
	 */
	public static final String CLEARDNSCACHE = "CLEARDNSCACHE";

	public static final TorSignal newReload() {
		return new TorSignal(RELOAD);
	}

	public static final TorSignal newShutdown() {
		return new TorSignal(SHUTDOWN);
	}

	public static final TorSignal newDump() {
		return new TorSignal(DUMP);
	}

	public static final TorSignal newDebug() {
		return new TorSignal(DEBUG);
	}

	public static final TorSignal newHalt() {
		return new TorSignal(HALT);
	}

	public static final TorSignal newClearDNSCache() {
		return new TorSignal(CLEARDNSCACHE);
	}

	public static final TorSignal newNewNym() {
		return new TorSignal(NEWNYM);
	}

	/* properties */

	private final String signal;

	/**
	 * 
	 * @param signal
	 */
	public TorSignal(final String signal) {
		super(KEYWORD);
		this.signal = signal;
	}

	/**
	 * 
	 * @return
	 */
	public String getSignal() {
		return signal;
	}

	@Override
	public String toString() {
		// "SIGNAL" SP Signal CRLF
		return KEYWORD + SP + signal + CRLF;
	}
}
