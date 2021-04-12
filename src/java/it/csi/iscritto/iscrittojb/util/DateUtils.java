package it.csi.iscritto.iscrittojb.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public final class DateUtils {
	public static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
	public static final String NAO_DATE_FORMAT = "yyyyMMdd";
	public static final String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	public static final String UTC = "UTC";

	private DateUtils() {
		/* NOP */
	}

	public static Date toDate(Timestamp timestamp) {
		return timestamp == null ? null : new Date(timestamp.getTime());
	}

	public static Date toDate(String date, String format) throws ParseException {
		Validate.notBlank(format);

		if (StringUtils.isBlank(date)) {
			return null;
		}

		return new Date(getDateFormat(format).parse(date).getTime());
	}

	public static String toStringDate(Date date, String format) {
		Validate.notBlank(format);

		if (date == null) {
			return null;
		}

		return getDateFormat(format).format(date);
	}

	public static String changeFormat(String date, String fromFormat, String toFormat) throws ParseException {
		Validate.notBlank(fromFormat);
		Validate.notBlank(toFormat);

		if (StringUtils.isBlank(date)) {
			return null;
		}

		return toStringDate(toDate(date, fromFormat), toFormat);
	}

	public static String toIso8601Format(Date date, String timeZone) {
		Validate.notBlank(timeZone);

		if (date == null) {
			return null;
		}

		return getIso8601Format(timeZone).format(date);
	}

	public static Date addDays(Date date, int days) {
		Validate.notNull(date);

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);

		return cal.getTime();
	}

	private static DateFormat getDateFormat(String format) {
		Validate.notBlank(format);

		DateFormat df = new SimpleDateFormat(format);
		df.setLenient(false);

		return df;
	}

	private static DateFormat getIso8601Format(String timeZone) {
		DateFormat df = getDateFormat(ISO_8601_FORMAT);
		df.setTimeZone(TimeZone.getTimeZone(timeZone));

		return df;
	}

}
