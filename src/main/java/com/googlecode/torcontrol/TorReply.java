package com.googlecode.torcontrol;

/**
 * 
 * @author Denis Migol
 * 
 */
public class TorReply {

	/**
	 * EndReplyLine = StatusCode SP ReplyLine
	 */
	private String endReplyLine;

	private int statusCode;
	/**
	 * ReplyLine = [ReplyText] CRLF
	 */
	private String replyText;

	private String reply;

	/**
	 * 
	 */
	public TorReply() {
		this(null);
	}

	/**
	 * 
	 * @param reply
	 */
	public TorReply(final String reply) {
		setReply(reply);
	}

	/**
	 * @param reply
	 *            the reply to set
	 */
	public void setReply(final String reply) {
		this.reply = reply;
	}

	/**
	 * @return the reply
	 */
	public String getReply() {
		return reply;
	}
}
