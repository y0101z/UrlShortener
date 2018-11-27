package com.nttdata.urlshortener.utilities;

import java.net.URL;
import java.util.HashMap;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	// base URL name
	public UrlReducer(String domain)
	{
		this.domain = cleanUrl(domain);	
		populateCharBuffer();
	}

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
	
	public String reduceUrl(String longURL)
	{
		String reducedUrl = "";
		if (isUrlValid(longURL))
		{
			longURL = cleanUrl(longURL);			
			reducedUrl = urlKeyMap.containsKey(longURL) ? urlKeyMap.get(longURL) : getKey(longURL);
		}
		return reducedUrl;
	}
	
	public String expandURL(String shortURL)
	{
		String longURL = "";
		String key = shortURL.substring(domain.length() + 1);
		longURL = keyUrlMap.get(key);
		return longURL;
	}

	/*
	 * This method is not implemented correctly.
	 * Should take URLs with tracing slashes '/'
	 */
	boolean isUrlValid(String urlString)
	{
		boolean valid = false;
		try
		{
			new URL(urlString).toURI(); 
			valid = true;
		}
		catch (Exception e)
		{
			log.error(e.getMessage());
		}
		return valid;
	}
	
	String cleanUrl(String url)
	{
		if (url.substring(0, 7).equals("http://"))
			url = url.substring(7);

		if (url.substring(0, 8).equals("https://"))
			url = url.substring(8);

		if (url.charAt(url.length() - 1) == '/')
			url = url.substring(0, url.length() - 1);
		return url;
	}

	private String getKey(String longURL)
	{
		String key = createKey();
		keyUrlMap.put(key, longURL);
		urlKeyMap.put(longURL, key);
		return key;
	}

	private String createKey()
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

	//////////////////////////////////////////////////////////////////////////////////////////////////////
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
		{ "http://www.geeksforgeeks.org/", "http://www.google.com/", "http://www.google.com", "http://www.yahoo.com", "http://www.yahoo.com/", "http://www.amazon.com", "http://www.amazon.com/page1.php", "http://www.amazon.com/page2.php", "http://www.flipkart.in", "http://www.rediff.com", "http://www.techmeme.com", "http://www.techcrunch.com", "http://www.lifehacker.com", "http://www.icicibank.com", "http://asdfasdf.com" };

		for (int i = 0; i < urls.length; i++)
		{
			System.out.println("URL:" + urls[i] + "\tTiny: " + reducer.reduceUrl(urls[i]) + "\tExpanded: " + reducer.expandURL(reducer.reduceUrl(urls[i])));
		}
	}
}