package com.nttdata.urlshortener.utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Predicate;

/*
 * This class is just my old playground for some misc. operations * 
 */
public class Utilities
{
	public static UUID generateUUID()
	{
		return UUID.randomUUID();
	}
	
	public static String getAlphaNumericString(int length)
	{
		String SALTCHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder salt = new StringBuilder();
		Random random = new Random();
		while (salt.length() < length)
			salt.append(SALTCHARS.charAt((int) (random.nextFloat() * SALTCHARS.length())));

		return salt.toString();
	}

	public static int getRandomInteger(int min, int max)
	{
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}

	public static double getRandomDouble(double min, double max)
	{
		return ThreadLocalRandom.current().nextDouble(min, max + 1);
	}

	public static String getRandomFileName(String extension)
	{
		return Utilities.getAlphaNumericString(5) + " " + new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(Calendar.getInstance().getTime()).toString() + extension;
	}

	public static <T> boolean isValidCollection(List<T> collection)
	{
		return (collection != null && !collection.isEmpty() && collection.size() > 0);
	}

	public static Timestamp generateTimestamp()
	{
		return new Timestamp(new Date().getTime());
	}

	public static boolean getRandomBoolean()
	{
		return new Random().nextBoolean();
	}

	public static Timestamp getRandomTimestamp(int startHour, int endHour)
	{
		long millis = (endHour - startHour) * 60 * 60 * 1000;
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, startHour);
		calendar.add(Calendar.MILLISECOND, Utilities.getRandomInteger(0, (int) millis));
		Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
		return timestamp;
	}

	public static void main(String[] args)
	{

		for (int i = 0; i < 20; i++)
		{
		}
	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor)
	{
		Map<Object, String> seen = new ConcurrentHashMap<>();
		return t -> seen.put(keyExtractor.apply(t), "") == null;
	}

	public static Date getSpecificDate(int year, int month, int day, int hours, int minutes, int seconds)
	{
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day, hours, minutes, seconds); // year, month, day of month, hours, minutes and seconds
		Date date = cal.getTime();
		return date;
	}

	public static Timestamp getSpecificTimestamp(int year, int month, int day, int hours, int minutes, int seconds)
	{
		return new Timestamp(getSpecificDate(year, month, day, hours, minutes, seconds).getTime());
	}

	public static Timestamp getTimestamp(String date, String format)
	{
		//date = "02/22/2018";
		//format = "MM/dd/yyyy";
		try
		{
			DateFormat formatter = new SimpleDateFormat(format);
			Timestamp timeStampDate = new Timestamp(formatter.parse(date).getTime());
			return timeStampDate;
		}
		catch (Exception e)
		{
		}
		return null;
	}
}