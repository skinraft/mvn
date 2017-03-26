package util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Stringx {
	/**
	 * Unix时间转java
	 * 
	 */
	public static String unixToJava(String timestampString, String formats) {
		if (formats.isEmpty())
			formats = "yyyy-MM-dd HH:mm:ss";
		Long timestamp = Long.parseLong(timestampString) * 1000;
		String date = new SimpleDateFormat(formats, Locale.CHINA).format(new Date(timestamp));
		return date;
	}

	/***
	 * 取得当前的时间
	 * 
	 * @return
	 */
	public static String getCurrentTime() {
		Date date = Calendar.getInstance().getTime();
		SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String today = simpleFormat.format(date);
		return today;
	}

}
