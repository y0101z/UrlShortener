package com.nttdata.urlshortener.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.urlshortener.model.Link;
import com.nttdata.urlshortener.service.LinkService;
import com.nttdata.urlshortener.utilities.UrlReducer;
import com.nttdata.urlshortener.utilities.Utilities;

@RestController
public class LinkController
{
	@Autowired
	private LinkService service;
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	// click counter holder for short links
	private Map clickCounters = new TreeMap();
	// 1 Year default link entry life span
	private int DEFAULT_LIFE_SPAN = 365;
	private String REDUCER_DOMAIN = "http://yz0101.com/";
	private UrlReducer reducer = new UrlReducer(REDUCER_DOMAIN);

	/************************************ 1. CREATE SHORT URL ************************************************/
	@RequestMapping("/create")
	public ResponseEntity<String> create(@RequestParam String shortUrl, @RequestParam String longUrl, @RequestParam Integer days)
	{
		boolean custom = !shortUrl.isEmpty();
		try
		{
			// decode original URL
			longUrl = new String(longUrl.getBytes("UTF-8"), "ASCII");
			// create short URL if it is not provided
			shortUrl = shortUrl.isEmpty() ? reducer.reduceUrl(longUrl) : shortUrl;
			// check is short URL already exist in DB
			Link link = service.getByShortUrl(shortUrl);

			// if custom short URL is in DB return conflict status
			// return short URL if Link is already in DB for the same original URL
			if (link != null)
				return custom ? ResponseEntity.status(HttpStatus.CONFLICT).build() : ResponseEntity.ok(REDUCER_DOMAIN + shortUrl);

			// create link expiration date if it is not provided
			// or set it to default 365 days (one year)
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, days == null ? DEFAULT_LIFE_SPAN : days);
			// construct new entity
			link = service.create(shortUrl, longUrl, new Date(), calendar.getTime(), 0);
			// return URI of new resource just created
			return ResponseEntity.ok(REDUCER_DOMAIN + shortUrl);
		}
		catch (Exception e)
		{
			log.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
	}

	/************************************ 2. RETRIEVE ORIGINAL URL *******************************************/
	@RequestMapping("/longurl/{shortUrl}")
	public void retrieveLongUrl(@PathVariable String shortUrl, HttpServletResponse response)
	{
		try
		{
			Link link = service.getByShortUrl(shortUrl);

			// todo: this should not update DB every time short link clicked
			// it should be aggregated in memory,
			// and then stored to DB as BULK update on some occasions, maybe on timer, or by capacity
			updateClickCounters(shortUrl);

			// immediately redirect to original long URL
			response.sendRedirect(link.getLongUrl());
		}
		catch (Exception e)
		{
			log.error(e.getMessage());
		}
	}

	/************************************ 3. RETRIEVE STATS (CLICKS) *******************************************/
	@RequestMapping(path = "/stats/{shortUrl}", method = RequestMethod.GET)
	public ResponseEntity<Integer> retrieveStats(@PathVariable String shortUrl)
	{
		try
		{
			int counter = clickCounters.containsKey(shortUrl) ? (int) clickCounters.get(shortUrl) : 0;
			Link link = service.getByShortUrl(shortUrl);
			counter += link.getClicks();
			// return 200 with numbers of clicks
			return ResponseEntity.ok(counter);
		}
		catch (Exception e)
		{
			log.error(e.getMessage());
			// return 404, with null body
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	private void updateClickCounters(String shortUrl)
	{
		if (clickCounters.containsKey(shortUrl))
			clickCounters.replace(shortUrl, (Integer) (clickCounters.get(shortUrl)) + 1);
		else
			clickCounters.put(shortUrl, 1);
	}

	// This scheduled task will read acquired in memory clicks
	// and store them into DB once in 30 min
	@Scheduled(cron = "0/30 * * * * ?")
	private void storeClicks()
	{
		int totalClicks = 0;

		try
		{
			for (Object shortUrl : clickCounters.keySet())
			{
				int clicks = (Integer) clickCounters.get(shortUrl);
				Link link = service.getByShortUrl(shortUrl.toString());
				link.setClicks(link.getClicks() + clicks);
				service.update(link);
				totalClicks += clicks;
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage());
		}

		log.debug(String.format("%d clicks for %d short URLs stored to DB.", totalClicks, clickCounters.size()));
		//remove all stored clicls from memory
		clickCounters.clear();
	}
	
	
	
	/********************************* VARIOUS METHODS FOR DEVELOPMENT AND TESTING *****************************/

	@RequestMapping(path = "/links", method = RequestMethod.GET)
	private ResponseEntity<List<Link>> getAll()
	{
		try
		{
			return ResponseEntity.ok(service.getAll());
		}
		catch (Exception e)
		{
			log.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // return 404, with null body
		}
	}

	@RequestMapping("/delete/{shorturl}")
	public ResponseEntity<Boolean> delete(@PathVariable String shortUrl)
	{
		try
		{
			service.delete(shortUrl);
			return ResponseEntity.ok(true);
		}
		catch (Exception e)
		{
			log.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	// for testing only
	@RequestMapping("/purgedb")
	public ResponseEntity<Boolean> purgedb()
	{
		try
		{
			service.deleteAll();
			return ResponseEntity.ok(true);
		}
		catch (Exception e)
		{
			log.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	// for testing only
	@RequestMapping("/init/{n}")
	private void init(@PathVariable int n)
	{
		String longUrl = "https://stackoverflow.com/questions/9375882/how-i-can-run-my-timertask-everyday-2-pm";
		for (int i = 0; i < n; i++)
		{
			String shortUrl = Utilities.getAlphaNumericString(8);
			clickCounters.put(shortUrl, Utilities.getRandomInteger(10, 100));
			Date expiration = Utilities.getSpecificDate(2018, Utilities.getRandomInteger(11, 12), Utilities.getRandomInteger(1, 28), 
					Utilities.getRandomInteger(1, 12), Utilities.getRandomInteger(1, 60), Utilities.getRandomInteger(1, 60));

			service.create(shortUrl, longUrl, new Date(), expiration, Utilities.getRandomInteger(10, 100));
		}
	}

	// for testing only
	@RequestMapping("/store")
	private void store()
	{
		storeClicks();
	}
	
}
