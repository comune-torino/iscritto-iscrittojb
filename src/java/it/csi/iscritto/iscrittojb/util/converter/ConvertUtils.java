package it.csi.iscritto.iscrittojb.util.converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ConvertUtils {
	private ConvertUtils() {
		/* NOP */
	}

	public static BigDecimal toBigDecimal(Integer value) {
		return value == null ? null : BigDecimal.valueOf(value);
	}

	public static BigDecimal toBigDecimal(Double value) {
		return value == null ? null : BigDecimal.valueOf(value);
	}

	public static Integer toInteger(BigDecimal value) {
		return value == null ? null : value.intValue();
	}

	public static Integer toInteger(String value) {
		return value == null ? null : Integer.valueOf(value);
	}

	public static String toStringValue(BigDecimal value) {
		return value == null ? null : value.toString();
	}

	public static String toStringValue(Integer value) {
		return value == null ? null : value.toString();
	}

	public static String toStringValue(Double value) {
		return value == null ? null : value.toString();
	}

	public static Double toDouble(BigDecimal value) {
		return value == null ? null : value.doubleValue();
	}

	public static Long toLong(BigDecimal value) {
		return value == null ? null : value.longValue();
	}

	public static String toSN(Boolean value) {
		return Boolean.TRUE.equals(value) ? "S" : "N";
	}

	public static String toSNN(Boolean value) {
		return value == null ? null : toSN(value);
	}

	public static Boolean fromSN(String value) {
		return value == null ? null : "S".equalsIgnoreCase(value);
	}

	public static String toUpper(String value) {
		return value == null ? null : value.toUpperCase();
	}

	public static <T> List<T> toList(T[] values) {
		List<T> result = new ArrayList<>();
		if (values != null) {
			result.addAll(Arrays.asList(values));
		}

		return result;
	}

}
