package com.googlecode.torcontrol;

/**
 * 
 * @author Denis Migol
 * 
 */
public final class TorReplyCode {

	/**
	 * Don't let anyone instantiate this class.
	 */
	private TorReplyCode() {
	}

	// 2yz Positive Completion Reply
	// The command was successful; a new request can be started.

	/**
	 * 250 OK
	 */
	public static final int OK = 250;

	/**
	 * 251 Operation was unnecessary [Tor has declined to perform the operation,
	 * but no harm was done.]
	 */
	public static final int OPERATION_WAS_UNNECESSARY = 251;

	// 4yz Temporary Negative Completion reply
	// The command was unsuccessful but might be reattempted later.

	/**
	 * 451 Resource exhausted
	 */
	public static final int RESOURCE_EXHAUSTED = 451;

	// 5yz Permanent Negative Completion Reply
	// The command was unsuccessful; the client should not try exactly
	// that sequence of commands again.

	/**
	 * 500 Syntax error: protocol
	 */
	public static final int SYNTAX_ERROR_PROTOCOL = 500;

	/**
	 * 510 Unrecognized command
	 */
	public static final int UNRECOGNIZED_COMMAND = 510;

	/**
	 * 511 Unimplemented command
	 */
	public static final int UNIMPLEMENTED_COMMAND = 511;

	/**
	 * 512 Syntax error in command argument
	 */
	public static final int SYNTAX_ERROR_IN_COMMAND_ARGUMENT = 512;

	/**
	 * 513 Unrecognized command argument
	 */
	public static final int UNRECOGNIZED_COMMAND_ARGUMENT = 513;

	/**
	 * 514 Authentication required
	 */
	public static final int AUTHENTICATION_REQUIRED = 514;

	/**
	 * 515 Bad authentication
	 */
	public static final int BAD_AUTHENTICATION = 515;

	/**
	 * 550 Unspecified Tor error
	 */
	public static final int UNSPECIFIED_TOR_ERROR = 550;

	/**
	 * 551 Internal error [Something went wrong inside Tor, so that the client's
	 * request couldn't be fulfilled.]
	 */
	public static final int INTERNAL_ERROR = 551;

	/**
	 * 552 Unrecognized entity [A configuration key, a stream ID, circuit ID,
	 * event, mentioned in the command did not actually exist.]
	 */
	public static final int UNRECOGNIZED_ENTITY = 552;

	/**
	 * 553 Invalid configuration value [The client tried to set a configuration
	 * option to an incorrect, ill-formed, or impossible value.]
	 */
	public static final int INVALID_CONFIGURATION_VALUE = 553;

	/**
	 * 554 Invalid descriptor
	 */
	public static final int INVALID_DESCRIPTOR = 554;

	/**
	 * 555 Unmanaged entity
	 */
	public static final int UNMANAGED_ENTITY = 555;

	// 6yz Asynchronous Reply
	// Sent out-of-order in response to an earlier SETEVENTS command.

	/**
	 * 650 Asynchronous event notification
	 */
	public static final int ASYNCHRONOUS_EVENT_NOTIFICATION = 650;
}
