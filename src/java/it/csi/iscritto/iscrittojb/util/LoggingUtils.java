package it.csi.iscritto.iscrittojb.util;

import java.lang.reflect.Method;

import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;

public final class LoggingUtils {
	public static final String TEMPLATE = "[%s::%s] %s ";

	public static final String LOG_BEGIN = "BEGIN";
	public static final String LOG_END = "END";

	public static final String LOG_BEGIN_QUERY = "BEGIN QUERY";
	public static final String LOG_END_QUERY = "END QUERY";

	public static final String LOG_ERROR = "ERROR";

	/* The Constant BEGIN. */
	static final int BEGIN = 1;

	/* The Constant END. */
	static final int END = 2;

	/* The Constant VALUE. */
	static final int VALUE = 3;

	/* The Constant TEST. */
	static final int TEST = 4;

	/* The Constant SIMPLE. */
	static final int SIMPLE = 5;

	protected static Logger log = Logger.getLogger("");

	private LoggingUtils() {
		/* NOP */
	}

	public static String buildMessage(Class<?> clazz, String method, String message) {
		Validate.notNull(clazz);
		Validate.notBlank(method);

		return String.format(TEMPLATE, clazz.getSimpleName(), method, message);
	}

	/**
	 * Stampa.
	 *
	 * @param o
	 *            the o
	 * @param useLog4j
	 *            the use log4j
	 * @param depth
	 *            the depth
	 * @param testName
	 *            the test name
	 */
	public static void stampa(Object o, boolean useLog4j, int depth, String testName) {
		try {
			if (useLog4j) {
				log.info(testName + " BEGIN");
			} else {
				System.out.println(testName + " BEGIN");
			}
			if (o != null) {
				if (o.getClass().isArray()) {
					Object[] a = (Object[]) o;
					stampa(a, useLog4j, depth);
				} else {
					stampa(o, useLog4j, depth);
				}
			}
			if (useLog4j) {
				log.info(testName + " END");
			} else {
				System.out.println(testName + " END");
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	/**
	 * Stampa.
	 *
	 * @param o
	 *            the o
	 * @param useLog4j
	 *            the use log4j
	 * @param depth
	 *            the depth
	 */
	public static void stampa(Object o, boolean useLog4j, int depth) {
		try {
			if (o == null) {
				print(o, null, useLog4j, depth, BEGIN);
			} else {
				if (o instanceof String) {
					print(o, o, useLog4j, depth, SIMPLE);
				} else {
					print(o, null, useLog4j, depth, BEGIN);
					callGetMethods(o, useLog4j, depth + 1);
					print(o, null, useLog4j, depth, END);
				}
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	/**
	 * Prints the.
	 *
	 * @param o
	 *            the o
	 * @param value
	 *            the value
	 * @param useLog4j
	 *            the use log4j
	 * @param depth
	 *            the depth
	 * @param type
	 *            the type
	 * @throws Exception
	 *             the exception
	 */
	private static void print(Object o, Object value, boolean useLog4j, int depth, int type) throws Exception {
		StringBuffer tab = new StringBuffer();
		for (int i = 0; i < depth; i++) {
			tab.append("\t");
		}
		if (o != null) {
			String className = o.getClass().getName();
			switch (type) {
			case BEGIN:
				tab.append(className);
				tab.append(" BEGIN");
				break;
			case END:
				tab.append(className);
				tab.append(" END");
				break;
			case VALUE:
				tab.append(((Method) o).getName());
				tab.append(" == ");
				tab.append(value);
				break;
			case SIMPLE:
				tab.append(o);
				tab.append(" == ");
				tab.append(value);
				break;
			default:
			}
		} else if (type == TEST) {
			tab.append("");
		} else {
			tab.append("Oggetto nullo!!");
		}
		if (useLog4j) {
			log.info(tab);
		} else {
			System.out.println(tab);
		}
	}

	/**
	 * Call get methods.
	 *
	 * @param o
	 *            the o
	 * @param useLog4j
	 *            the use log4j
	 * @param depth
	 *            the depth
	 */
	private static void callGetMethods(Object o, boolean useLog4j, int depth) {
		try {
			Method[] meth = o.getClass().getDeclaredMethods();
			for (int i = 0; i < meth.length; i++) {
				Method thisM = meth[i];
				if (thisM.getName().startsWith("get")) {
					if (!thisM.getName().equals("get")) {
						Object result = thisM.invoke(o, new Object[] {});
						if (result != null && result.getClass().isArray()) {
							Object[] a = (Object[]) result;
							stampa(a, useLog4j, depth);
						} else {
							print(thisM, result, useLog4j, depth, VALUE);
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	/**
	 * Stampa.
	 *
	 * @param o
	 *            the o
	 * @param useLog4j
	 *            the use log4j
	 * @param depth
	 *            the depth
	 * @throws Exception
	 *             the exception
	 */
	public static void stampa(Object[] o, boolean useLog4j, int depth) throws Exception {
		String className = o.getClass().getSimpleName();
		for (int i = 0; i < o.length; i++) {
			stampa(o[i], false, depth);
		}
		if (o.length == 0) {
			System.out.println(className + " vuoto");
		}
	}

}

