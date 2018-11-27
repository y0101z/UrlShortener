package com.nttdata.urlshortener.utilities;

import java.net.URL;
import java.util.HashMap;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
* This class encapsulates all routines needed to generate 8 character long key 
* to construct short URL
*/
public class UrlReducer
{
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	// storage for generated keys
	// key-url map
	private HashMap<String, String> keyUrlMap = new HashMap<String, String>();
	// url-key map to quickly check whether an url is already entered in our system
	private HashMap<String, String> urlKeyMap = new HashMap<String, String>();
	// used to generate URLs for a custom domain name
	private String domain;
	// array for character to number mapping
	private char charBuffer[] = new char[62];;
	// generate random integers
	private Random random = new Random();
	// the key length in URL
	private int KEY_LENGTH = 7;

	/**
	 * Class constructor.
	 * 
	 * @param (domain) (It is a domain name of current application)
	 */
	public UrlReducer(String domain)
	{
		this.domain = cleanUrl(domain);
		populateCharBuffer();
	}

	/**
	 * this method randomly fill s buffer for key generation routine
	 * 
	 * @return (void)
	 */
	private void populateCharBuffer()
	{
		for (int i = 0; i < 62; i++)
		{
			int n = 0;
			if (i < 10)
				n = i + 48;
			else if (i > 9 && i <= 35)
				n = i + 55;
			else
				n = i + 61;
			charBuffer[i] = (char) n;
		}
	}

	/**
	 * This is a wrapper method for all key generation procedures
	 *
	 * @param (longURL) (Original long URL supplied by user)
	 * @return (reducedUrl) (reduced URL)
	 */
	public String reduceUrl(String longURL)
	{
		String reducedUrl = "";
		if (isUrlValid(longURL))
		{
			longURL = cleanUrl(longURL);
			// create a new key if it is not stored in collection
			// otherwise reuse from collection
			reducedUrl = urlKeyMap.containsKey(longURL) ? urlKeyMap.get(longURL) : createKey(longURL);
		}
		return reducedUrl;
	}

	/**
	 * This is a key generating reverse routine for testing purposes only,
	 * It's never used in the application
	 *
	 * @param (shortURL) (reduced URL)
	 * @return (longURL) (Original long URL supplied by user)
	 */
	public String expandURL(String reducedUrl)
	{
		String longURL = "";
		String key = reducedUrl.substring(domain.length() + 1);
		longURL = keyUrlMap.get(key);
		return longURL;
	}

	/**
	 * This is a standard way to validate URL.
	 * Exception is not a very clean way to do validation.
	 * Using exception in program flow control is considered as ANTIPATTERN.
	 *
	 * @param (longUrl) (Original long URL supplied by user)
	 * @return (valid) (true if valid, otherwise false)
	 */
	boolean isUrlValid(String longUrl)
	{
		boolean valid = false;
		try
		{
			new URL(longUrl).toURI();
			valid = true;
		}
		catch (Exception e)
		{
			log.error(e.getMessage());
		}
		return valid;
	}

	/**
	 * This method removes all fancy parts from URL.
	 * leaving 8 characters only
	 *
	 * @param (url) (any URL string)
	 * @return (url) (cleaned url string)
	 */
	String cleanUrl(String url)
	{
		String http = "http://";
		String https = "https://";
		char slash = '/';
		
		if (url.substring(0, http.length()).equals(http))
			url = url.substring(http.length());

		if (url.substring(0, https.length()).equals(https))
			url = url.substring(https.length());

		if (url.charAt(url.length() - 1) == slash)
			url = url.substring(0, url.length() - 1);
		return url;
	}

	/**
	 * This method stores generated key into two cross-maps.
	 *
	 * @param (longURL) (Original long URL supplied by user)
	 * @return (key) (generated key string)
	 */
	private String createKey(String longURL)
	{
		String key = generateKey();
		keyUrlMap.put(key, longURL);
		urlKeyMap.put(longURL, key);
		return key;
	}

	/**
	 * This method generates key randomly taking characters from buffer
	 *
	 * @return (key) (generated key string)
	 */
	private String generateKey()
	{
		String key = "";
		while (true)
		{
			key = "";
			for (int i = 0; i <= KEY_LENGTH; i++)
				key += charBuffer[random.nextInt(62)];
			if (!keyUrlMap.containsKey(key))
				break;
		}
		return key;
	}

	/**************************** all stuff below is just Yuri's playground. One can safely delete it  *********/
	
	private static final String ALPHABET_MAP = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static final int BASE = ALPHABET_MAP.length();

	public static String encode(int indexNumber)
	{
		StringBuilder sb = new StringBuilder();

		while (indexNumber > 0)
		{
			sb.append(ALPHABET_MAP.charAt(indexNumber % BASE));
			indexNumber /= BASE;
		}
		return sb.reverse().toString();
	}

	public static int decode(String str)
	{
		int num = 0;
		int length = str.length();

		for (int i = 0; i < length; i++)
			num = num * BASE + ALPHABET_MAP.indexOf(str.charAt(i));

		return num;
	}

	public static void main(String args[])
	{
		UrlReducer reducer = new UrlReducer("www.tinyurl.com/");
		String encoded = reducer.encode(123);
		int decoded = decode("b9");
		System.out.println("Encoding for 123 is " + encode(123));
		System.out.println("Decoding for b9 is " + decode("b9"));

		String urls[] =
		{ "http://www.geeksforgeeks.org/", "http://www.google.com/", "http://www.google.com", "http://www.yahoo.com", "http://www.yahoo.com/", "http://www.amazon.com", "http://www.amazon.com/page1.php", "http://www.amazon.com/page2.php", "http://www.flipkart.in", "http://www.rediff.com",
				"http://www.techmeme.com", "http://www.techcrunch.com", "http://www.lifehacker.com", "http://www.icicibank.com", "http://asdfasdf.com" };

		for (int i = 0; i < urls.length; i++)
		{
			System.out.println("URL:" + urls[i] + "\tTiny: " + reducer.reduceUrl(urls[i]) + "\tExpanded: " + reducer.expandURL(reducer.reduceUrl(urls[i])));
		}
	}
}