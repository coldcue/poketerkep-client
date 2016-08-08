package com.googlecode.torcontrol.command;

import static com.googlecode.torcontrol.TorTokens.CRLF;
import static com.googlecode.torcontrol.TorTokens.SP;

import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

/**
 * TODO
 * 
 * @author Denis Migol
 * 
 */
public class TorGetinfo extends TorCommandBase {

	public static final String KEYWORD = "GETINFO";

	//

	public static final String ADDRESS = "address";
	public static final String IP_TO_COUNTRY = "ip-to-country";

	private Properties properties;

	public TorGetinfo() {
		this(new Properties());
	}

	public TorGetinfo(final Properties properties) {
		super(KEYWORD);
		this.setProperties(properties);
	}

	/**
	 * @param properties
	 *            the properties to set
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public void setProperty(final String key, final String value) {
		this.properties.setProperty(key, value);
	}

	public String setProperty(final String key) {
		return (String) this.properties.setProperty(key, "");
	}

	/**
	 * @return the properties
	 */
	public Properties getProperties() {
		return properties;
	}

	private static final String toString(final Properties properties) {
		final StringBuilder ret = new StringBuilder();
		final Set<Entry<Object, Object>> entrySet = properties.entrySet();
		for (final Entry<Object, Object> entry : entrySet) {
			final String key = (String) entry.getKey();
			final String value = (String) entry.getValue();

			ret.append(SP + key + (value == null || value.length() == 0 ? "" : "/" + value));
		}
		return ret.toString();
	}

	@Override
	public String toString() {
		// "GETINFO" 1*(SP keyword) CRLF
		return KEYWORD + toString(this.properties) + CRLF;
	}

}
