package it.csi.iscritto.iscrittojb.util;

import java.util.Iterator;

public final class Joiner {
	public static final String DEFAULT_FORMAT = "%s";

	private final String separator;
	private String format;

	private Joiner(String separator) {
		this.separator = separator;
		this.format = DEFAULT_FORMAT;
	}

	public static Joiner on(String separator) {
		return new Joiner(separator);
	}

	public Joiner withFormat(String format) {
		this.format = format;
		return this;
	}

	public <T> String join(Iterable<T> objs) {
		return join(objs, this.separator, this.format);
	}

	private static <T> String join(Iterable<T> objs, String separator, String format) {
		final StringBuilder sb = new StringBuilder();
		final Iterator<T> iter = objs.iterator();

		if (iter.hasNext()) {
			sb.append(String.format(format, String.valueOf(iter.next())));
			while (iter.hasNext()) {
				sb.append(separator);
				sb.append(String.format(format, String.valueOf(iter.next())));
			}
		}

		return sb.toString();
	}

}
